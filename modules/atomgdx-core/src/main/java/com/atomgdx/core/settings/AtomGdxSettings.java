package com.atomgdx.core.settings;

import java.io.Serializable;

/**
 * Global IDE configuration settings for AtomGdx Studio.
 */
public class AtomGdxSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private String androidSdkPath = "";
    private String defaultGdxVersion = "1.13.1";
    private String defaultJavaVersion = "21";
    private boolean autoRepackAtlasOnBuild = true;
    private boolean liveShaderPreviewEnabled = true;

    // AI Provider Configuration
    private String aiProvider = "OLLAMA"; // OLLAMA, GEMINI, CLAUDE, OPENAI
    private String ollamaEndpoint = "http://localhost:11434";
    private String ollamaModel = "deepseek-coder";
    private String geminiApiKey = "";
    private String claudeApiKey = "";
    private String openAiApiKey = "";

    // MCP Configuration
    private boolean mcpServerEnabled = true;
    private int mcpServerPort = 9876;

    public AtomGdxSettings() {
    }

    public String getAndroidSdkPath() {
        return androidSdkPath;
    }

    public void setAndroidSdkPath(String androidSdkPath) {
        this.androidSdkPath = androidSdkPath;
    }

    public String getDefaultGdxVersion() {
        return defaultGdxVersion;
    }

    public void setDefaultGdxVersion(String defaultGdxVersion) {
        this.defaultGdxVersion = defaultGdxVersion;
    }

    public String getDefaultJavaVersion() {
        return defaultJavaVersion;
    }

    public void setDefaultJavaVersion(String defaultJavaVersion) {
        this.defaultJavaVersion = defaultJavaVersion;
    }

    public boolean isAutoRepackAtlasOnBuild() {
        return autoRepackAtlasOnBuild;
    }

    public void setAutoRepackAtlasOnBuild(boolean autoRepackAtlasOnBuild) {
        this.autoRepackAtlasOnBuild = autoRepackAtlasOnBuild;
    }

    public boolean isLiveShaderPreviewEnabled() {
        return liveShaderPreviewEnabled;
    }

    public void setLiveShaderPreviewEnabled(boolean liveShaderPreviewEnabled) {
        this.liveShaderPreviewEnabled = liveShaderPreviewEnabled;
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public void setAiProvider(String aiProvider) {
        this.aiProvider = aiProvider;
    }

    public String getOllamaEndpoint() {
        return ollamaEndpoint;
    }

    public void setOllamaEndpoint(String ollamaEndpoint) {
        this.ollamaEndpoint = ollamaEndpoint;
    }

    public String getOllamaModel() {
        return ollamaModel;
    }

    public void setOllamaModel(String ollamaModel) {
        this.ollamaModel = ollamaModel;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }

    public String getClaudeApiKey() {
        return claudeApiKey;
    }

    public void setClaudeApiKey(String claudeApiKey) {
        this.claudeApiKey = claudeApiKey;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public boolean isMcpServerEnabled() {
        return mcpServerEnabled;
    }

    public void setMcpServerEnabled(boolean mcpServerEnabled) {
        this.mcpServerEnabled = mcpServerEnabled;
    }

    public int getMcpServerPort() {
        return mcpServerPort;
    }

    public void setMcpServerPort(int mcpServerPort) {
        this.mcpServerPort = mcpServerPort;
    }
}
