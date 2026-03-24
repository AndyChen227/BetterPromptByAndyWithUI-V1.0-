package org.example;

import java.util.List;
import java.util.regex.Pattern;

/**
 * DomainAnalyzer inspects the raw prompt text and returns the most likely Domain.
 *
 * Strategy: keyword counting.
 *   1. Check how many CODE keywords appear, then how many WRITING keywords appear.
 *   2. Whichever count is higher wins. Tie -> GENERAL.
 *
 * This is a simple rule-based approach with no machine learning needed.
 * It is easy to extend: just add more words to the lists below.
 *
 * WHY whole-word matching matters:
 * Plain substring matching can count accidental hits inside larger words.
 * For example, "class" should not match inside "classical".
 * Whole-word matching improves prompt classification quality by counting
 * only real keyword hits.
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
        if (prompt == null || prompt.isBlank()) {
            return Domain.GENERAL;
        }

        // Count keyword hits using case-insensitive whole-word matching.
        int codeScore = countMatches(prompt, CODE_KEYWORDS);
        int writingScore = countMatches(prompt, WRITING_KEYWORDS);

        // Pick the winner. A tie falls back to GENERAL.
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
     * Whole-word matching avoids false positives from partial words.
     *
     * @param text      the original prompt
     * @param keywords  the keyword list to check against
     * @return          number of matched keywords
     */
    private int countMatches(String text, List<String> keywords) {
        int count = 0;
        for (String keyword : keywords) {
            if (containsWholeKeyword(text, keyword)) {
                count++;
            }
        }
        return count;
    }

    /** Returns true when the full keyword appears as a standalone word or phrase. */
    private boolean containsWholeKeyword(String text, String keyword) {
        if (text == null || text.isBlank()) {
            return false;
        }

        return buildWholeKeywordPattern(keyword).matcher(text).find();
    }

    /**
     * Builds a case-insensitive pattern that matches a keyword only when it is
     * not attached to letters or digits on either side.
     */
    private Pattern buildWholeKeywordPattern(String keyword) {
        String regex = "(?i)(?<![A-Za-z0-9])" + Pattern.quote(keyword) + "(?![A-Za-z0-9])";
        return Pattern.compile(regex);
    }
}
