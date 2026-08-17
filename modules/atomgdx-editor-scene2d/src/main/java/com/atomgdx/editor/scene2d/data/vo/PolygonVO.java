package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;

/**
 * 2D Polygon vertices for physics fixtures and custom mesh boundaries.
 */
public class PolygonVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public Vector2DVO[] vertices = new Vector2DVO[0];

    public static class Vector2DVO implements Serializable {
        private static final long serialVersionUID = 1L;
        public float x;
        public float y;

        public Vector2DVO() {}
        public Vector2DVO(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
