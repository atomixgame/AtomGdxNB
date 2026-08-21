package com.atomgdx.editor.scene2d.tilemap.data;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Vector Object in an ObjectGroupLayer (Spawns, Lights, Triggers, Colliders, Waypoints).
 */
public class MapObjectVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ShapeType {
        RECTANGLE,
        ELLIPSE,
        POLYGON,
        POLYLINE,
        POINT
    }

    public String id = java.util.UUID.randomUUID().toString();
    public String name = "SpawnPoint";
    public String type = "PlayerStart";
    public ShapeType shape = ShapeType.RECTANGLE;
    public float x = 0;
    public float y = 0;
    public float width = 32;
    public float height = 32;
    public float rotation = 0;
    public boolean visible = true;
    public Color color = new Color(0, 220, 255);
    public final List<float[]> points = new ArrayList<>();
    public final PropertyMap properties = new PropertyMap();

    public MapObjectVO() {}

    public MapObjectVO(String name, String type, float x, float y, float w, float h) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
}
