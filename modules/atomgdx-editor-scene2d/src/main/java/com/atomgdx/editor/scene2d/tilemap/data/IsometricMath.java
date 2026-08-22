package com.atomgdx.editor.scene2d.tilemap.data;

import java.awt.Point;
import java.awt.Polygon;

/**
 * High-precision mathematical transformation engine for Isometric Diamond,
 * Isometric Staggered, and Hexagonal Tilemap Projections.
 */
public class IsometricMath {

    /**
     * Converts grid tile cell (cx, cy) to Isometric Diamond screen coordinate.
     * Top vertex of cell (cx, cy) will be placed at (screenX + tileWidth / 2, screenY).
     */
    public static Point cellToScreen(int cx, int cy, int tileWidth, int tileHeight, int originX, int originY) {
        int halfW = tileWidth / 2;
        int halfH = tileHeight / 2;
        int sx = originX + (cx - cy) * halfW;
        int sy = originY + (cx + cy) * halfH;
        return new Point(sx, sy);
    }

    /**
     * Converts screen pixel (screenX, screenY) to grid tile cell (cx, cy) in Isometric Diamond projection.
     */
    public static Point screenToCell(int screenX, int screenY, int tileWidth, int tileHeight, int originX, int originY) {
        double halfW = tileWidth / 2.0;
        double halfH = tileHeight / 2.0;

        // Offset relative to center of top diamond vertex
        double rx = (screenX - originX - halfW) / halfW;
        double ry = (screenY - originY) / halfH;

        int cx = (int) Math.floor((ry + rx) / 2.0);
        int cy = (int) Math.floor((ry - rx) / 2.0);

        return new Point(cx, cy);
    }

    /**
     * Generates a 4-vertex Polygon for the Isometric Diamond cell at (cx, cy).
     */
    public static Polygon getDiamondPolygon(int cx, int cy, int tileWidth, int tileHeight, int originX, int originY) {
        Point p = cellToScreen(cx, cy, tileWidth, tileHeight, originX, originY);
        int halfW = tileWidth / 2;
        int halfH = tileHeight / 2;

        Polygon poly = new Polygon();
        // Top
        poly.addPoint(p.x + halfW, p.y);
        // Right
        poly.addPoint(p.x + tileWidth, p.y + halfH);
        // Bottom
        poly.addPoint(p.x + halfW, p.y + tileHeight);
        // Left
        poly.addPoint(p.x, p.y + halfH);

        return poly;
    }

    /**
     * Converts grid cell (cx, cy) to Isometric Staggered screen coordinate.
     */
    public static Point staggeredCellToScreen(int cx, int cy, int tileWidth, int tileHeight, int originX, int originY, boolean staggerAxisY, boolean staggerOdd) {
        int halfW = tileWidth / 2;
        int halfH = tileHeight / 2;

        if (staggerAxisY) {
            int sx = originX + cx * tileWidth + ((cy % 2 != 0) == staggerOdd ? halfW : 0);
            int sy = originY + cy * halfH;
            return new Point(sx, sy);
        } else {
            int sx = originX + cx * halfW;
            int sy = originY + cy * tileHeight + ((cx % 2 != 0) == staggerOdd ? halfH : 0);
            return new Point(sx, sy);
        }
    }

    /**
     * Converts screen pixel to grid cell in Isometric Staggered projection.
     */
    public static Point screenToStaggeredCell(int screenX, int screenY, int tileWidth, int tileHeight, int originX, int originY, boolean staggerAxisY, boolean staggerOdd) {
        int halfW = tileWidth / 2;
        int halfH = tileHeight / 2;

        if (staggerAxisY) {
            int cy = (screenY - originY) / halfH;
            boolean isStaggered = (cy % 2 != 0) == staggerOdd;
            int offset = isStaggered ? halfW : 0;
            int cx = (screenX - originX - offset) / tileWidth;
            return new Point(cx, cy);
        } else {
            int cx = (screenX - originX) / halfW;
            boolean isStaggered = (cx % 2 != 0) == staggerOdd;
            int offset = isStaggered ? halfH : 0;
            int cy = (screenY - originY - offset) / tileHeight;
            return new Point(cx, cy);
        }
    }
}
