package com.promptopt.strategy;

import com.promptopt.RuleLoader;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * RemoveRedundancyStrategy strips out polite filler phrases that add words
 * but carry no actual meaning for an AI model.
 *
 * WHY: AI models don't need "please" or "I was wondering" — they respond to
 * intent. Removing social padding makes the core question clearer and shorter,
 * which leaves more token budget for the answer.
 *
 * HOW: replaceAll() with a regex. The (?i) flag makes it case-insensitive so
 * "Can you please" and "can you please" are both caught.
 */
public class RemoveRedundancyStrategy implements CompressionStrategy {

    // The phrase list lives in a JSON resource so it is easy to update later.
    private static final String RULES_PATH = "rules/redundancy_phrases.json";
    private static final List<String> REDUNDANCY_PHRASES =
            new RuleLoader().loadStringList(RULES_PATH).stream()
                    .sorted(Comparator.comparingInt(String::length).reversed())
                    .toList();

    @Override
    public String compress(String prompt) {
        // Return early for null or blank input so the rest of the logic stays simple.
        if (prompt == null || prompt.isBlank()) {
            return prompt;
        }

        String result = prompt;

        // Remove longer phrases first so a short phrase does not partially consume them.
        for (String phrase : REDUNDANCY_PHRASES) {
            // Also remove optional punctuation right after the phrase, plus nearby spaces.
            String regex = "(?i)" + Pattern.quote(phrase) + "\\s*[,;:.!?-]?\\s*";
            result = result.replaceAll(regex, " ");
        }

        // Clean up punctuation and spacing that may be left behind after removal.
        result = result.replaceAll("\\s+([,;:.!?])", "$1");
        result = result.replaceAll("([,;:.!?-])\\1+", "$1");
        result = result.replaceAll("^[\\s,;:.!?-]+", "");
        result = result.replaceAll("\\s{2,}", " ").trim();

        // Capitalize the first letter after cleanup if anything is left.
        if (!result.isEmpty()) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result;
    }

    @Override
    public String name() { return "Remove Redundancy (filler phrases)"; }
}
