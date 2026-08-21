package com.atomgdx.theme.graph;

import org.junit.Test;
import static org.junit.Assert.*;

public class VisualGraphEngineTest {

    @Test
    public void testPinTypesAndCompatibility() {
        assertTrue(PinType.FLOAT.canConnectTo(PinType.FLOAT));
        assertTrue(PinType.FLOAT.canConnectTo(PinType.VEC2));
        assertTrue(PinType.FLOAT.canConnectTo(PinType.VEC3));
        assertTrue(PinType.FLOAT.canConnectTo(PinType.VEC4_COLOR));
        assertTrue(PinType.VEC3.canConnectTo(PinType.VEC4_COLOR));
        assertTrue(PinType.VEC4_COLOR.canConnectTo(PinType.VEC3));
        assertTrue(PinType.GEOMETRY.canConnectTo(PinType.GEOMETRY));
        assertTrue(PinType.FLOW.canConnectTo(PinType.FLOW));
    }

    @Test
    public void testGraphDocumentNodeAndConnectionOperations() {
        VisualGraphDocument doc = new VisualGraphDocument("Test_Graph", VisualGraphDocument.GraphType.SHADER_GRAPH);

        NodeModel mathNode = new NodeModel("node_add", "Add (+)", "Math");
        mathNode.addInput("inA", "A", PinType.FLOAT);
        mathNode.addInput("inB", "B", PinType.FLOAT);
        mathNode.addOutput("out", "Out", PinType.FLOAT);
        doc.addNode(mathNode);

        NodeModel masterNode = new NodeModel("pbr_master", "PBR Master", "Output");
        masterNode.addInput("roughness", "Roughness", PinType.FLOAT);
        doc.addNode(masterNode);

        assertEquals(2, doc.nodes.size());
        assertNotNull(doc.findNode("node_add"));
        assertNotNull(doc.findNode("pbr_master"));

        doc.connect("node_add", "out", "pbr_master", "roughness");
        assertEquals(1, doc.connections.size());
        assertEquals("node_add:out->pbr_master:roughness", doc.connections.get(0).connectionId);

        // Remove node
        doc.removeNode("node_add");
        assertEquals(1, doc.nodes.size());
        assertEquals(0, doc.connections.size()); // Connection should be automatically cleaned up
    }

    @Test
    public void testGlslShaderGraphGeneration() {
        VisualGraphDocument doc = new VisualGraphDocument("PBR_Lit", VisualGraphDocument.GraphType.SHADER_GRAPH);

        NodeModel master = new NodeModel("pbr_master", "PBR Master Stack", "Output");
        master.addInput("baseColor", "Base Color", PinType.VEC4_COLOR);
        doc.addNode(master);

        GlslShaderGraphGenerator.ShaderOutput output = GlslShaderGraphGenerator.generateGlsl(doc);
        assertNotNull(output);
        assertNotNull(output.vertexShader);
        assertNotNull(output.fragmentShader);

        assertTrue(output.fragmentShader.contains("gl_FragColor"));
        assertTrue(output.fragmentShader.contains("uniform sampler2D u_texture;"));
        assertTrue(output.vertexShader.contains("gl_Position = u_projTrans * a_position;"));
    }

    @Test
    public void testFsmAndVisualScriptingGraph() {
        VisualGraphDocument fsm = new VisualGraphDocument("AI_Fsm", VisualGraphDocument.GraphType.VISUAL_SCRIPTING_FSM);

        NodeModel idle = new NodeModel("state_idle", "State: Idle", "State");
        idle.addOutput("onPlayerSeen", "On Player Seen", PinType.FLOW);
        fsm.addNode(idle);

        NodeModel chase = new NodeModel("state_chase", "State: Chase", "State");
        chase.addInput("enter", "Enter", PinType.FLOW);
        fsm.addNode(chase);

        fsm.connect("state_idle", "onPlayerSeen", "state_chase", "enter");
        assertEquals(1, fsm.connections.size());
        assertEquals(VisualGraphDocument.GraphType.VISUAL_SCRIPTING_FSM, fsm.type);
    }

    @Test
    public void testAnimatorAndGeometryGraphTypes() {
        VisualGraphDocument anim = new VisualGraphDocument("Hero_Animator", VisualGraphDocument.GraphType.ANIMATOR_CONTROLLER);
        assertEquals(VisualGraphDocument.GraphType.ANIMATOR_CONTROLLER, anim.type);

        VisualGraphDocument geo = new VisualGraphDocument("Terrain_Mesh", VisualGraphDocument.GraphType.PROCEDURAL_GEOMETRY);
        assertEquals(VisualGraphDocument.GraphType.PROCEDURAL_GEOMETRY, geo.type);
    }
}
