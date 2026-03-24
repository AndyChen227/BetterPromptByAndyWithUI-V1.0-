package com.promptopt.strategy;

import com.promptopt.RuleLoader;

import java.util.Collections;
import java.util.List;

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
 * HOW: Sentence-splitting. We split on ". " and drop any sentence
 * that starts with a known self-intro pattern. Remaining parts are rejoined.
 *
 * Externalized context-removal rules improve maintainability and make it easier
 * to experiment with new patterns without editing the strategy code itself.
 */
public class RemoveContextStrategy implements CompressionStrategy {

    private static final String RULES_PATH = "rules/remove_context_rules.json";
    private static final ContextRuleFile RULES = loadRules();

    @Override
    public String compress(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return prompt;
        }

        // Split on ". " (sentence boundary). Remaining parts are rejoined below.
        String[] sentences = prompt.split("\\. ");
        StringBuilder kept = new StringBuilder();

        for (String sentence : sentences) {
            if (!matchesIntroPattern(sentence.trim())) {
                if (!kept.isEmpty()) kept.append(". ");
                kept.append(sentence.trim());
            }
        }

        String result = kept.toString().trim();
        // If everything was stripped, fall back to the original prompt.
        return result.isEmpty() ? prompt : result;
    }

    /** Returns true if the sentence matches any configured self-intro pattern. */
    private boolean matchesIntroPattern(String sentence) {
        if (sentence == null || sentence.isBlank()) {
            return false;
        }

        // Defensive checks matter here because external rule files may be missing
        // or contain incomplete entries while patterns are being edited.
        for (ContextPatternRule rule : getSafePatterns()) {
            if (!isValidRule(rule)) {
                continue;
            }

            if (sentence.matches(rule.getPattern())) {
                return true;
            }
        }
        return false;
    }

    private static ContextRuleFile loadRules() {
        try {
            return new RuleLoader().loadJson(RULES_PATH, ContextRuleFile.class);
        } catch (RuntimeException e) {
            return new ContextRuleFile();
        }
    }

    private List<ContextPatternRule> getSafePatterns() {
        List<ContextPatternRule> patterns = RULES.getPatterns();
        return patterns == null ? Collections.emptyList() : patterns;
    }

    private boolean isValidRule(ContextPatternRule rule) {
        return rule != null
                && rule.getPattern() != null
                && !rule.getPattern().isBlank();
    }

    @Override
    public String name() { return "Remove Context (self-intro sentences)"; }

    /** Wraps the JSON file so future rule metadata can be added later. */
    public static class ContextRuleFile {
        private List<ContextPatternRule> patterns;

        public List<ContextPatternRule> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<ContextPatternRule> patterns) {
            this.patterns = patterns;
        }
    }

    /** One removable context pattern from the JSON file. */
    public static class ContextPatternRule {
        private String pattern;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }
}
