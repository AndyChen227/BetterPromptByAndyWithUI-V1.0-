package org.example;

import java.util.List;

/**
 * DomainAnalyzer inspects the raw prompt text and returns the most likely Domain.
 *
 * Strategy: keyword counting.
 *   1. Lowercase the prompt so "Python" and "python" both match.
 *   2. Check how many CODE keywords appear, then how many WRITING keywords appear.
 *   3. Whichever count is higher wins. Tie → GENERAL.
 *
 * This is a simple rule-based approach — no machine learning needed.
 * It's easy to extend: just add more words to the lists below.
 */
public class DomainAnalyzer {

    // --- Keyword lists ---
    // Each list captures vocabulary typical of that domain.
    // Using List.of() creates an immutable list (can't be changed after creation).

    private static final List<String> CODE_KEYWORDS = List.of(
            "code", "function", "bug", "debug", "algorithm", "class", "method",
            "variable", "loop", "array", "object", "api", "database", "sql",
            "java", "python", "javascript", "c++", "html", "css", "git",
            "error", "exception", "compile", "runtime", "syntax", "program",
            "refactor", "test", "unit test", "library", "framework", "dependency",
            "implement", "fix", "script", "output", "input", "return", "parameter"
    );

    private static final List<String> WRITING_KEYWORDS = List.of(
            "write", "essay", "story", "paragraph", "sentence", "tone", "style",
            "creative", "blog", "article", "poem", "narrative", "draft", "edit",
            "proofread", "grammar", "persuasive", "formal", "informal", "email",
            "letter", "report", "summary", "introduction", "conclusion", "thesis",
            "describe", "explain", "dialogue", "character", "plot", "fiction",
            "nonfiction", "rewrite", "improve", "clarity", "concise", "professional"
    );

    /**
     * Analyzes the prompt and returns the detected Domain.
     *
     * @param prompt  the raw text the user typed
     * @return        CODE, WRITING, or GENERAL
     */
    public Domain analyze(String prompt) {
        // Step 1: normalize to lowercase so matching is case-insensitive
        String lower = prompt.toLowerCase();

        // Step 2: count keyword hits for each domain
        int codeScore    = countMatches(lower, CODE_KEYWORDS);
        int writingScore = countMatches(lower, WRITING_KEYWORDS);

        // Step 3: pick the winner (tie → GENERAL)
        if (codeScore > writingScore) {
            return Domain.CODE;
        } else if (writingScore > codeScore) {
            return Domain.WRITING;
        } else {
            return Domain.GENERAL;
        }
    }

    /**
     * Counts how many keywords from the list appear in the text.
     * Uses String.contains() — simple substring search.
     *
     * @param text      the lowercased prompt
     * @param keywords  the keyword list to check against
     * @return          number of matched keywords
     */
    private int countMatches(String text, List<String> keywords) {
        int count = 0;
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                count++;
            }
        }
        return count;
    }
}
