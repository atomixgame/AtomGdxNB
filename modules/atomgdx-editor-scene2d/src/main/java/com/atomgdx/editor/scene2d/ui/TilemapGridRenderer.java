package com.atomgdx.editor.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Renders orthogonal, isometric, and hexagonal grid overlays for 2D level editing.
 */
public class TilemapGridRenderer {

    public static void renderGrid(
            ShapeRenderer shapeRenderer,
            TilemapGridMode mode,
            int tileWidth,
            int tileHeight,
            int cols,
            int rows,
            Color gridColor
    ) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(gridColor);

        if (mode == TilemapGridMode.ORTHOGONAL) {
            for (int x = 0; x <= cols; x++) {
                shapeRenderer.line(x * tileWidth, 0, x * tileWidth, rows * tileHeight);
            }
            for (int y = 0; y <= rows; y++) {
                shapeRenderer.line(0, y * tileHeight, cols * tileWidth, y * tileHeight);
            }
        } else if (mode == TilemapGridMode.ISOMETRIC_DIAMOND || mode == TilemapGridMode.ISOMETRIC_STAGGERED) {
            float halfW = tileWidth / 2f;
            float halfH = tileHeight / 2f;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    float cx = (c - r) * halfW + (rows * halfW);
                    float cy = (c + r) * halfH;

                    // Diamond outline
                    shapeRenderer.line(cx, cy + halfH, cx + halfW, cy);
                    shapeRenderer.line(cx + halfW, cy, cx, cy - halfH);
                    shapeRenderer.line(cx, cy - halfH, cx - halfW, cy);
                    shapeRenderer.line(cx - halfW, cy, cx, cy + halfH);
                }
            }
        } else if (mode == TilemapGridMode.HEXAGONAL_POINTY || mode == TilemapGridMode.HEXAGONAL_FLAT) {
            float r = tileWidth / 2f;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    float cx = col * tileWidth * 0.75f + r;
                    float cy = row * tileHeight + ((col % 2 == 1) ? tileHeight / 2f : 0f) + r;

                    for (int i = 0; i < 6; i++) {
                        double a1 = Math.toRadians(60 * i);
                        double a2 = Math.toRadians(60 * (i + 1));
                        float x1 = cx + (float) (Math.cos(a1) * r * 0.9);
                        float y1 = cy + (float) (Math.sin(a1) * r * 0.9);
                        float x2 = cx + (float) (Math.cos(a2) * r * 0.9);
                        float y2 = cy + (float) (Math.sin(a2) * r * 0.9);
                        shapeRenderer.line(x1, y1, x2, y2);
                    }
                }
            }
        }

        shapeRenderer.end();
    }
}
