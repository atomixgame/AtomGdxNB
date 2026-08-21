package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Interactive 2D on-screen transform gizmo supporting Translate, Scale, and Rotate modes.
 */
public class TransformGizmo {

    public enum GizmoMode {
        TRANSLATE, SCALE, ROTATE
    }

    public enum HandleType {
        NONE, CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT, ROTATION_HANDLE
    }

    private GizmoMode mode = GizmoMode.TRANSLATE;
    private MainItemVO targetItem;

    private final Vector2 dragStartWorld = new Vector2();
    private final Vector2 itemStartPos = new Vector2();
    private final Vector2 itemStartScale = new Vector2(1f, 1f);
    private float itemStartRotation = 0f;
    private HandleType activeHandle = HandleType.NONE;

    public void setMode(GizmoMode mode) {
        this.mode = mode;
    }

    public GizmoMode getMode() {
        return mode;
    }

    public interface TextureSizeProvider {
        float[] getDimensions(String imageName);
    }

    private TextureSizeProvider textureSizeProvider;

    public void setTextureSizeProvider(TextureSizeProvider provider) {
        this.textureSizeProvider = provider;
    }

    public void setTargetItem(MainItemVO item) {
        this.targetItem = item;
    }

    public MainItemVO getTargetItem() {
        return targetItem;
    }

    public Rectangle getItemBounds() {
        if (targetItem == null) return null;
        float w = 64f;
        float h = 64f;
        if (targetItem instanceof SimpleImageVO) {
            SimpleImageVO img = (SimpleImageVO) targetItem;
            if (img.width > 0) w = img.width;
            if (img.height > 0) h = img.height;
            if ((img.width <= 0 || img.height <= 0) && textureSizeProvider != null && img.imageName != null) {
                float[] dims = textureSizeProvider.getDimensions(img.imageName);
                if (dims != null && dims.length >= 2 && dims[0] > 0 && dims[1] > 0) {
                    if (img.width <= 0) w = dims[0];
                    if (img.height <= 0) h = dims[1];
                }
            }
        } else if (targetItem instanceof NinePatchVO) {
            NinePatchVO np = (NinePatchVO) targetItem;
            if (np.width > 0) w = np.width;
            if (np.height > 0) h = np.height;
        } else if (targetItem instanceof LabelVO) {
            LabelVO lbl = (LabelVO) targetItem;
            if (lbl.width > 0) w = lbl.width;
            if (lbl.height > 0) h = lbl.height;
            if (w <= 0 && lbl.text != null) w = lbl.text.length() * 10f;
            if (h <= 0) h = 20f;
        } else if (targetItem instanceof LightVO) {
            LightVO lt = (LightVO) targetItem;
            w = lt.distance > 0 ? lt.distance * 2f : 64f;
            h = w;
        } else if (targetItem instanceof ParticleEffectVO) {
            w = 80f;
            h = 80f;
        } else if (targetItem instanceof CompositeItemVO) {
            CompositeItemVO comp = (CompositeItemVO) targetItem;
            if (comp.width > 0) w = comp.width;
            if (comp.height > 0) h = comp.height;
        }
        float sx = Math.abs(targetItem.scaleX) > 0.001f ? targetItem.scaleX : 1f;
        float sy = Math.abs(targetItem.scaleY) > 0.001f ? targetItem.scaleY : 1f;
        float ox = targetItem.originX;
        float oy = targetItem.originY;

        float posX = targetItem.x;
        float posY = targetItem.y;
        if (ox != 0 || oy != 0) {
            posX = targetItem.x + ox - ox * sx;
            posY = targetItem.y + oy - oy * sy;
        }
        return new Rectangle(posX, posY, w * sx, h * sy);
    }

