package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;

/**
 * Value Object representing a layer in a scene.
 */
public class LayerItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String layerName = "Default Layer";
    public boolean isVisible = true;
    public boolean isLocked = false;

    public LayerItemVO() {}

    public LayerItemVO(String layerName) {
        this.layerName = layerName;
    }
}
