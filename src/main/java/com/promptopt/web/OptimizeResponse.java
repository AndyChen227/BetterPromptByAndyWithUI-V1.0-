package com.promptopt.web;

public class OptimizeResponse {
    private String originalPrompt;
    private String compressedPrompt;
    private String optimizedPrompt;
    private String domain;
    private int originalTokens;
    private int compressedTokens;
    private int savedTokens;
    private String strategyName;

    public String getOriginalPrompt() { return originalPrompt; }
    public void setOriginalPrompt(String v) { this.originalPrompt = v; }

    public String getCompressedPrompt() { return compressedPrompt; }
    public void setCompressedPrompt(String v) { this.compressedPrompt = v; }

    public String getOptimizedPrompt() { return optimizedPrompt; }
    public void setOptimizedPrompt(String v) { this.optimizedPrompt = v; }

    public String getDomain() { return domain; }
    public void setDomain(String v) { this.domain = v; }

    public int getOriginalTokens() { return originalTokens; }
    public void setOriginalTokens(int v) { this.originalTokens = v; }

    public int getCompressedTokens() { return compressedTokens; }
    public void setCompressedTokens(int v) { this.compressedTokens = v; }

    public int getSavedTokens() { return savedTokens; }
    public void setSavedTokens(int v) { this.savedTokens = v; }

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String v) { this.strategyName = v; }
}