    public HandleType hitTest(float worldX, float worldY, float handleSize) {
        if (targetItem == null || targetItem.isLocked || !targetItem.isVisible) return HandleType.NONE;
        Rectangle b = getItemBounds();
        if (b == null) return HandleType.NONE;

        float cx = b.x + b.width / 2f;
        float cy = b.y + b.height / 2f;

        // Rotation handle (top above bounding box)
        if (Vector2.dst(worldX, worldY, cx, b.y + b.height + 25f) <= handleSize * 1.5f) {
            return HandleType.ROTATION_HANDLE;
        }

        // 4 Corner handles
        if (Vector2.dst(worldX, worldY, b.x, b.y) <= handleSize) return HandleType.BOTTOM_LEFT;
        if (Vector2.dst(worldX, worldY, b.x + b.width, b.y) <= handleSize) return HandleType.BOTTOM_RIGHT;
        if (Vector2.dst(worldX, worldY, b.x, b.y + b.height) <= handleSize) return HandleType.TOP_LEFT;
        if (Vector2.dst(worldX, worldY, b.x + b.width, b.y + b.height) <= handleSize) return HandleType.TOP_RIGHT;

        // 4 Edge handles
        if (Vector2.dst(worldX, worldY, cx, b.y + b.height) <= handleSize) return HandleType.TOP;
        if (Vector2.dst(worldX, worldY, cx, b.y) <= handleSize) return HandleType.BOTTOM;
        if (Vector2.dst(worldX, worldY, b.x, cy) <= handleSize) return HandleType.LEFT;
        if (Vector2.dst(worldX, worldY, b.x + b.width, cy) <= handleSize) return HandleType.RIGHT;

        // Inside bounding box = Center move
        if (b.contains(worldX, worldY)) {
            return HandleType.CENTER;
        }

        return HandleType.NONE;
    }

    public void startDrag(float worldX, float worldY, HandleType handle) {
        this.activeHandle = handle;
        this.dragStartWorld.set(worldX, worldY);
        if (targetItem != null) {
            this.itemStartPos.set(targetItem.x, targetItem.y);
            this.itemStartScale.set(targetItem.scaleX != 0 ? targetItem.scaleX : 1f, targetItem.scaleY != 0 ? targetItem.scaleY : 1f);
            this.itemStartRotation = targetItem.rotation;
        }
    }

    public void updateDrag(float worldX, float worldY) {
        if (targetItem == null || activeHandle == HandleType.NONE) return;

        float dx = worldX - dragStartWorld.x;
        float dy = worldY - dragStartWorld.y;

        if (activeHandle == HandleType.ROTATION_HANDLE || mode == GizmoMode.ROTATE) {
            Rectangle b = getItemBounds();
            float cx = b.x + b.width / 2f;
            float cy = b.y + b.height / 2f;
            float angle = (float) Math.toDegrees(Math.atan2(worldY - cy, worldX - cx));
            targetItem.rotation = (float) (Math.round((angle - 90f) * 10f) / 10f);
        } else if (activeHandle == HandleType.TOP_RIGHT || mode == GizmoMode.SCALE) {
            float factorX = 1f + dx / 120f;
            float factorY = 1f + dy / 120f;
            targetItem.scaleX = Math.max(0.05f, itemStartScale.x * factorX);
            targetItem.scaleY = Math.max(0.05f, itemStartScale.y * factorY);
        } else if (activeHandle == HandleType.TOP_LEFT) {
            float factorX = 1f - dx / 120f;
            float factorY = 1f + dy / 120f;
            targetItem.scaleX = Math.max(0.05f, itemStartScale.x * factorX);
            targetItem.scaleY = Math.max(0.05f, itemStartScale.y * factorY);
        } else if (activeHandle == HandleType.BOTTOM_RIGHT) {
            float factorX = 1f + dx / 120f;
            float factorY = 1f - dy / 120f;
            targetItem.scaleX = Math.max(0.05f, itemStartScale.x * factorX);
            targetItem.scaleY = Math.max(0.05f, itemStartScale.y * factorY);
        } else if (activeHandle == HandleType.BOTTOM_LEFT) {
            float factorX = 1f - dx / 120f;
            float factorY = 1f - dy / 120f;
            targetItem.scaleX = Math.max(0.05f, itemStartScale.x * factorX);
            targetItem.scaleY = Math.max(0.05f, itemStartScale.y * factorY);
        } else if (activeHandle == HandleType.RIGHT || activeHandle == HandleType.LEFT) {
            float factorX = (activeHandle == HandleType.RIGHT) ? (1f + dx / 120f) : (1f - dx / 120f);
            targetItem.scaleX = Math.max(0.05f, itemStartScale.x * factorX);
        } else if (activeHandle == HandleType.TOP || activeHandle == HandleType.BOTTOM) {
            float factorY = (activeHandle == HandleType.TOP) ? (1f + dy / 120f) : (1f - dy / 120f);
            targetItem.scaleY = Math.max(0.05f, itemStartScale.y * factorY);
        } else { // Center move / Translate
            targetItem.x = itemStartPos.x + dx;
            targetItem.y = itemStartPos.y + dy;
        }
    }

