package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.viewport.GdxAwtViewport;
import com.atomgdx.viewer3d.data.Scene3DVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Full-featured interactive 3D Scene & Level Editor panel.
 * Features edge-to-edge hardware OpenGL canvas, Unity-inspired 3D View Gizmo,
 * Transform Gizmo modes with icons, Shading options, and Physics simulation triggers.
 */
public class Scene3DEditorPanel extends JPanel {

    private final Scene3DVO scene;
    private final Model3DViewportListener viewportListener;
    private final GdxAwtViewport gdxViewport;

    private int lastMouseX, lastMouseY;
    private boolean isOrbiting = false;
    private boolean isPanning = false;

    public Scene3DEditorPanel() {
        this(new Scene3DVO("MainScene3D"));
    }

    public Scene3DEditorPanel(Scene3DVO scene) {
        this.scene = scene != null ? scene : new Scene3DVO("MainScene3D");
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        viewportListener = new Model3DViewportListener(new File("spacecraft_cruiser.gltf"));
        gdxViewport = new GdxAwtViewport(viewportListener);

        setupMouseInteractions();

        // Top Scene Editor Toolbar with rich icons
        JToolBar toolbar = createSceneEditorToolBar();

        add(toolbar, BorderLayout.NORTH);
        add(gdxViewport, BorderLayout.CENTER);
    }

    public Scene3DVO getScene() {
        return scene;
    }

    public Model3DViewportListener getViewportListener() {
        return viewportListener;
    }

    private JToolBar createSceneEditorToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(DarkThemeUtils.BG_HEADER);
        tb.setBorder(new LineBorder(DarkThemeUtils.BORDER, 1));

        // 1. Transform Gizmo Modes with Icons
        JToggleButton selectBtn = new JToggleButton("Select (Q)", DarkThemeUtils.getFatcowIcon("cursor.png"), true);
        JToggleButton translateBtn = new JToggleButton("Move (W)", DarkThemeUtils.getFatcowIcon("arrow_out.png"), false);
        JToggleButton rotateBtn = new JToggleButton("Rotate (E)", DarkThemeUtils.getFatcowIcon("arrow_rotate_clockwise.png"), false);
        JToggleButton scaleBtn = new JToggleButton("Scale (R)", DarkThemeUtils.getFatcowIcon("arrow_inout.png"), false);

        ButtonGroup gizmoGroup = new ButtonGroup();
        for (JToggleButton b : new JToggleButton[]{selectBtn, translateBtn, rotateBtn, scaleBtn}) {
            gizmoGroup.add(b);
            styleToolbarButton(b);
            tb.add(b);
        }

        selectBtn.addActionListener(e -> viewportListener.setGizmoMode(Model3DViewportListener.GizmoMode.SELECT));
        translateBtn.addActionListener(e -> viewportListener.setGizmoMode(Model3DViewportListener.GizmoMode.TRANSLATE));
        rotateBtn.addActionListener(e -> viewportListener.setGizmoMode(Model3DViewportListener.GizmoMode.ROTATE));
        scaleBtn.addActionListener(e -> viewportListener.setGizmoMode(Model3DViewportListener.GizmoMode.SCALE));

        tb.addSeparator();

        // 2. Shading Modes with Icon
        JLabel shadeLbl = new JLabel(DarkThemeUtils.getFatcowIcon("color_wheel.png"));
        tb.add(shadeLbl);

        JComboBox<String> shadingCombo = new JComboBox<>(new String[]{"PBR Shaded", "Wireframe", "Unlit"});
        shadingCombo.setBackground(DarkThemeUtils.BG_INPUT);
        shadingCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        shadingCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        shadingCombo.addActionListener(e -> {
            int idx = shadingCombo.getSelectedIndex();
            if (idx == 1) viewportListener.setShadingMode(Model3DViewportListener.ShadingMode.WIREFRAME);
            else if (idx == 2) viewportListener.setShadingMode(Model3DViewportListener.ShadingMode.UNLIT);
            else viewportListener.setShadingMode(Model3DViewportListener.ShadingMode.SHADED_PBR);
        });
        tb.add(shadingCombo);

        tb.addSeparator();

        // 3. View Snapping Controls with Icons
        JButton topViewBtn = new JButton("Top (Y)", DarkThemeUtils.getFatcowIcon("bullet_green.png"));
        JButton frontViewBtn = new JButton("Front (Z)", DarkThemeUtils.getFatcowIcon("bullet_blue.png"));
        JButton rightViewBtn = new JButton("Right (X)", DarkThemeUtils.getFatcowIcon("bullet_red.png"));
        JButton isoViewBtn = new JButton("Iso", DarkThemeUtils.getFatcowIcon("box.png"));
        JButton resetCamBtn = new JButton("Reset", DarkThemeUtils.getFatcowIcon("camera.png"));

        for (JButton b : new JButton[]{topViewBtn, frontViewBtn, rightViewBtn, isoViewBtn, resetCamBtn}) {
            styleToolbarButton(b);
            tb.add(b);
        }

        topViewBtn.addActionListener(e -> viewportListener.snapViewTop());
        frontViewBtn.addActionListener(e -> viewportListener.snapViewFront());
        rightViewBtn.addActionListener(e -> viewportListener.snapViewRight());
        isoViewBtn.addActionListener(e -> viewportListener.snapViewIsometric());
        resetCamBtn.addActionListener(e -> viewportListener.resetCamera());

        tb.addSeparator();

        // 4. Environment & Grid Toggles with Icons
        JToggleButton gridBtn = new JToggleButton("Grid", DarkThemeUtils.getFatcowIcon("layout.png"), true);
        styleToolbarButton(gridBtn);
        gridBtn.addActionListener(e -> viewportListener.setShowGrid(gridBtn.isSelected()));
        tb.add(gridBtn);

        JToggleButton physicsBtn = new JToggleButton("Play Physics", DarkThemeUtils.getFatcowIcon("control_play_blue.png"), false);
        styleToolbarButton(physicsBtn);
        tb.add(physicsBtn);

        return tb;
    }

    private void styleToolbarButton(AbstractButton b) {
        b.setBackground(DarkThemeUtils.BG_HEADER);
        b.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setFocusPainted(false);
    }

    private void setupMouseInteractions() {
        if (gdxViewport.getCanvas() == null) return;
        Canvas canvas = gdxViewport.getCanvas().getCanvas();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isOrbiting = true;
                } else if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isOrbiting = false;
                isPanning = false;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                if (isOrbiting) {
                    viewportListener.orbit(dx * 0.5f, dy * 0.5f);
                } else if (isPanning) {
                    viewportListener.pan(dx, dy);
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            float delta = (float) e.getPreciseWheelRotation();
            viewportListener.zoom(delta * 0.8f);
        });
    }
}
