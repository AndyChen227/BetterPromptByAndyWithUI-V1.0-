# BetterPrompt v3.0

A prompt optimizer built in Java — type a rough prompt, and the tool compresses it using a rule-based strategy, measures the token savings, and restructures it into something that gets better responses from AI assistants like ChatGPT or Claude.

Built by Andy | UW Sophomore | Spring 2026

---

## Version History

| Version | Status | Key Addition |
|---|---|---|
| **V1** | ✅ Complete | Domain detection + structured prompt templates |
| **V2** | ✅ Complete | Strategy Pattern, compression strategies, token counter |
| **V3** | ✅ Complete | Spring Boot Web UI + JSON-backed rule system |

---

## What It Does

Most people type short, vague prompts like:

> *"fix my code"* or *"write something about space"*

AI tools respond much better when prompts are specific, structured, and give the model a clear role to play.

BetterPrompt compresses your prompt using a strategy you choose, shows you how many tokens were saved, then wraps the result in a structured template.

**Example:**

| | Prompt |
|---|---|
| **Before** | `Can you please explain recursion and show me a Java example` |
| **After** | `Act as an expert software engineer. Task: Explain recursion and show me a Java example. Please provide: 1. A clear explanation...` |
| **Tokens** | `~14 → ~11 (21% reduction)` |

---

## How It Works

V3 runs a six-step pipeline every time you enter a prompt:

```
Your input
    │
    ▼
1. Choose Strategy  — pick one of 4 compression strategies
    │
    ▼
2. Compress         — strategy removes/replaces words using JSON rule files
    │
    ▼
3. Count Tokens     — estimate before/after token count (word count × 1.3)
    │
    ▼
4. Detect Domain    — keyword matching assigns CODE, WRITING, or GENERAL
    │
    ▼
5. Improve          — domain rules add missing hints (language, tone, etc.)
    │
    ▼
6. Template         — wrap in a structured prompt for that domain
    │
    ▼
Optimized output  ←  displayed side-by-side in the Web UI
```

Steps 1–3 were added in V2. Steps 4–6 carried over from V1. V3 upgrades all strategies to load rules from JSON files at runtime, and adds the Spring Boot Web UI.

---

## Compression Strategies

Each strategy implements the `CompressionStrategy` interface (Strategy Pattern). You pick one per run via the Web UI or terminal menu.

All strategies load their rules from JSON files in `src/main/resources/rules/` at startup via `RuleLoader.java`. This means you can update, extend, or swap rule lists without touching any Java code.

| # | Strategy | What It Does | Rule File | Best For |
|---|---|---|---|---|
| 1 | **Remove Redundancy** | Strips filler phrases: "can you please", "I was wondering", "could you help me" | `redundancy_phrases.json` | Polite but wordy prompts |
| 2 | **Shorten Words** | Swaps verbose words: "utilize"→"use", "demonstrate"→"show", "implement"→"build" | `shorten_words_rules.json` | Formal/academic writing style |
| 3 | **Remove Context** | Drops self-intro sentences matching regex patterns: "I am a student", "I am new to" | `remove_context_rules.json` | Prompts with unnecessary background |
| 4 | **Split Question** | Splits compound prompts on "and" into "Question 1:" / "Question 2:" | *(inline logic)* | Two-part questions in one prompt |

### Rule File Formats

**`redundancy_phrases.json`** — flat list of strings to remove:
```json
["can you please", "could you please", "i was wondering if you could", ...]
```

**`shorten_words_rules.json`** — array of from/to replacement pairs:
```json
{ "replacements": [{ "from": "utilize", "to": "use" }, ...] }
```

**`remove_context_rules.json`** — array of regex patterns to match and drop:
```json
{ "patterns": [{ "pattern": "(?i)^i am a (student|beginner).*" }, ...] }
```

---

## Token Counter

The `TokenCounter` uses the formula **word count × 1.3**, a standard industry approximation when you don't have access to the real tokenizer.

Why tokens matter: AI APIs charge by token and have context-window limits. Fewer tokens means faster responses, lower cost, and more room for the model's answer.

In V3, the Web UI displays token counts for the **user input only** (before compression vs. after compression), making the savings easy to read at a glance.

```
Original:    ~14 tokens
Compressed:  ~11 tokens
Saved:        3 tokens (21% reduction)
```

Note: if a strategy adds words (e.g. SplitQuestion restructures into labeled parts), the token count may increase. The UI shows this as a positive "added" value rather than a negative savings.

---

## The Three Domains

