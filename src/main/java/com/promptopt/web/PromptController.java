package com.promptopt.web;

import com.promptopt.TokenCounter;
import com.promptopt.strategy.*;
import org.example.DomainAnalyzer;
import org.example.PromptOptimizer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PromptController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/optimize")
    public String optimize(@RequestParam String prompt,
                           @RequestParam int strategyChoice,
                           Model model) {

        CompressionStrategy strategy = switch (strategyChoice) {
            case 2 -> new ShortenWordsStrategy();
            case 3 -> new RemoveContextStrategy();
            case 4 -> new SplitQuestionStrategy();
            default -> new RemoveRedundancyStrategy();
        };

        String strategyName = switch (strategyChoice) {
            case 2 -> "Shorten Words";
            case 3 -> "Remove Context";
            case 4 -> "Split Question";
            default -> "Remove Redundancy";
        };

        TokenCounter tokenCounter = new TokenCounter();
        int originalTokens = tokenCounter.count(prompt);
        String compressed = strategy.compress(prompt);
        int compressedTokens = tokenCounter.count(compressed);
        int savedTokens = originalTokens - compressedTokens;

        PromptOptimizer optimizer = new PromptOptimizer();
        String optimized = optimizer.optimize(compressed);

        DomainAnalyzer analyzer = new DomainAnalyzer();
        String domain = analyzer.analyze(prompt).name();

        model.addAttribute("originalPrompt", prompt);
        model.addAttribute("compressedPrompt", compressed);
        model.addAttribute("optimizedPrompt", optimized);
        model.addAttribute("domain", domain);
        model.addAttribute("originalTokens", originalTokens);
        model.addAttribute("compressedTokens", compressedTokens);
        model.addAttribute("savedTokens", savedTokens);
        model.addAttribute("strategyName", strategyName);
        model.addAttribute("strategyChoice", strategyChoice);
        model.addAttribute("hasResult", true);

        return "index";
    }
}
