package com.atomgdx.editor.scene2d.tilemap.data;

/**
 * Image layer for parallax background and foreground scenery.
 */
public class ImageLayer extends TilemapLayer {
    private static final long serialVersionUID = 1L;

    private String imagePath = "";
    private boolean repeatX = false;
    private boolean repeatY = false;

    public ImageLayer(String name, String imagePath) {
        super(name, LayerType.IMAGE_LAYER);
        this.imagePath = imagePath;
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String path) { this.imagePath = path; }
    public boolean isRepeatX() { return repeatX; }
    public void setRepeatX(boolean repeatX) { this.repeatX = repeatX; }
    public boolean isRepeatY() { return repeatY; }
    public void setRepeatY(boolean repeatY) { this.repeatY = repeatY; }
}
