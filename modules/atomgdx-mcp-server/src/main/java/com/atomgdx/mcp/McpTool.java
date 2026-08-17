package com.atomgdx.mcp;

import java.util.Map;

/**
 * Model Context Protocol (MCP) Tool representation.
 */
public class McpTool {

    @FunctionalInterface
    public interface ToolHandler {
        String execute(Map<String, Object> arguments) throws Exception;
    }

    private final String name;
    private final String description;
    private final Map<String, Object> inputSchema;
    private final ToolHandler handler;

    public McpTool(String name, String description, Map<String, Object> inputSchema, ToolHandler handler) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
        this.handler = handler;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Map<String, Object> getInputSchema() { return inputSchema; }

    public String execute(Map<String, Object> arguments) throws Exception {
        return handler.execute(arguments);
    }
}
