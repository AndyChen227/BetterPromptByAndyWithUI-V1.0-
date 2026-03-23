package com.promptopt.strategy;

/**
 * RemoveContextStrategy strips self-introductory background that the user
 * added but that an AI model doesn't need to answer the actual question.
 *
 * WHY: "I am a student learning Java" is context about the asker, not
 * information the model needs to answer "how does a for-loop work?".
 * Removing it shortens the prompt and focuses the model on the task.
 *
 * NOTE: This strategy is intentionally aggressive. It's useful when you
 * want the shortest possible prompt. If context IS important to the answer
 * (e.g., "as a beginner, avoid advanced topics"), don't use this strategy.
 *
 * HOW: Sentence-splitting. We split on ". " or ", " and drop any sentence
 * that starts with a known self-intro pattern. Remaining parts are rejoined.
 */
public class RemoveContextStrategy implements CompressionStrategy {

    // Patterns that signal a self-introduction rather than a real question.
    private static final String[] INTRO_PATTERNS = {
        "(?i)^i am a (student|beginner|learner|developer|newcomer).*",
        "(?i)^i am (learning|new to|studying).*",
        "(?i)^as a (student|beginner|learner|newcomer).*",
        "(?i)^i'm (learning|new to|studying).*",
        "(?i)^i'm a (student|beginner|learner).*",
        "(?i)^just started.*",
        "(?i)^i have no experience.*"
    };

    @Override
    public String compress(String prompt) {
        // Split on ". " (sentence boundary) — limit=-1 keeps trailing empty strings.
        String[] sentences = prompt.split("\\. ");
        StringBuilder kept = new StringBuilder();

        for (String sentence : sentences) {
            if (!matchesIntroPattern(sentence.trim())) {
                if (!kept.isEmpty()) kept.append(". ");
                kept.append(sentence.trim());
            }
        }

        String result = kept.toString().trim();
        // If everything was stripped (unlikely), fall back to original.
        return result.isEmpty() ? prompt : result;
    }

    /** Returns true if the sentence matches any intro pattern. */
    private boolean matchesIntroPattern(String sentence) {
        for (String pattern : INTRO_PATTERNS) {
            if (sentence.matches(pattern)) return true;
        }
        return false;
    }

    @Override
    public String name() { return "Remove Context (self-intro sentences)"; }
}
