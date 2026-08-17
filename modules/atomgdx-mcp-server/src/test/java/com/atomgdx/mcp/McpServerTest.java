package com.atomgdx.mcp;

import com.atomgdx.core.project.LibGdxProject;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class McpServerTest {

    @Test
    void testMcpServerRpc() {
        McpServer server = new McpServer();
        LibGdxProject project = new LibGdxProject("CyberAssault", "com.cyber", "Main", new File("."));
        server.setActiveProject(project);

        // Test resources/list
        String resListReq = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"resources/list\"}";
        String resListResp = server.handleRpc(resListReq);
        assertThat(resListResp).contains("libgdx://project/manifest");

        // Test tools/list
        String toolsListReq = "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/list\"}";
        String toolsListResp = server.handleRpc(toolsListReq);
        assertThat(toolsListResp).contains("compile_glsl_shader");
        assertThat(toolsListResp).contains("create_particle_effect");
    }
}
