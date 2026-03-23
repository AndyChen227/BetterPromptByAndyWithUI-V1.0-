package com.promptopt.web;

public class OptimizeRequest {
    private String prompt;
    private int strategyChoice;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public int getStrategyChoice() { return strategyChoice; }
    public void setStrategyChoice(int strategyChoice) {
        this.strategyChoice = strategyChoice;
    }
}
