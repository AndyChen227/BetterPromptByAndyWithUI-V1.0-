package com.promptopt.strategy;

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

    @Override
    public String compress(String prompt) {
        String result = prompt;

        // Each call removes one filler phrase and collapses any leftover spaces.
        // Order matters: remove longer phrases first to avoid partial matches.
        result = result.replaceAll("(?i)can you please\\s*", "");
        result = result.replaceAll("(?i)could you help me\\s*", "");
        result = result.replaceAll("(?i)i was wondering\\s*", "");
        result = result.replaceAll("(?i)i would like to know\\s*", "");
        result = result.replaceAll("(?i)would you be able to\\s*", "");
        result = result.replaceAll("(?i)if you don't mind\\s*", "");

        // Collapse any double spaces created by the removals, then trim ends.
        result = result.replaceAll("\\s{2,}", " ").trim();

        // Capitalize the first letter since removal may have lowercased the start.
        if (!result.isEmpty()) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result;
    }

    @Override
    public String name() { return "Remove Redundancy (filler phrases)"; }
}
