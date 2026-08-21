package com.atomgdx.theme.graph;

import java.io.Serializable;

/**
 * Connection Edge linking an output pin to an input pin.
 */
public class ConnectionModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public String connectionId;
    public String sourceNodeId;
    public String sourcePinId;
    public String targetNodeId;
    public String targetPinId;

    public ConnectionModel(String sourceNodeId, String sourcePinId, String targetNodeId, String targetPinId) {
        this.connectionId = sourceNodeId + ":" + sourcePinId + "->" + targetNodeId + ":" + targetPinId;
        this.sourceNodeId = sourceNodeId;
        this.sourcePinId = sourcePinId;
        this.targetNodeId = targetNodeId;
        this.targetPinId = targetPinId;
    }
}
