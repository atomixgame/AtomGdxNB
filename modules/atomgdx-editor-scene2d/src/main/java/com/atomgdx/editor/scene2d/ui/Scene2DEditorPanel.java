package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.core.SciFiColors;
import com.atomgdx.editor.scene2d.Scene2DModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * HyperLap2D Scene & Level Designer Viewport Canvas with transform gizmos, Box2D collider overlays, and camera grid.
 * Hierarchy and Property inspection are delegated to native NetBeans 'Scene Structure' and 'Inspector' TopComponents.
 */
public class Scene2DEditorPanel extends JPanel {
    private final Scene2DModel sceneModel;

    public Scene2DEditorPanel(Scene2DModel sceneModel) {
        this.sceneModel = sceneModel != null ? sceneModel : new Scene2DModel("New Scene");
        setLayout(new BorderLayout(8, 8));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(6, 6, 6, 6));

        // Top Scene Designer Toolbar
        JToolBar toolbar = createToolBar();

        // Center Scene Viewport Canvas
        JPanel viewport = createViewport();

        add(toolbar, BorderLayout.NORTH);
        add(viewport, BorderLayout.CENTER);
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SciFiColors.BG_PANEL);

        JButton btnSelect = new JButton("Select (V)");
        JButton btnTranslate = new JButton("Translate (W)");
        JButton btnRotate = new JButton("Rotate (E)");
        JButton btnScale = new JButton("Scale (R)");

        tb.add(btnSelect);
        tb.add(btnTranslate);
        tb.add(btnRotate);
        tb.add(btnScale);
        tb.addSeparator();

        tb.add(new JButton("+ Box2D Polygon"));
        tb.add(new JButton("+ PointLight"));
        tb.add(new JButton("+ Particle"));
        tb.addSeparator();

        JLabel info = new JLabel(" Viewport: 1920x1080 | Zoom: 100% | Cam: (0, 0) ");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(SciFiColors.TEXT_MUTED);
        tb.add(info);

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

                // Draw Grid
                g2.setColor(new Color(25, 33, 44));
                for (int x = 0; x < w; x += 32) {
                    g2.drawLine(x, 0, x, h);
                }
                for (int y = 0; y < h; y += 32) {
                    g2.drawLine(0, y, w, y);
                }

                // Origin Axes
                g2.setColor(new Color(0, 240, 255, 100));
                g2.drawLine(cx, 0, cx, h);
                g2.drawLine(0, cy, w, cy);

                // Mock Scene Render: Background Nebula
                g2.setColor(new Color(20, 10, 40));
                g2.fillRect(cx - 300, cy - 200, 600, 400);

                // Target Game Screen Frame
                g2.setColor(new Color(0, 240, 255, 180));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRect(cx - 300, cy - 200, 600, 400);

                // Mock Player Ship Sprite with Box2D Dynamic Collider
                int shipX = cx - 40;
                int shipY = cy - 20;
                g2.setColor(new Color(56, 139, 255));
                Polygon ship = new Polygon(
                        new int[]{shipX, shipX + 80, shipX},
                        new int[]{shipY - 30, shipY, shipY + 30},
                        3
                );
                g2.fill(ship);

                // Box2D Collider Outline (Green)
                g2.setColor(new Color(63, 185, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(ship);

                // PointLight Glow (Cyan)
                RadialGradientPaint p = new RadialGradientPaint(
                        new Point(shipX, shipY), 60f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 240, 255, 160), new Color(0, 240, 255, 0)}
                );
                g2.setPaint(p);
                g2.fillOval(shipX - 60, shipY - 60, 120, 120);

                // Selection Handle Gizmo
                g2.setColor(new Color(0, 240, 255));
                g2.drawRect(shipX - 10, shipY - 35, 100, 70);
                g2.fillRect(shipX - 13, shipY - 38, 6, 6);
                g2.fillRect(shipX + 87, shipY - 38, 6, 6);
                g2.fillRect(shipX - 13, shipY + 32, 6, 6);
                g2.fillRect(shipX + 87, shipY + 32, 6, 6);

                g2.dispose();
            }
        };
        vp.setBackground(new Color(10, 14, 20));
        vp.setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));
        return vp;
    }
}
