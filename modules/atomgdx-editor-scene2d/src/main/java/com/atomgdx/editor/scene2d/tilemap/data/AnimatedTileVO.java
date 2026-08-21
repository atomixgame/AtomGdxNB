package com.atomgdx.editor.scene2d.tilemap.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Animated Tile definition containing a sequence of tile GID frames and millisecond durations.
 */
public class AnimatedTileVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Frame implements Serializable {
        public int tileGid;
        public int durationMs;

        public Frame(int tileGid, int durationMs) {
            this.tileGid = tileGid;
            this.durationMs = durationMs;
        }
    }

    public int tileId;
    public final List<Frame> frames = new ArrayList<>();

    public AnimatedTileVO() {}

    public AnimatedTileVO(int tileId) {
        this.tileId = tileId;
    }

    public void addFrame(int tileGid, int durationMs) {
        frames.add(new Frame(tileGid, durationMs));
    }

    public int getCurrentFrameGid(long elapsedTimeMs) {
        if (frames.isEmpty()) return tileId;
        int total = 0;
        for (Frame f : frames) total += f.durationMs;
        if (total <= 0) return frames.get(0).tileGid;

        long mod = elapsedTimeMs % total;
        long accum = 0;
        for (Frame f : frames) {
            accum += f.durationMs;
            if (mod < accum) return f.tileGid;
        }
        return frames.get(0).tileGid;
    }
}
