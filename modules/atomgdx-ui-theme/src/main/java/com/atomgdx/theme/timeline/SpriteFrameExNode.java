package com.atomgdx.theme.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hierarchical Sprite Node for SpriteFrames Ex (Paperdoll / multi-part character animations).
 */
public class SpriteFrameExNode implements Serializable {
    private static final long serialVersionUID = 1L;

    public String nodeName;
    public String textureAtlasPath;
    public String regionPrefix;
    public int currentFrameIndex = 0;
    public int totalFrames = 8;
    public float frameDurationSec = 0.1f;
    public float offsetX = 0f;
    public float offsetY = 0f;
    public float rotation = 0f;
    public int zOrder = 0;
    public boolean flipX = false;
    public boolean flipY = false;

    public final List<SpriteFrameExNode> childNodes = new ArrayList<>();

    public SpriteFrameExNode(String nodeName, String regionPrefix) {
        this.nodeName = nodeName;
        this.regionPrefix = regionPrefix;
    }
}
