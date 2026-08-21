package com.atomgdx.editor.scene2d.tilemap.physics;

import com.atomgdx.editor.scene2d.tilemap.data.TileLayer;
import com.atomgdx.editor.scene2d.tilemap.data.TilemapDocument;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates optimized Box2D physics collision contours (Unity CompositeCollider2D equivalent)
 * by merging contiguous tile boundaries to eliminate internal seam snags.
 */
public class TilemapCollisionGenerator {

    public static class CollisionPolygon {
        public final List<float[]> vertices = new ArrayList<>();
        public boolean isSensor = false;
        public float friction = 0.2f;
        public float restitution = 0.0f;

        public void addVertex(float x, float y) {
            vertices.add(new float[]{x, y});
        }
    }

    /**
     * Generates merged rectangular bounding boxes from solid non-zero cells in a TileLayer.
     */
    public static List<Rectangle> generateMergedBoxColliders(TileLayer layer, int tileW, int tileH) {
        List<Rectangle> result = new ArrayList<>();
        if (layer == null) return result;

        int w = layer.getWidth();
        int h = layer.getHeight();
        boolean[][] visited = new boolean[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (layer.getTile(x, y) > 0 && !visited[y][x]) {
                    // Find max contiguous width in this row
                    int rw = 0;
                    while ((x + rw) < w && layer.getTile(x + rw, y) > 0 && !visited[y][x + rw]) {
                        rw++;
                    }

                    // Find max contiguous height for this width
                    int rh = 1;
                    boolean canExpand = true;
                    while ((y + rh) < h && canExpand) {
                        for (int k = 0; k < rw; k++) {
                            if (layer.getTile(x + k, y + rh) == 0 || visited[y + rh][x + k]) {
                                canExpand = false;
                                break;
                            }
                        }
                        if (canExpand) rh++;
                    }

                    // Mark visited
                    for (int dy = 0; dy < rh; dy++) {
                        for (int dx = 0; dx < rw; dx++) {
                            visited[y + dy][x + dx] = true;
                        }
                    }

                    // Add merged rectangle
                    result.add(new Rectangle(x * tileW, y * tileH, rw * tileW, rh * tileH));
                }
            }
        }

        return result;
    }

    /**
     * Converts merged collision rectangles into Box2D polygon definitions.
     */
    public static List<CollisionPolygon> generateBox2DPolygons(TileLayer layer, int tileW, int tileH) {
        List<Rectangle> boxes = generateMergedBoxColliders(layer, tileW, tileH);
        List<CollisionPolygon> polys = new ArrayList<>();

        for (Rectangle box : boxes) {
            CollisionPolygon poly = new CollisionPolygon();
            poly.addVertex(box.x, box.y);
            poly.addVertex(box.x + box.width, box.y);
            poly.addVertex(box.x + box.width, box.y + box.height);
            poly.addVertex(box.x, box.y + box.height);
            polys.add(poly);
        }

        return polys;
    }
}
