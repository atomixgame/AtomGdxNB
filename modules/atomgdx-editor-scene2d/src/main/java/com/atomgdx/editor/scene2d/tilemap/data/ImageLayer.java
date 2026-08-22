package com.atomgdx.editor.scene2d.tilemap.data;

/**
 * Image layer for parallax background and foreground scenery.
 */
public class ImageLayer extends TilemapLayer {
    private static final long serialVersionUID = 1L;

    private String imagePath = "";
    private float offsetX = 0;
    private float offsetY = 0;
    private boolean repeatX = false;
    private boolean repeatY = false;

    public ImageLayer(String name, String imagePath) {
        this(name, imagePath, 0, 0);
    }

    public ImageLayer(String name, String imagePath, float offsetX, float offsetY) {
        super(name, LayerType.IMAGE_LAYER);
        this.imagePath = imagePath;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String path) { this.imagePath = path; }
    public float getOffsetX() { return offsetX; }
    public void setOffsetX(float offsetX) { this.offsetX = offsetX; }
    public float getOffsetY() { return offsetY; }
    public void setOffsetY(float offsetY) { this.offsetY = offsetY; }
    public boolean isRepeatX() { return repeatX; }
    public void setRepeatX(boolean repeatX) { this.repeatX = repeatX; }
    public boolean isRepeatY() { return repeatY; }
    public void setRepeatY(boolean repeatY) { this.repeatY = repeatY; }
}