    public void endDrag() {
        this.activeHandle = HandleType.NONE;
    }

    public void render(ShapeRenderer shapes) {
        if (targetItem == null) return;
        Rectangle b = getItemBounds();
        if (b == null) return;

        float cx = b.x + b.width / 2f;
        float cy = b.y + b.height / 2f;

        // 1. Selection bounding box (Unity Cyan/Blue)
        shapes.setColor(0.18f, 0.65f, 0.98f, 0.95f);
        shapes.rect(b.x, b.y, b.width, b.height);

        // 2. Mode Specific Gizmo Visuals
        if (mode == GizmoMode.TRANSLATE) {
            // Translate arrows: Red (X-right), Green (Y-up)
            shapes.setColor(0.95f, 0.25f, 0.25f, 1f);
            shapes.line(cx, cy, cx + 45f, cy);
            shapes.rect(cx + 40f, cy - 3f, 6f, 6f);

            shapes.setColor(0.25f, 0.95f, 0.35f, 1f);
            shapes.line(cx, cy, cx, cy + 45f);
            shapes.rect(cx - 3f, cy + 40f, 6f, 6f);

            // Center square
            shapes.setColor(0.95f, 0.85f, 0.15f, 1f);
            shapes.rect(cx - 4f, cy - 4f, 8f, 8f);
        } else if (mode == GizmoMode.ROTATE) {
            // Rotation circle ring
            shapes.setColor(0.25f, 0.95f, 0.4f, 0.85f);
            float radius = Math.max(28f, Math.max(b.width, b.height) * 0.55f);
            shapes.circle(cx, cy, radius);

            // Rotation stem and top handle
            float rx = cx;
            float ry = b.y + b.height;
            shapes.setColor(0.25f, 0.95f, 0.4f, 1f);
            shapes.line(rx, ry, rx, ry + 25f);
            shapes.circle(rx, ry + 25f, 6f);
        } else if (mode == GizmoMode.SCALE) {
            // Scale axis handles with cube heads
            shapes.setColor(0.95f, 0.25f, 0.25f, 1f);
            shapes.line(cx, cy, cx + 45f, cy);
            shapes.rect(cx + 42f, cy - 4f, 8f, 8f);

            shapes.setColor(0.25f, 0.95f, 0.35f, 1f);
            shapes.line(cx, cy, cx, cy + 45f);
            shapes.rect(cx - 4f, cy + 42f, 8f, 8f);
        }

        // 3. Rotation stem & top handle (always visible for quick rotation)
        float rx = cx;
        float ry = b.y + b.height;
        shapes.setColor(0.3f, 0.9f, 0.4f, 0.9f);
        shapes.line(rx, ry, rx, ry + 25f);
        shapes.circle(rx, ry + 25f, 5f);

        // 4. 8-Point Resize Handles (White boxes with dark borders)
        shapes.setColor(1f, 1f, 1f, 1f);
        // Corners
        shapes.rect(b.x - 4, b.y - 4, 8, 8);
        shapes.rect(b.x + b.width - 4, b.y - 4, 8, 8);
        shapes.rect(b.x - 4, b.y + b.height - 4, 8, 8);
        shapes.rect(b.x + b.width - 4, b.y + b.height - 4, 8, 8);
        // Edges
        shapes.rect(cx - 3, b.y + b.height - 3, 6, 6);
        shapes.rect(cx - 3, b.y - 3, 6, 6);
        shapes.rect(b.x - 3, cy - 3, 6, 6);
        shapes.rect(b.x + b.width - 3, cy - 3, 6, 6);

        // 5. Origin Marker (Yellow diamond/circle)
        shapes.setColor(1f, 0.8f, 0.2f, 1f);
        shapes.circle(targetItem.x + targetItem.originX, targetItem.y + targetItem.originY, 4f);
    }
}
