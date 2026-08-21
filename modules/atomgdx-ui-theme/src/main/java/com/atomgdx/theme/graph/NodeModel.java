package com.atomgdx.theme.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node Model in the Visual Graph (Shader, Blueprint, FSM, Geometry Nodes).
 */
public class NodeModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public String nodeId;
    public String title;
    public String category = "General";
    public int posX = 100;
    public int posY = 100;
    public int width = 160;
    public int height = 120;
    public int headerColorRgb = 0xFF282C34;

    public final List<PinModel> inputPins = new ArrayList<>();
    public final List<PinModel> outputPins = new ArrayList<>();
    public final Map<String, String> properties = new HashMap<>();

    public NodeModel(String nodeId, String title, String category) {
        this.nodeId = nodeId;
        this.title = title;
        this.category = category;
    }

    public NodeModel addInput(String pinId, String name, PinType type) {
        inputPins.add(new PinModel(pinId, name, type, true));
        return this;
    }

    public NodeModel addInput(String pinId, String name, PinType type, String defVal) {
        inputPins.add(new PinModel(pinId, name, type, true, defVal));
        return this;
    }

    public NodeModel addOutput(String pinId, String name, PinType type) {
        outputPins.add(new PinModel(pinId, name, type, false));
        return this;
    }

    public PinModel findPin(String pinId) {
        for (PinModel p : inputPins) if (p.pinId.equals(pinId)) return p;
        for (PinModel p : outputPins) if (p.pinId.equals(pinId)) return p;
        return null;
    }
}
