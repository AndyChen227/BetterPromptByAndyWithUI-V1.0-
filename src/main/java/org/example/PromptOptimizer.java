package org.example;

/**
 * PromptOptimizer is the central coordinator of the tool.
 *
 * It runs the full pipeline:
 *   raw prompt → clean → detect domain → build optimized prompt
 *
 * It also holds the result of the last optimization so Main.java
 * can display a before/after comparison without rerunning the logic.
 *
 * Key concept: this class uses COMPOSITION — it owns instances of
 * DomainAnalyzer and PromptTemplate rather than extending them.
 * "Favor composition over inheritance" is a classic OOP guideline.
 */
public class PromptOptimizer {

    // These are instance fields — each PromptOptimizer carries its own copies.
    private final DomainAnalyzer  analyzer = new DomainAnalyzer();
    private final PromptTemplate  template = new PromptTemplate();

    // Stored after the last optimize() call so Main can display them.
    private Domain lastDetectedDomain;
    private String lastOptimizedPrompt;

    /**
     * Runs the full optimization pipeline on the user's raw prompt.
     *
     * Steps:
     *   1. Clean the prompt (trim whitespace, fix common issues)
     *   2. Detect which domain it belongs to
     *   3. Apply quick rule-based text improvements
     *   4. Build and return the final optimized prompt
     *
     * @param rawPrompt  the text the user typed at the terminal
     * @return           the optimized prompt ready to paste into an AI tool
     */
    public String optimize(String rawPrompt) {
        // Top-level pipeline classes should validate input early so later steps
        // can assume they are working with a safe, consistent value.
        if (rawPrompt == null || rawPrompt.isBlank()) {
            lastDetectedDomain = Domain.GENERAL;
            lastOptimizedPrompt = "";
            return lastOptimizedPrompt;
        }

        // Step 1: basic cleaning
        String cleaned = clean(rawPrompt);

        // Step 2: domain detection (stores result for later retrieval)
        lastDetectedDomain = analyzer.analyze(cleaned);

        // Step 3: rule-based text improvements
        String improved = applyRules(cleaned, lastDetectedDomain);

        // Step 4: wrap in the domain's template
        lastOptimizedPrompt = template.buildOptimizedPrompt(lastDetectedDomain, improved);

        return lastOptimizedPrompt;
    }

    // --- Getters so Main.java can display results ---

    public Domain getLastDetectedDomain() {
        return lastDetectedDomain;
    }

    public String getLastOptimizedPrompt() {
        return lastOptimizedPrompt;
    }

    // -----------------------------------------------------------------------
    // Private helper methods — internal details hidden from other classes.
    // -----------------------------------------------------------------------

    /**
     * Cleans the raw prompt:
     *   - Removes leading/trailing whitespace
     *   - Collapses multiple spaces into one
     *   - Capitalizes the first letter
     *
     * @param raw  the original input string
     * @return     cleaned string
     */
    private String clean(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        // trim() removes whitespace from both ends.
        // replaceAll() uses a regex: \\s+ means "one or more whitespace characters".
        String result = raw.trim().replaceAll("\\s+", " ");

        // Capitalize the first character if the string isn't empty.
        if (!result.isEmpty()) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result;
    }

    /**
     * Applies domain-specific rule-based improvements to the prompt text.
     *
     * These are simple string transformations — no AI, no external calls.
     *   - CODE:    append a language hint if none is mentioned
     *   - WRITING: append a tone/audience reminder if missing
     *   - GENERAL: ensure the prompt ends with a question mark when appropriate
     *
     * @param prompt  the cleaned prompt
     * @param domain  the detected domain
     * @return        the improved prompt text (still plain text, not yet templated)
     */
    private String applyRules(String prompt, Domain domain) {
        return switch (domain) {

            case CODE -> {
                String result = prompt;
                // If no common language name is mentioned, add a gentle reminder.
                boolean mentionsLanguage =
                        prompt.toLowerCase().matches(".*\\b(java|python|javascript|c\\+\\+|go|rust|kotlin)\\b.*");
                if (!mentionsLanguage) {
                    result += " (please specify the programming language if relevant)";
                }
                yield result;   // yield is how you return a value from a switch block
            }

            case WRITING -> {
                String result = prompt;
                // Remind the model about audience/tone if not already stated.
                boolean mentionsTone =
                        prompt.toLowerCase().matches(".*\\b(formal|informal|professional|casual|tone|audience)\\b.*");
                if (!mentionsTone) {
                    result += " (assume a professional tone unless instructed otherwise)";
                }
                yield result;
            }

            case GENERAL -> {
                String result = prompt;
                // Add a question mark if the prompt looks like a question but lacks one.
                boolean looksLikeQuestion =
                        result.toLowerCase().matches("^(what|why|how|when|where|who|which|can|could|should|is|are|do|does).*");
                if (looksLikeQuestion && !result.endsWith("?")) {
                    result += "?";
                }
                yield result;
            }
        };
    }
}
