package com.atomgdx.editor.scene2d.tilemap.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Object Group Layer holding vector MapObjectVO objects.
 */
public class ObjectGroupLayer extends TilemapLayer {
    private static final long serialVersionUID = 1L;

    private final List<MapObjectVO> objects = new ArrayList<>();

    public ObjectGroupLayer(String name) {
        super(name, LayerType.OBJECT_GROUP);
    }

    public List<MapObjectVO> getObjects() {
        return objects;
    }

    public void addObject(MapObjectVO obj) {
        objects.add(obj);
    }

    public void removeObject(MapObjectVO obj) {
        objects.remove(obj);
    }
}
