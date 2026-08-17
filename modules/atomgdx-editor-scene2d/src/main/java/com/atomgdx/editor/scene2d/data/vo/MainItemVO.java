package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Value Object for any item placed in a HyperLap2D scene.
 */
public abstract class MainItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public int uniqueId = -1;
    public String itemIdentifier = "";
    public String itemName = "";
    public String[] tags = new String[0];
    public String customVars = "";

    public float x = 0f;
    public float y = 0f;
    public float scaleX = 1f;
    public float scaleY = 1f;
    public float originX = 0f;
    public float originY = 0f;
    public float rotation = 0f;
    public int zIndex = 0;
    public String layerName = "Default Layer";

    public ColorDataVO tintColor = new ColorDataVO(1f, 1f, 1f, 1f);
    public PhysicsBodyDataVO physics = null;
    public ShapeVO shape = null;
    public String shaderName = "";

    public boolean isVisible = true;
    public boolean isLocked = false;

    public Map<String, String> parseCustomVars() {
        Map<String, String> map = new HashMap<>();
        if (customVars != null && !customVars.isEmpty()) {
            String[] lines = customVars.split(";");
            for (String line : lines) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return map;
    }
}
