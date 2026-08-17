package com.atomgdx.ai;

/**
 * AI Provider backends supported by AtomGdx Studio AI Assistant.
 */
public enum AiProvider {
    OLLAMA("Ollama (Local LLM)", true, "http://localhost:11434/api/generate"),
    GEMINI("Google Gemini 2.0", false, "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"),
    CLAUDE("Anthropic Claude 3.5", false, "https://api.anthropic.com/v1/messages"),
    OPENAI("OpenAI GPT-4o", false, "https://api.openai.com/v1/chat/completions");

    private final String displayName;
    private final boolean local;
    private final String endpoint;

    AiProvider(String displayName, boolean local, String endpoint) {
        this.displayName = displayName;
        this.local = local;
        this.endpoint = endpoint;
    }

    public String getDisplayName() { return displayName; }
    public boolean isLocal() { return local; }
    public String getEndpoint() { return endpoint; }
}
