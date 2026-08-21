package com.atomgdx.editor.scene2d.tilemap.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 3x3 Neighbor Adjacency Bitmask Auto-Tiling Rule (Unity Rule Tile / Wang Terrain matching).
 *
 * 8-Neighbor Bitmask Flags:
 *   TOP_LEFT=1,     TOP=2,     TOP_RIGHT=4,
 *   LEFT=8,                    RIGHT=16,
 *   BOTTOM_LEFT=32, BOTTOM=64, BOTTOM_RIGHT=128
 */
public class AutoTileRule implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TOP_LEFT     = 1 << 0; // 1
    public static final int TOP          = 1 << 1; // 2
    public static final int TOP_RIGHT    = 1 << 2; // 4
    public static final int LEFT         = 1 << 3; // 8
    public static final int RIGHT        = 1 << 4; // 16
    public static final int BOTTOM_LEFT  = 1 << 5; // 32
    public static final int BOTTOM       = 1 << 6; // 64
    public static final int BOTTOM_RIGHT = 1 << 7; // 128

    public static final int FOUR_CARDINAL = TOP | LEFT | RIGHT | BOTTOM; // 2 + 8 + 16 + 64 = 90
    public static final int ALL_EIGHT     = 0xFF; // 255

    private String name = "Terrain AutoTile";
    private int defaultTileGid = 1;
    // Map bitmask -> target Tile GID
    private final Map<Integer, Integer> bitmaskToTileGid = new HashMap<>();

    public AutoTileRule() {
        initStandardPresets();
    }

    public AutoTileRule(String name, int defaultGid) {
        this.name = name;
        this.defaultTileGid = defaultGid;
        initStandardPresets();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getDefaultTileGid() { return defaultTileGid; }
    public void setDefaultTileGid(int gid) { this.defaultTileGid = gid; }

    public void mapRule(int bitmask, int targetGid) {
        bitmaskToTileGid.put(bitmask, targetGid);
    }

    public int resolveTile(int bitmask) {
        Integer gid = bitmaskToTileGid.get(bitmask);
        if (gid != null) return gid;

        // Fallback to cardinal 4-way bitmask if exact 8-way not mapped
        int cardinal = bitmask & FOUR_CARDINAL;
        gid = bitmaskToTileGid.get(cardinal);
        if (gid != null) return gid;

        return defaultTileGid;
    }

    public Map<Integer, Integer> getBitmaskRules() {
        return bitmaskToTileGid;
    }

    private void initStandardPresets() {
        // Center full surrounded (255 / 90)
        bitmaskToTileGid.put(ALL_EIGHT, defaultTileGid);
        bitmaskToTileGid.put(FOUR_CARDINAL, defaultTileGid);

        // Single isolated (0)
        bitmaskToTileGid.put(0, defaultTileGid);

        // Top edge (LEFT + RIGHT + BOTTOM)
        bitmaskToTileGid.put(LEFT | RIGHT | BOTTOM, defaultTileGid + 1);

        // Bottom edge (TOP + LEFT + RIGHT)
        bitmaskToTileGid.put(TOP | LEFT | RIGHT, defaultTileGid + 2);

        // Left edge (TOP + RIGHT + BOTTOM)
        bitmaskToTileGid.put(TOP | RIGHT | BOTTOM, defaultTileGid + 3);

        // Right edge (TOP + LEFT + BOTTOM)
        bitmaskToTileGid.put(TOP | LEFT | BOTTOM, defaultTileGid + 4);

        // Top-Left Corner (RIGHT + BOTTOM)
        bitmaskToTileGid.put(RIGHT | BOTTOM, defaultTileGid + 5);

        // Top-Right Corner (LEFT + BOTTOM)
        bitmaskToTileGid.put(LEFT | BOTTOM, defaultTileGid + 6);

        // Bottom-Left Corner (TOP + RIGHT)
        bitmaskToTileGid.put(TOP | RIGHT, defaultTileGid + 7);

        // Bottom-Right Corner (TOP + LEFT)
        bitmaskToTileGid.put(TOP | LEFT, defaultTileGid + 8);
    }

    /**
     * Computes the 8-neighbor bitmask at (x, y) in a TileLayer where matching tiles are non-zero.
     */
    public static int calculateBitmask(TileLayer layer, int x, int y) {
        int mask = 0;
        if (isMatch(layer, x - 1, y - 1)) mask |= TOP_LEFT;
        if (isMatch(layer, x, y - 1))     mask |= TOP;
        if (isMatch(layer, x + 1, y - 1)) mask |= TOP_RIGHT;
        if (isMatch(layer, x - 1, y))     mask |= LEFT;
        if (isMatch(layer, x + 1, y))     mask |= RIGHT;
        if (isMatch(layer, x - 1, y + 1)) mask |= BOTTOM_LEFT;
        if (isMatch(layer, x, y + 1))     mask |= BOTTOM;
        if (isMatch(layer, x + 1, y + 1)) mask |= BOTTOM_RIGHT;
        return mask;
    }

    private static boolean isMatch(TileLayer layer, int x, int y) {
        if (layer == null) return false;
        return layer.getTile(x, y) > 0;
    }
}
