package com.atomgdx.mcp;

import java.io.Serializable;

/**
 * Model Context Protocol (MCP) Resource representation.
 */
public class McpResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String uri;
    private final String name;
    private final String description;
    private final String mimeType;

    public McpResource(String uri, String name, String description, String mimeType) {
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
    }

    public String getUri() { return uri; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getMimeType() { return mimeType; }
}
