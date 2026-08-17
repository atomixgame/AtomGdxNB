package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.editor.scene2d.data.vo.MainItemVO;
import com.atomgdx.editor.scene2d.data.vo.SimpleImageVO;
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
        }
        return new Rectangle(targetItem.x, targetItem.y, w * targetItem.scaleX, h * targetItem.scaleY);
    }

    public HandleType hitTest(float worldX, float worldY, float handleSize) {
        if (targetItem == null) return HandleType.NONE;
        Rectangle b = getItemBounds();
        if (b == null) return HandleType.NONE;

        // Rotation handle (top above bounding box)
        if (Vector2.dst(worldX, worldY, b.x + b.width / 2f, b.y + b.height + 25f) <= handleSize) {
            return HandleType.ROTATION_HANDLE;
        }

        // Corner handles
        if (Vector2.dst(worldX, worldY, b.x, b.y) <= handleSize) return HandleType.BOTTOM_LEFT;
        if (Vector2.dst(worldX, worldY, b.x + b.width, b.y) <= handleSize) return HandleType.BOTTOM_RIGHT;
        if (Vector2.dst(worldX, worldY, b.x, b.y + b.height) <= handleSize) return HandleType.TOP_LEFT;
        if (Vector2.dst(worldX, worldY, b.x + b.width, b.y + b.height) <= handleSize) return HandleType.TOP_RIGHT;

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
            this.itemStartScale.set(targetItem.scaleX, targetItem.scaleY);
            this.itemStartRotation = targetItem.rotation;
        }
    }

    public void updateDrag(float worldX, float worldY) {
        if (targetItem == null || activeHandle == HandleType.NONE) return;

        float dx = worldX - dragStartWorld.x;
        float dy = worldY - dragStartWorld.y;

        if (activeHandle == HandleType.CENTER || mode == GizmoMode.TRANSLATE) {
            targetItem.x = itemStartPos.x + dx;
            targetItem.y = itemStartPos.y + dy;
        } else if (activeHandle == HandleType.ROTATION_HANDLE || mode == GizmoMode.ROTATE) {
            Rectangle b = getItemBounds();
            float cx = b.x + b.width / 2f;
            float cy = b.y + b.height / 2f;
            float angle = (float) Math.toDegrees(Math.atan2(worldY - cy, worldX - cx));
            targetItem.rotation = angle - 90f;
        } else if (mode == GizmoMode.SCALE || activeHandle == HandleType.TOP_RIGHT) {
            float sx = Math.max(0.1f, itemStartScale.x + dx / 100f);
            float sy = Math.max(0.1f, itemStartScale.y + dy / 100f);
            targetItem.scaleX = sx;
            targetItem.scaleY = sy;
        }
    }

    public void endDrag() {
        this.activeHandle = HandleType.NONE;
    }

    public void render(ShapeRenderer shapes) {
        if (targetItem == null) return;
        Rectangle b = getItemBounds();
        if (b == null) return;

        // Selection rectangle outline
        shapes.setColor(0.2f, 0.55f, 0.95f, 0.9f);
        shapes.rect(b.x, b.y, b.width, b.height);

        // Origin marker
        shapes.setColor(1f, 0.8f, 0.2f, 1f);
        shapes.circle(targetItem.x + targetItem.originX, targetItem.y + targetItem.originY, 4f);

        // Rotation stem and handle
        float rx = b.x + b.width / 2f;
        float ry = b.y + b.height;
        shapes.setColor(0.3f, 0.9f, 0.4f, 0.9f);
        shapes.line(rx, ry, rx, ry + 25f);
        shapes.circle(rx, ry + 25f, 6f);

        // Corner handles
        shapes.setColor(1f, 1f, 1f, 1f);
        shapes.rect(b.x - 4, b.y - 4, 8, 8);
        shapes.rect(b.x + b.width - 4, b.y - 4, 8, 8);
        shapes.rect(b.x - 4, b.y + b.height - 4, 8, 8);
        shapes.rect(b.x + b.width - 4, b.y + b.height - 4, 8, 8);
    }
}
