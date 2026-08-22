package com.atomgdx.editor.scene2d.tilemap.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tileset definition containing image source, tile metrics, auto-tiling rules, animated tiles, and collisions.
 * Supports external TSX references, custom tile offsets for isometric elevation, and per-tile properties.
 */
public class TileSetVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name = "DefaultTileset";
    public int firstGid = 1;
    public String imageSource = "tilesets/scifi_station_tiles.png";
    public String sourceTsx = null; // Path to external .tsx file if referenced
    public int imageWidth = 512;
    public int imageHeight = 512;
    public int tileWidth = 32;
    public int tileHeight = 32;
    public int spacing = 0;
    public int margin = 0;
    public int tileCount = 256;
    public int columns = 16;
    public int tileOffsetX = 0;
    public int tileOffsetY = 0;

    public final List<AutoTileRule> autoTileRules = new ArrayList<>();
    public final Map<Integer, AnimatedTileVO> animatedTiles = new HashMap<>();
    public final Map<Integer, PropertyMap> perTileProperties = new HashMap<>();
    public final Map<Integer, float[]> tileCollisionBoxes = new HashMap<>();

    public TileSetVO() {}

    public TileSetVO(String name, int firstGid, String imageSource, int tileWidth, int tileHeight) {
        this.name = name;
        this.firstGid = firstGid;
        this.imageSource = imageSource;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public boolean containsGid(int gid) {
        return gid >= firstGid && gid < (firstGid + tileCount);
    }
}