Domain detection uses keyword counting with whole-word regex matching (so "class" doesn't match inside "classical").

| Domain | Detected When | Template Focus |
|---|---|---|
| **Code** | Keywords like `bug`, `function`, `python`, `debug`, `compile` | Role: expert engineer. Asks for code, comments, edge cases, examples |
| **Writing** | Keywords like `essay`, `draft`, `tone`, `rewrite`, `grammar` | Role: professional editor. Asks for structure, tone, grammar |
| **General** | No clear winner between the two above | Role: knowledgeable assistant. Asks for direct answer + context |

After detecting the domain, `PromptOptimizer` applies a quick rule-based improvement before templating:

- **CODE** — appends a language hint if no programming language is mentioned
- **WRITING** — appends a tone reminder if no audience/tone is specified
- **GENERAL** — adds a question mark if the prompt looks like a question but lacks one

---

## Project Structure

```
src/main/java/
│
├── org/example/                        ← V1 core (domain detection + templates)
│   ├── Domain.java                     # Enum: CODE, WRITING, GENERAL
│   ├── DomainAnalyzer.java             # Keyword counting with whole-word regex matching
│   ├── PromptTemplate.java             # Builds the structured prompt per domain
│   ├── PromptOptimizer.java            # Coordinates the full pipeline (clean → detect → improve → template)
│   └── Main.java                       # Entry point (terminal mode)
│
├── com/promptopt/                      ← V2 additions
│   ├── RuleLoader.java                 # Loads JSON rule files from classpath at runtime
│   ├── TokenCounter.java               # Estimates tokens (word count × 1.3)
│   └── strategy/
│       ├── CompressionStrategy.java    # Interface — the Strategy Pattern contract
│       ├── RemoveRedundancyStrategy.java  # Loads phrases from redundancy_phrases.json
│       ├── ShortenWordsStrategy.java      # Loads from/to pairs from shorten_words_rules.json
│       ├── RemoveContextStrategy.java     # Loads regex patterns from remove_context_rules.json
│       └── SplitQuestionStrategy.java
│
└── com/promptopt/web/                  ← V3 additions (Spring Boot + Thymeleaf)
    ├── PromptController.java           # GET / and POST /optimize, passes results to template
    ├── OptimizeRequest.java            # Form binding object
    └── OptimizeResponse.java           # Response model for the view

src/main/resources/
├── rules/
│   ├── redundancy_phrases.json         # Filler phrases for RemoveRedundancyStrategy
│   ├── shorten_words_rules.json        # Verbose→concise word pairs for ShortenWordsStrategy
│   └── remove_context_rules.json       # Regex patterns for RemoveContextStrategy
└── templates/
    └── index.html                      # Thymeleaf template (Web UI)
```

---

## How to Run

**Requirements:** Java 21, Maven

### Option 1 — Web UI (V3, recommended)
```bash
mvn spring-boot:run
```
Then open [http://localhost:8080](http://localhost:8080) in your browser.

The web UI shows:
- Input box for your prompt
- Dropdown to select a compression strategy
- Side-by-side display: original vs. compressed vs. domain-templated output
- Token count comparison (original vs. compressed)
- Detected domain label

### Option 2 — Terminal (V2 mode)
```bash
mvn compile exec:java -Dexec.mainClass="org.example.Main"
```

### Option 3 — IntelliJ IDEA
Open the project, then click the green **Run** button on `Main.java`.

### Sample Terminal Session

```
============================================================
   BetterPrompt v3.0 — Compression + Token Awareness
============================================================

Enter your prompt (or 'exit' to quit):
> Can you please explain recursion and show me a Java example

Choose a compression strategy:
  1. Remove Redundancy (filler phrases)
  2. Shorten Words (verbose → concise)
  3. Remove Context (self-intro sentences)
  4. Split Question (compound → two labeled parts)
Pick a strategy (1-4): 1

Strategy applied: Remove Redundancy (filler phrases)

--- Token Count ---
  Original:   ~14 tokens
  Compressed: ~11 tokens
  Saved:      3 tokens (21% reduction)
-------------------

------------------------------------------------------------
  DETECTED DOMAIN: Code / Programming
------------------------------------------------------------

[BEFORE — your original prompt]
Can you please explain recursion and show me a Java example

[AFTER — compressed + domain-templated prompt]
Act as an expert software engineer.

Task: Explain recursion and show me a Java example.

Please provide:
1. A clear explanation of the approach
2. Well-commented code with meaningful variable names
3. Edge cases or potential errors to watch for
4. A brief example of how to run or test the solution
------------------------------------------------------------
Tip: copy the AFTER text and paste it into ChatGPT, Claude, etc.
```

---

## V3 Roadmap

### 1. ✅ Web UI (Complete)
Spring Boot + Thymeleaf interface with:
- Input box and strategy selector
- Three-column output: original / compressed / optimized
- Token count comparison
- Detected domain label

### 2. ✅ JSON Rule System (Complete)
All strategy rule lists are now externalized to JSON files and loaded at runtime by `RuleLoader.java`:
- `redundancy_phrases.json` — 26 filler phrases for RemoveRedundancy
- `shorten_words_rules.json` — 8 verbose→concise word pairs for ShortenWords
- `remove_context_rules.json` — 7 regex patterns for RemoveContext

New rules can be added by editing the JSON files — no Java changes needed.

### 3. ⬜ Larger Open-Source Corpora
Replace the current small lists with larger open-source wordlists:
- Filler phrase corpus (from academic NLP datasets)
- Verbose-to-concise word pairs (from WordNet or similar)
- Self-introduction patterns corpus

### 4. ⬜ LLM-as-a-Judge Evaluation
Automatically measure compression quality:
- Send original prompt to Claude Sonnet → get Answer A
- Send compressed prompt to Claude Sonnet → get Answer B
- Ask Claude to score both answers (1–10)
- Record scores + token counts for every test

### 5. ⬜ Efficiency Index
A single metric to determine if compression is worth it:

```
Efficiency Index = Quality improvement% / Token cost increase%
```

- Index > 1 → compression is worth it
- Index < 1 → compression costs more than it saves

### 6. ⬜ Results Export
Save all test results to `results.csv` for analysis.

---

## Tech Stack

| | |
|---|---|
| Language | Java 21 |
| Build tool | Maven |
| Web framework | Spring Boot 3.2.5 |
| Templating | Thymeleaf |
| Frontend | HTML / CSS / JS (served via Spring Boot) |
| Design patterns | Strategy Pattern (compression), Composition (optimizer) |
| Rule format | JSON (loaded via Jackson at runtime) |
| External APIs | None (V1–V3 core), Claude API planned for evaluation phase |

---

*V1 focused on Java OOP fundamentals: enums, composition, switch expressions, text blocks.*
*V2 adds the Strategy Pattern and interfaces — the same principles used in large production codebases.*
*V3 adds a Spring Boot web UI and externalizes all strategy rules to JSON files, making the system easy to extend without touching Java code.*
