package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.viewport.GdxAwtViewport;
import com.atomgdx.viewer3d.Model3DDescriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * 3D Model & GLTF/GLB Viewer panel powered by native LibGDX OpenGL 3D pipeline.
 * Features orbit camera controls, PBR environment lighting, and animation player.
 */
public class Model3DViewerPanel extends JPanel {

    private final Model3DDescriptor descriptor;
    private final Model3DViewportListener viewportListener;
    private final GdxAwtViewport gdxViewport;

    private int lastMouseX, lastMouseY;
    private boolean isOrbiting = false;
    private boolean isPanning = false;

    public Model3DViewerPanel(Model3DDescriptor descriptor) {
        this.descriptor = descriptor;
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        File file = descriptor != null ? descriptor.getModelFile() : null;
        viewportListener = new Model3DViewportListener(file);
        gdxViewport = new GdxAwtViewport(viewportListener);

        // Setup mouse controls for 3D Viewport
        setupMouseInteractions();

        // Top Toolbar
        JToolBar toolbar = createToolBar();

        // Right Inspector Panel
        JPanel inspector = createInspector();

        add(toolbar, BorderLayout.NORTH);
        add(gdxViewport, BorderLayout.CENTER);
        add(inspector, BorderLayout.EAST);
    }

    public int getRenderedFrameCount() {
        return viewportListener != null ? viewportListener.getFrameCount() : 0;
    }

    public Model3DViewportListener getViewportListener() {
        return viewportListener;
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(DarkThemeUtils.BG_HEADER);
        tb.setBorder(new LineBorder(DarkThemeUtils.BORDER, 1));

        JToggleButton gridBtn = new JToggleButton("Grid & Axes", true);
        JToggleButton pbrBtn = new JToggleButton("PBR Lighting", true);
        JButton resetCamBtn = new JButton("Reset Camera");

        for (AbstractButton b : new AbstractButton[]{gridBtn, pbrBtn, resetCamBtn}) {
            b.setBackground(DarkThemeUtils.BG_HEADER);
            b.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
        }

        gridBtn.addActionListener(e -> viewportListener.setShowGrid(gridBtn.isSelected()));
        pbrBtn.addActionListener(e -> viewportListener.setPbrLighting(pbrBtn.isSelected()));
        resetCamBtn.addActionListener(e -> viewportListener.resetCamera());

        tb.add(gridBtn);
        tb.add(pbrBtn);
        tb.addSeparator();
        tb.add(resetCamBtn);

        return tb;
    }

    private JPanel createInspector() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(240, 500));
        side.setBackground(DarkThemeUtils.BG_PANEL);
        side.setBorder(new LineBorder(DarkThemeUtils.BORDER, 1));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel propBox = new JPanel(new GridLayout(0, 1, 2, 2));
        propBox.setOpaque(false);
        propBox.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(DarkThemeUtils.BORDER, 1),
                "Model Statistics",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 11),
                DarkThemeUtils.TEXT_PRIMARY
        ));

        String name = (descriptor != null && descriptor.getModelFile() != null) ? descriptor.getModelFile().getName() : "Spacecraft_Mesh";
        int meshes = descriptor != null ? descriptor.getMeshCount() : 1;
        int nodes = descriptor != null ? descriptor.getNodeCount() : 1;

        propBox.add(createPropRow("Name:", name));
        propBox.add(createPropRow("Meshes:", String.valueOf(meshes)));
        propBox.add(createPropRow("Nodes:", String.valueOf(nodes)));
        propBox.add(createPropRow("Materials:", "1 (PBR)"));
        propBox.add(createPropRow("Shading:", "Standard Metallic-Roughness"));

        side.add(propBox);
        side.add(Box.createVerticalGlue());

        return side;
    }

    private JPanel createPropRow(String label, String value) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(75, 20));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        val.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        r.add(lbl, BorderLayout.WEST);
        r.add(val, BorderLayout.CENTER);
        return r;
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
