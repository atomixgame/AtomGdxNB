package com.atomgdx.tools.texturepacker;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 * Data model for configuring 9-patch stretchable sprite margins, padding, and mesh patch geometry.
 */
public class NinePatchEditorModel implements Serializable {
    private static final long serialVersionUID = 1L;

    // Stretch margins (in pixels from edge)
    private int left = 12;
    private int right = 12;
    private int top = 12;
    private int bottom = 12;

    // Optional content padding margins
    private int padLeft = -1;
    private int padRight = -1;
    private int padTop = -1;
    private int padBottom = -1;

    public NinePatchEditorModel() {
        this(12, 12, 12, 12);
    }

    public NinePatchEditorModel(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public int getLeft() { return left; }
    public void setLeft(int left) { this.left = left; }

    public int getRight() { return right; }
    public void setRight(int right) { this.right = right; }

    public int getTop() { return top; }
    public void setTop(int top) { this.top = top; }

    public int getBottom() { return bottom; }
    public void setBottom(int bottom) { this.bottom = bottom; }

    public int getPadLeft() { return padLeft; }
    public void setPadLeft(int padLeft) { this.padLeft = padLeft; }

    public int getPadRight() { return padRight; }
    public void setPadRight(int padRight) { this.padRight = padRight; }

    public int getPadTop() { return padTop; }
    public void setPadTop(int padTop) { this.padTop = padTop; }

    public int getPadBottom() { return padBottom; }
    public void setPadBottom(int padBottom) { this.padBottom = padBottom; }

    public boolean isValid(int imageWidth, int imageHeight) {
        return left >= 0 && right >= 0 && top >= 0 && bottom >= 0
                && (left + right) < imageWidth
                && (top + bottom) < imageHeight;
    }

    /**
     * Computes the 9 sub-rectangles for rendering a 9-patch scaled to (targetW, targetH).
     * Order:
     * [0] Top-Left,    [1] Top-Center,    [2] Top-Right
     * [3] Middle-Left, [4] Center,        [5] Middle-Right
     * [6] Bottom-Left, [7] Bottom-Center, [8] Bottom-Right
     */
    public Rectangle[] calculateDestPatches(int targetW, int targetH) {
        Rectangle[] rects = new Rectangle[9];
        int centerW = Math.max(0, targetW - left - right);
        int centerH = Math.max(0, targetH - top - bottom);

        int x0 = 0;
        int x1 = left;
        int x2 = left + centerW;

        int y0 = 0;
        int y1 = top;
        int y2 = top + centerH;

        // Row 0 (Top)
        rects[0] = new Rectangle(x0, y0, left, top);
        rects[1] = new Rectangle(x1, y0, centerW, top);
        rects[2] = new Rectangle(x2, y0, right, top);

        // Row 1 (Middle)
        rects[3] = new Rectangle(x0, y1, left, centerH);
        rects[4] = new Rectangle(x1, y1, centerW, centerH);
        rects[5] = new Rectangle(x2, y1, right, centerH);

        // Row 2 (Bottom)
        rects[6] = new Rectangle(x0, y2, left, bottom);
        rects[7] = new Rectangle(x1, y2, centerW, bottom);
        rects[8] = new Rectangle(x2, y2, right, bottom);

        return rects;
    }
}
