package com.promptopt.strategy;

/**
 * SplitQuestionStrategy detects compound prompts — two questions joined by
 * "and" — and splits them into separate numbered questions.
 *
 * WHY: AI models handle one question at a time more accurately than compound
 * ones. "Explain recursion and show a Java example" often gets a shorter answer
 * than two clearly labeled sub-questions. Splitting forces the model to address
 * each part explicitly.
 *
 * HOW: We look for " and " in the prompt. If found, we split there and label
 * the two halves "Question 1:" and "Question 2:". Each half is capitalized
 * and trimmed independently.
 *
 * LIMITATION: This is heuristic — "loops and arrays are related" would also
 * split even though it's not two questions. Rule-based logic has edge cases;
 * a V3 could use grammar parsing to be smarter here.
 */
public class SplitQuestionStrategy implements CompressionStrategy {

    @Override
    public String compress(String prompt) {
        // Case-insensitive search for " and " as the split point.
        // We only split on the FIRST occurrence to avoid over-splitting.
        int splitIndex = prompt.toLowerCase().indexOf(" and ");

        // If no " and " found, return the prompt unchanged — nothing to split.
        if (splitIndex == -1) {
            return prompt;
        }

        String part1 = prompt.substring(0, splitIndex).trim();
        // +5 skips past " and " (5 characters)
        String part2 = prompt.substring(splitIndex + 5).trim();

        // Capitalize each part independently.
        part1 = capitalize(part1);
        part2 = capitalize(part2);

        return "Question 1: " + part1 + "\nQuestion 2: " + part2;
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    public String name() { return "Split Question (compound → two labeled parts)"; }
}
