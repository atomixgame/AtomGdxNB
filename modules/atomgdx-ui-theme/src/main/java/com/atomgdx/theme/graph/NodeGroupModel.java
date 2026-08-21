package com.atomgdx.theme.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Visual Comment / Group Box enclosing a set of related nodes (Unreal Blueprint / Blender style).
 */
public class NodeGroupModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public String groupId;
    public String title = "Subsystem Group";
    public int posX = 50;
    public int posY = 50;
    public int width = 480;
    public int height = 320;
    public int colorRgb = 0x2A3240; // Slate dark backdrop
    public final List<String> nodeIds = new ArrayList<>();

    public NodeGroupModel() {
        this("group_" + System.currentTimeMillis(), "Comment Group", 50, 50, 480, 320, 0x2A3240);
    }

    public NodeGroupModel(String groupId, String title, int x, int y, int w, int h, int colorRgb) {
        this.groupId = groupId;
        this.title = title;
        this.posX = x;
        this.posY = y;
        this.width = w;
        this.height = h;
        this.colorRgb = colorRgb;
    }
}
