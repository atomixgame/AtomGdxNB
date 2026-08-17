package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Shape geometry definition containing polygon vertices and circles.
 */
public class ShapeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<PolygonVO> polygons = new ArrayList<>();
    public List<CircleVO> circles = new ArrayList<>();

    public static class CircleVO implements Serializable {
        private static final long serialVersionUID = 1L;
        public float x = 0f;
        public float y = 0f;
        public float radius = 10f;
    }
}
