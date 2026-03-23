package org.example;

/**
 * PromptTemplate builds an optimized prompt string for a given domain.
 *
 * Each domain gets its own template that:
 *   - Sets the AI's role ("Act as a...")
 *   - Asks for structured output (steps, examples, tone guidance, etc.)
 *   - Embeds the user's original prompt in a clear way
 *
 * The core Java feature here is String.format(), which replaces %s placeholders
 * with actual values — similar to printf in C/C++.
 *
 * Why put templates here instead of inside PromptOptimizer?
 *   Single Responsibility Principle: each class does ONE job.
 *   PromptTemplate = "what should the optimized prompt say?"
 *   PromptOptimizer = "how do we coordinate the whole process?"
 */
public class PromptTemplate {

    /**
     * Returns an optimized prompt string for the given domain and original prompt.
     *
     * Uses a switch expression (Java 14+) — a clean way to return different
     * values based on which enum constant we have.
     *
     * @param domain          the detected domain
     * @param originalPrompt  the user's raw input
     * @return                the fully formatted optimized prompt
     */
    public String buildOptimizedPrompt(Domain domain, String originalPrompt) {

        // Switch expression: each arm returns a String directly.
        // The -> syntax means "if domain equals X, evaluate this expression".
        return switch (domain) {

            case CODE -> String.format(
                """
                Act as an expert software engineer.

                Task: %s

                Please provide:
                1. A clear explanation of the approach
                2. Well-commented code with meaningful variable names
                3. Edge cases or potential errors to watch for
                4. A brief example of how to run or test the solution
                """,
                originalPrompt
            );

            case WRITING -> String.format(
                """
                Act as a professional editor and writing coach.

                Request: %s

                Please provide:
                1. A polished draft that fits the requested tone and audience
                2. Clear paragraph structure with a strong opening and closing
                3. Suggestions for improving clarity or style
                4. Any grammar or punctuation corrections needed
                """,
                originalPrompt
            );

            case GENERAL -> String.format(
                """
                You are a knowledgeable and helpful assistant.

                Question / Task: %s

                Please provide:
                1. A direct, clear answer
                2. Supporting context or background where helpful
                3. Any important caveats or limitations to keep in mind
                """,
                originalPrompt
            );
        };
    }
}
