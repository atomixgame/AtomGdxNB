package com.atomgdx.viewer3d.ui;

import com.atomgdx.viewer3d.data.Node3DVO;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * 3D Interactive Transform Gizmo for OpenGL viewports (Translate, Rotate, Scale).
 */
public class TransformGizmo3D {

    public enum GizmoMode {
        TRANSLATE,
        ROTATE,
        SCALE
    }

    public enum ActiveAxis {
        NONE,
        X_AXIS,
        Y_AXIS,
        Z_AXIS,
        CENTER
    }

    private GizmoMode mode = GizmoMode.TRANSLATE;
    private ActiveAxis activeAxis = ActiveAxis.NONE;
    private Node3DVO targetNode;
    private float gizmoScale = 1.5f;

    public TransformGizmo3D() {}

    public GizmoMode getMode() { return mode; }
    public void setMode(GizmoMode mode) { this.mode = mode; }
    public ActiveAxis getActiveAxis() { return activeAxis; }
    public void setActiveAxis(ActiveAxis axis) { this.activeAxis = axis; }
    public Node3DVO getTargetNode() { return targetNode; }
    public void setTargetNode(Node3DVO node) { this.targetNode = node; }

    public void render(ShapeRenderer shapeRenderer) {
        if (targetNode == null) return;

        float x = targetNode.posX;
        float y = targetNode.posY;
        float z = targetNode.posZ;
        float len = gizmoScale;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (mode == GizmoMode.TRANSLATE || mode == GizmoMode.SCALE) {
            // X Axis - Red
            shapeRenderer.setColor(activeAxis == ActiveAxis.X_AXIS ? Color.YELLOW : Color.RED);
            shapeRenderer.line(x, y, z, x + len, y, z);

            // Y Axis - Green
            shapeRenderer.setColor(activeAxis == ActiveAxis.Y_AXIS ? Color.YELLOW : Color.GREEN);
            shapeRenderer.line(x, y, z, x, y + len, z);

            // Z Axis - Blue
            shapeRenderer.setColor(activeAxis == ActiveAxis.Z_AXIS ? Color.YELLOW : Color.BLUE);
            shapeRenderer.line(x, y, z, x, y, z + len);
        } else if (mode == GizmoMode.ROTATE) {
            // Rotate gimbal rings
            shapeRenderer.setColor(activeAxis == ActiveAxis.X_AXIS ? Color.YELLOW : Color.RED);
            shapeRenderer.circle(x, y, len * 0.8f, 32);

            shapeRenderer.setColor(activeAxis == ActiveAxis.Y_AXIS ? Color.YELLOW : Color.GREEN);
            shapeRenderer.circle(x, z, len * 0.8f, 32);
        }

        shapeRenderer.end();
    }
}
