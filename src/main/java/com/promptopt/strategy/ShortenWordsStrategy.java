package com.promptopt.strategy;

import java.util.Map;

/**
 * ShortenWordsStrategy replaces verbose words with shorter equivalents.
 *
 * WHY: Academic or formal writing tends to use longer words ("utilize",
 * "demonstrate") that cost extra tokens without adding precision. Swapping
 * them for simpler synonyms makes the prompt leaner and often clearer.
 *
 * HOW: We store replacements in a Map<String, String> (key = long word,
 * value = short word), then iterate and replace each one.
 * \\b in regex means "word boundary" — so "utilize" matches but "utilizer" won't.
 */
public class ShortenWordsStrategy implements CompressionStrategy {

    // Map.of() creates an immutable map — values can't be changed after creation.
    // Key = verbose word, Value = shorter replacement.
    private static final Map<String, String> REPLACEMENTS = Map.of(
        "utilize",      "use",
        "demonstrate",  "show",
        "implement",    "build",
        "regarding",    "about",
        "obtain",       "get",
        "sufficient",   "enough",
        "assistance",   "help",
        "therefore",    "so"
    );

    @Override
    public String compress(String prompt) {
        String result = prompt;

        // For each entry in the map, replace the verbose word with its shorter form.
        // (?i) = case-insensitive, \\b = word boundary (don't replace mid-word).
        for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            result = result.replaceAll("(?i)\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return result;
    }

    @Override
    public String name() { return "Shorten Words (verbose → concise)"; }
}
