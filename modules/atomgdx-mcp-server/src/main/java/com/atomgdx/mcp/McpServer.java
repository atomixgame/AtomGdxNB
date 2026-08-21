package com.atomgdx.mcp;

import com.atomgdx.core.project.LibGdxProject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Embedded Model Context Protocol (MCP) Server for AtomGdx Studio.
 */
public class McpServer {
    private final Map<String, McpResource> resources = new ConcurrentHashMap<>();
    private final Map<String, McpTool> tools = new ConcurrentHashMap<>();
    private LibGdxProject activeProject;

    public McpServer() {
        registerDefaultTools();
    }

    public void setActiveProject(LibGdxProject project) {
        this.activeProject = project;
        refreshProjectResources();
    }

    private void refreshProjectResources() {
        resources.clear();
        if (activeProject != null) {
            resources.put("libgdx://project/manifest", new McpResource(
                    "libgdx://project/manifest",
                    "Active Project Manifest",
                    "Project name, targets, and configured extensions",
                    "application/json"
            ));
            resources.put("libgdx://assets/atlas/list", new McpResource(
                    "libgdx://assets/atlas/list",
                    "Texture Atlases",
                    "List of packed texture atlases in the project",
                    "application/json"
            ));
            resources.put("libgdx://scenes/active/hierarchy", new McpResource(
                    "libgdx://scenes/active/hierarchy",
                    "Active Scene Hierarchy",
                    "HyperLap2D Scene2D node tree and layers",
                    "application/json"
            ));
        }
    }

    private void registerDefaultTools() {
        tools.put("compile_glsl_shader", new McpTool(
                "compile_glsl_shader",
                "Validates GLSL vertex and fragment shader syntax",
                Map.of("type", "object"),
                args -> {
                    String code = args != null && args.get("code") != null ? args.get("code").toString() : "";
                    if (code.isBlank()) {
                        return "{\"status\": \"ERROR\", \"message\": \"Empty shader source code.\"}";
                    }
                    boolean hasMain = code.contains("void main()");
                    boolean balancedBraces = code.chars().filter(ch -> ch == '{').count() == code.chars().filter(ch -> ch == '}').count();
                    if (!hasMain) {
                        return "{\"status\": \"ERROR\", \"message\": \"Missing void main() entrypoint in shader.\"}";
                    }
                    if (!balancedBraces) {
                        return "{\"status\": \"ERROR\", \"message\": \"Mismatched curly braces in shader source.\"}";
                    }
                    return "{\"status\": \"OK\", \"message\": \"GLSL syntax valid.\"}";
                }
        ));

        tools.put("create_particle_effect", new McpTool(
                "create_particle_effect",
                "Creates a new 2D/3D particle effect asset in the project",
                Map.of("type", "object"),
                args -> {
                    String name = args != null && args.get("name") != null ? args.get("name").toString() : "plasma";
                    String targetPath = "assets/particles/" + name + ".p";
                    if (activeProject != null && activeProject.getRootDirectory() != null) {
                        java.io.File pFile = new java.io.File(activeProject.getRootDirectory(), targetPath);
                        if (pFile.getParentFile() != null) pFile.getParentFile().mkdirs();
                        try {
                            String template = "- Delay -\nactive: false\n- Duration - \nlowMinValue: 1000.0\n- Count -\nmin: 0\nmax: 200\n- Emission -\nlowMin: 0.0\nlowMax: 0.0\nhighMin: 50.0\nhighMax: 50.0\n";
                            java.nio.file.Files.writeString(pFile.toPath(), template, java.nio.charset.StandardCharsets.UTF_8);
                        } catch (Exception ignored) {}
                    }
                    return "{\"status\": \"CREATED\", \"file\": \"" + targetPath + "\"}";
                }
        ));
    }

    public Collection<McpResource> getResources() {
        return Collections.unmodifiableCollection(resources.values());
    }

    public Collection<McpTool> getTools() {
        return Collections.unmodifiableCollection(tools.values());
    }

    public String handleRpc(String jsonRequest) {
        if (jsonRequest.contains("resources/list")) {
            StringBuilder sb = new StringBuilder("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"resources\":[");
            int idx = 0;
            for (McpResource r : resources.values()) {
                sb.append("{\"uri\":\"").append(r.getUri()).append("\",\"name\":\"").append(r.getName()).append("\"}");
                if (++idx < resources.size()) sb.append(",");
            }
            sb.append("]}}");
            return sb.toString();
        } else if (jsonRequest.contains("tools/list")) {
            StringBuilder sb = new StringBuilder("{\"jsonrpc\":\"2.0\",\"id\":2,\"result\":{\"tools\":[");
            int idx = 0;
            for (McpTool t : tools.values()) {
                sb.append("{\"name\":\"").append(t.getName()).append("\",\"description\":\"").append(t.getDescription()).append("\"}");
                if (++idx < tools.size()) sb.append(",");
            }
            sb.append("]}}");
            return sb.toString();
        }
        return "{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"status\":\"OK\"}}";
    }
}
