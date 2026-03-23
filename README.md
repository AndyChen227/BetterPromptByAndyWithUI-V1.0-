# BetterPrompt v3.0 (In Progress)

A prompt optimizer built in Java — type a rough prompt, and the tool compresses it, measures the token savings, and restructures it into something that gets better responses from AI assistants like ChatGPT or Claude.

Built by Andy | UW Sophomore | Spring 2026

---

## Version History

| Version | Status | Key Addition |
|---|---|---|
| **V1** | ✅ Complete | Domain detection + structured prompt templates |
| **V2** | ✅ Complete | Strategy Pattern, compression strategies, token counter |
| **V3** | 🔄 In Progress | Web UI (done), Open-Source Corpus Integration (in progress) |

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

V2/V3 runs a six-step pipeline every time you enter a prompt:

```
Your input
    │
    ▼
1. Choose Strategy  — pick one of 4 compression strategies
    │
    ▼
2. Compress         — strategy removes/replaces words to shorten the prompt
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
Optimized output  ←  try another strategy on the same prompt, or move on
```

Steps 1–3 were added in V2. Steps 4–6 carried over from V1. V3 adds a Web UI and corpus-backed strategies.

---

## Compression Strategies

Each strategy implements the `CompressionStrategy` interface (Strategy Pattern).
You pick one per run; you can re-run with a different strategy on the same prompt.

| # | Strategy | What It Does | Best For |
|---|---|---|---|
| 1 | **Remove Redundancy** | Strips filler phrases: "can you please", "I was wondering", "could you help me" | Polite but wordy prompts |
| 2 | **Shorten Words** | Swaps verbose words: "utilize"→"use", "demonstrate"→"show", "implement"→"build" | Formal/academic writing style |
| 3 | **Remove Context** | Drops self-intro sentences: "I am a student", "I am new to", "as a beginner" | Prompts with unnecessary background |
| 4 | **Split Question** | Splits compound prompts on "and" into "Question 1:" / "Question 2:" | Two-part questions in one prompt |

---

## Token Counter

The `TokenCounter` uses the formula **word count × 1.3**, a standard industry approximation when you don't have access to the real tokenizer.

Why tokens matter: AI APIs charge by token and have context-window limits. Fewer tokens means faster responses, lower cost, and more room for the model's answer.

```
--- Token Count ---
  Original:   ~14 tokens
  Compressed: ~11 tokens
  Saved:      3 tokens (21% reduction)
-------------------
```

---

## The Three Domains

| Domain | Detected When | Template Focus |
|---|---|---|
| **Code** | Keywords like `bug`, `function`, `python`, `debug` | Role: expert engineer. Asks for code, comments, edge cases, examples |
| **Writing** | Keywords like `essay`, `draft`, `tone`, `rewrite` | Role: professional editor. Asks for structure, tone, grammar |
| **General** | No clear winner between the two above | Role: knowledgeable assistant. Asks for direct answer + context |

---

## Project Structure

```
src/main/java/
│
├── org/example/                       ← V1 core (domain detection + templates)
│   ├── Domain.java                    # Enum: CODE, WRITING, GENERAL
│   ├── DomainAnalyzer.java            # Keyword counting to detect domain
│   ├── PromptTemplate.java            # Builds the structured prompt per domain
│   ├── PromptOptimizer.java           # Coordinates the V1 pipeline
│   └── Main.java                      # Entry point — updated for V3 web mode
│
├── com/promptopt/                     ← V2 additions
│   ├── TokenCounter.java              # Estimates tokens, prints before/after
│   └── strategy/
│       ├── CompressionStrategy.java   # Interface — the Strategy Pattern contract
│       ├── RemoveRedundancyStrategy.java
│       ├── ShortenWordsStrategy.java
│       ├── RemoveContextStrategy.java
│       └── SplitQuestionStrategy.java
│
└── com/promptopt/web/                 ← V3 additions (Spring Boot)
    ├── PromptController.java          # REST endpoints
    └── ...

src/main/resources/
    ├── static/                        ← Frontend (HTML/CSS/JS)
    └── application.properties
```

---

## How to Run

**Requirements:** Java 21, Maven

### Option 1 — Web UI (V3)
```bash
mvn spring-boot:run
```
Then open [http://localhost:8080](http://localhost:8080) in your browser.

### Option 2 — Terminal (V2 mode)
```bash
mvn compile exec:java -Dexec.mainClass="org.example.Main"
```

### Option 3 — IntelliJ IDEA
Open the project, then click the green **Run** button on `Main.java`.

### Sample Session (Terminal mode)

```
============================================================
   BetterPrompt v2.0 — Compression + Token Awareness
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

Try another strategy on the same prompt? (y/n):
```

---

## V3 Roadmap

**Goal: Web UI, corpus-backed compression, and measured evaluation of whether compression actually helps.**

### 1. ✅ Web UI (Complete)
A Spring Boot web interface with:
- Input box for the user's prompt
- Dropdown / buttons to select compression strategy
- Side-by-side display of original vs compressed prompt
- Token count comparison
- Extensible: new strategies can be added without changing the UI

### 2. 🔄 Open-Source Corpus Integration (In Progress)
Replace the small hardcoded keyword lists with large open-source corpora:
- **Filler phrases corpus** — loaded from `filler_phrases.txt` at runtime via `CorpusLoader.java`
- Verbose-to-concise word pairs (from WordNet or similar)
- Self-introduction patterns corpus

Sources: GitHub open datasets, academic wordlists

### 3. ⬜ LLM-as-a-Judge Evaluation
Automatically measure compression quality:
- Send original prompt to Claude Sonnet → get Answer A
- Send compressed prompt to Claude Sonnet → get Answer B
- Ask Claude to score both answers (1–10)
- Record scores + token counts for every test

### 4. ⬜ Efficiency Index
A single metric to determine if compression is worth it:

```
Efficiency Index = Quality improvement% / Token cost increase%
```

- Index > 1 → compression is worth it
- Index < 1 → compression costs more than it saves

### 5. ⬜ Results Export
Save all test results to `results.csv` for analysis.
Find which types of questions benefit most from compression.

---

## Tech Stack

| | |
|---|---|
| Language | Java 21 |
| Build tool | Maven |
| Web framework | Spring Boot (V3) |
| Frontend | HTML / CSS / JS (served via Spring Boot) |
| Design patterns | Strategy Pattern (compression), Composition (optimizer) |
| External APIs | None (V1/V2), Claude API planned for V3 evaluation phase |
| External libraries | None (V1/V2) |

---

*V1 focused on Java OOP fundamentals: enums, composition, switch expressions, text blocks.*
*V2 adds the Strategy Pattern and interfaces — the same principles used in large production codebases.*
*V3 adds a web UI, corpus-backed compression, and quantitative evaluation.*
