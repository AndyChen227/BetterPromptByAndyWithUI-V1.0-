package com.promptopt;

/**
 * TokenCounter estimates how many tokens a string would use when sent to an AI.
 *
 * WHY tokens matter: AI APIs charge by token and have context-window limits.
 * Fewer tokens = faster responses, lower cost, more room for the answer.
 *
 * WHY the 1.3x formula: Real tokenizers (like GPT's BPE) split on subwords,
 * punctuation, and special characters — so token count is always higher than
 * word count. The 1.3 multiplier is a well-known rough approximation used in
 * the industry when you don't have access to the actual tokenizer.
 * Example: "don't" → 2 tokens ("don" + "'t"), "unbelievable" → 3 tokens.
 */
public class TokenCounter {

    /**
     * Estimates token count for a string.
     * Formula: word count × 1.3, rounded to nearest int.
     *
     * @param text  any string
     * @return      estimated token count
     */
    public int count(String text) {
        if (text == null || text.isBlank()) return 0;

        // split("\\s+") splits on one-or-more whitespace characters.
        // trim() first so a leading space doesn't create a ghost empty word.
        String[] words = text.trim().split("\\s+");
        return (int) Math.round(words.length * 1.3);
    }

    /**
     * Prints a before/after token comparison to the terminal.
     *
     * IMPORTANT: both parameters must be the FULL prompt sent to the AI —
     * that means user text + domain template combined. Passing only the user's
     * raw input produces misleading results because the template (which is
     * always included) can be 30-50 tokens on its own.
     *
     * @param beforeFull  original user input already wrapped in its domain template
     * @param afterFull   compressed user input already wrapped in its domain template
     */
    public void printComparison(String beforeFull, String afterFull) {
        int originalTokens    = count(beforeFull);
        int compressedTokens  = count(afterFull);
        int saved             = originalTokens - compressedTokens;

        // Avoid division by zero if the original prompt was somehow empty.
        double percentSaved = (originalTokens == 0) ? 0
                : (saved / (double) originalTokens) * 100;

        System.out.println("\n--- Token Count (input + template) ---");
        System.out.printf("  Before: ~%d tokens%n", originalTokens);
        System.out.printf("  After:  ~%d tokens%n", compressedTokens);

        if (saved > 0) {
            System.out.printf("  Saved:  %d tokens (%.0f%% reduction)%n", saved, percentSaved);
        } else if (saved == 0) {
            System.out.println("  No tokens saved — prompt unchanged by this strategy.");
        } else {
            // A strategy like SplitQuestion adds words, so count may increase.
            System.out.printf("  Note: prompt grew by %d tokens (restructuring added words).%n",
                    Math.abs(saved));
        }
        System.out.println("--------------------------------------");
    }
}
