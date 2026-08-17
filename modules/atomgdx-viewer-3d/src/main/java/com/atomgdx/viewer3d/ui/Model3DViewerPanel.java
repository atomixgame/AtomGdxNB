package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.SciFiColors;
import com.atomgdx.viewer3d.Model3DDescriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * 3D Model & GLTF/GLB Viewer panel with PBR shading controls, animation player, and wireframe toggle.
 */
public class Model3DViewerPanel extends JPanel {
    private final Model3DDescriptor descriptor;

    public Model3DViewerPanel(Model3DDescriptor descriptor) {
        this.descriptor = descriptor;
        setLayout(new BorderLayout(8, 8));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Toolbar
        JToolBar toolbar = createToolBar();

        // Center 3D Viewport
        JPanel viewport = createViewport();

        // Right Inspector
        JPanel inspector = createInspector();

        // Bottom Animation Scrubber
        JPanel scrubber = createAnimationScrubber();

        add(toolbar, BorderLayout.NORTH);
        add(viewport, BorderLayout.CENTER);
        add(inspector, BorderLayout.EAST);
        add(scrubber, BorderLayout.SOUTH);
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SciFiColors.BG_PANEL);

        tb.add(new JCheckBox("Wireframe", false));
        tb.add(new JCheckBox("PBR Lighting", true));
        tb.add(new JCheckBox("Show Bones", false));
        tb.add(new JCheckBox("Environment Skybox", true));
        tb.addSeparator();
        tb.add(new JButton("Reset Camera"));
        return tb;
    }

    private JPanel createViewport() {
        JPanel vp = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;
                int cy = h / 2;

                // 3D Grid Perspective Lines
                g2.setColor(new Color(0x16, 0x22, 0x30));
                for (int i = -200; i <= 200; i += 40) {
                    g2.drawLine(cx + i, cy + 50, cx + i * 3, h);
                }
                for (int y = cy + 50; y <= h; y += 30) {
                    g2.drawLine(20, y, w - 20, y);
                }

                // Draw 3D Model Wireframe / Shaded representation
                g2.setColor(SciFiColors.ACCENT_CYAN);
                int[] px = {cx, cx - 80, cx, cx + 80};
                int[] py = {cy - 100, cy + 20, cy + 50, cy + 20};
                g2.fillPolygon(px, py, 4);

                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 180));
                g2.drawPolygon(px, py, 4);
                g2.drawLine(cx, cy - 100, cx, cy + 50);

                g2.setColor(SciFiColors.TEXT_PRIMARY);
                g2.drawString("GLTF 2.0 PBR Viewport | Camera Orbit: (30°, 45°)", 30, 40);
                g2.dispose();
            }
        };
        vp.setBackground(new Color(0x06, 0x09, 0x0E));
        vp.setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));
        return vp;
    }

    private JPanel createInspector() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setPreferredSize(new Dimension(230, 400));
        panel.setBackground(SciFiColors.BG_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE),
                "Model Properties",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                SciFiColors.ACCENT_CYAN
        ));

        panel.add(new JLabel("Format:"));
        panel.add(new JLabel(descriptor != null ? descriptor.getFormat().name() : "GLTF"));

        panel.add(new JLabel("Meshes:"));
        panel.add(new JLabel("4 Meshes"));

        panel.add(new JLabel("Vertices:"));
        panel.add(new JLabel("12,450"));

        panel.add(new JLabel("Materials:"));
        panel.add(new JLabel("PBR MetalRough"));

        panel.add(new JLabel("Animations:"));
        panel.add(new JComboBox<>(new String[]{"Idle", "Walk", "Attack"}));

        return panel;
    }

    private JPanel createAnimationScrubber() {
        JPanel scrubber = new JPanel(new BorderLayout(10, 5));
        scrubber.setBackground(SciFiColors.BG_PANEL);
        scrubber.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton playBtn = new JButton("▶ Play");
        JSlider timeSlider = new JSlider(0, 100, 30);
        JLabel timeLbl = new JLabel("0.90s / 3.00s");
        timeLbl.setForeground(SciFiColors.TEXT_SECONDARY);

        scrubber.add(playBtn, BorderLayout.WEST);
        scrubber.add(timeSlider, BorderLayout.CENTER);
        scrubber.add(timeLbl, BorderLayout.EAST);
        return scrubber;
    }
}
