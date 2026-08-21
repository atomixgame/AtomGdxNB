package com.atomgdx.theme.graph;

import java.io.Serializable;

/**
 * Input or Output Pin on a Visual Graph Node.
 */
public class PinModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public String pinId;
    public String name;
    public PinType type;
    public boolean isInput;
    public String defaultValue = "0.0";

    public PinModel(String pinId, String name, PinType type, boolean isInput) {
        this.pinId = pinId;
        this.name = name;
        this.type = type;
        this.isInput = isInput;
    }

    public PinModel(String pinId, String name, PinType type, boolean isInput, String defaultValue) {
        this.pinId = pinId;
        this.name = name;
        this.type = type;
        this.isInput = isInput;
        this.defaultValue = defaultValue;
    }
}
