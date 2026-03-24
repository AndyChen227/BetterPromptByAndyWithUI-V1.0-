package com.promptopt.strategy;

import com.promptopt.RuleLoader;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ShortenWordsStrategy replaces verbose words with shorter equivalents.
 *
 * WHY: Academic or formal writing tends to use longer words ("utilize",
 * "demonstrate") that cost extra tokens without adding precision. Swapping
 * them for simpler synonyms makes the prompt leaner and often clearer.
 *
 * HOW: The replacement rules live in a JSON file under resources and are loaded
 * at startup. Externalizing rules makes it easier to update wording, experiment
 * with new replacements, and keep strategy classes small.
 */
public class ShortenWordsStrategy implements CompressionStrategy {

    private static final String RULES_PATH = "rules/shorten_words_rules.json";
    private static final ShorteningRuleFile RULES = loadRules();

    @Override
    public String compress(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return prompt;
        }

        String result = prompt;

        // External rule loading needs defensive checks so one bad file or entry
        // does not break the whole strategy.
        for (ShorteningRule rule : getSafeReplacements()) {
            if (!isValidRule(rule)) {
                continue;
            }

            String regex = "(?i)\\b" + Pattern.quote(rule.getFrom()) + "\\b";
            result = result.replaceAll(regex, Matcher.quoteReplacement(rule.getTo()));
        }
        return result;
    }

    @Override
    public String name() { return "Shorten Words (verbose -> concise)"; }

    private static ShorteningRuleFile loadRules() {
        try {
            return new RuleLoader().loadJson(RULES_PATH, ShorteningRuleFile.class);
        } catch (RuntimeException e) {
            return new ShorteningRuleFile();
        }
    }

    private List<ShorteningRule> getSafeReplacements() {
        List<ShorteningRule> replacements = RULES.getReplacements();
        return replacements == null ? Collections.emptyList() : replacements;
    }

    private boolean isValidRule(ShorteningRule rule) {
        return rule != null
                && rule.getFrom() != null
                && !rule.getFrom().isBlank()
                && rule.getTo() != null;
    }

    /** Wraps the JSON file so future metadata can be added without reshaping the loader. */
    public static class ShorteningRuleFile {
        private List<ShorteningRule> replacements;

        public List<ShorteningRule> getReplacements() {
            return replacements;
        }

        public void setReplacements(List<ShorteningRule> replacements) {
            this.replacements = replacements;
        }
    }

    /** One replacement rule from the JSON file. */
    public static class ShorteningRule {
        private String from;
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}
