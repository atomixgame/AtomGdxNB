package com.atomgdx.theme.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete Visual Graph Document container with node and edge serialization.
 */
public class VisualGraphDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum GraphType {
        SHADER_GRAPH,
        VISUAL_SCRIPTING_FSM,
        ANIMATOR_CONTROLLER,
        PROCEDURAL_GEOMETRY
    }

    public String graphName = "New_Graph";
    public GraphType type = GraphType.SHADER_GRAPH;
    public final List<NodeModel> nodes = new ArrayList<>();
    public final List<ConnectionModel> connections = new ArrayList<>();

    public VisualGraphDocument() {}

    public VisualGraphDocument(String graphName, GraphType type) {
        this.graphName = graphName;
        this.type = type;
    }

    public void addNode(NodeModel node) {
        nodes.add(node);
    }

    public void removeNode(String nodeId) {
        nodes.removeIf(n -> n.nodeId.equals(nodeId));
        connections.removeIf(c -> c.sourceNodeId.equals(nodeId) || c.targetNodeId.equals(nodeId));
    }

    public void connect(String sourceNodeId, String sourcePinId, String targetNodeId, String targetPinId) {
        connections.removeIf(c -> c.targetNodeId.equals(targetNodeId) && c.targetPinId.equals(targetPinId));
        connections.add(new ConnectionModel(sourceNodeId, sourcePinId, targetNodeId, targetPinId));
    }

    public void disconnect(String connectionId) {
        connections.removeIf(c -> c.connectionId.equals(connectionId));
    }

    public NodeModel findNode(String nodeId) {
        for (NodeModel n : nodes) if (n.nodeId.equals(nodeId)) return n;
        return null;
    }
}
