package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Interactive MiniMap / MiniView overlay component for Visual Graph Editors.
 * Provides live bird-eye overview, scaled node representations, and draggable camera viewport rect.
 */
public class GraphMiniMapPanel extends JPanel {

    private final AtomVisualGraphScene scene;
    private final JScrollPane scrollPane;

    public GraphMiniMapPanel(AtomVisualGraphScene scene, JScrollPane scrollPane) {
        this.scene = scene;
        this.scrollPane = scrollPane;

        setPreferredSize(new Dimension(180, 130));
        setSize(new Dimension(180, 130));
        setBackground(new Color(20, 22, 28, 230));
        setBorder(new LineBorder(new Color(60, 68, 85), 1, true));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClickOrDrag(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleClickOrDrag(e.getPoint());
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        // Repaint timer for smooth synchronization
        Timer timer = new Timer(100, e -> repaint());
        timer.start();
    }

    private void handleClickOrDrag(Point p) {
        Rectangle docBounds = scene.getDocumentBounds();
        if (docBounds.isEmpty() || docBounds.width <= 0 || docBounds.height <= 0) return;

        double scaleX = (double) getWidth() / (double) (docBounds.width + 400);
        double scaleY = (double) getHeight() / (double) (docBounds.height + 400);
        double scale = Math.min(scaleX, scaleY);

        int targetSceneX = docBounds.x - 200 + (int) (p.x / scale);
        int targetSceneY = docBounds.y - 200 + (int) (p.y / scale);

        scene.panToLocation(targetSceneX, targetSceneY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Title badge
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(new Color(150, 160, 180));
        g2.drawString("MINIVIEW", 8, 14);

        Rectangle docBounds = scene.getDocumentBounds();
        if (docBounds.isEmpty() || docBounds.width <= 0 || docBounds.height <= 0) {
            g2.dispose();
            return;
        }

        int pad = 200;
        int totalW = docBounds.width + pad * 2;
        int totalH = docBounds.height + pad * 2;

        double scaleX = (double) (getWidth() - 16) / (double) totalW;
        double scaleY = (double) (getHeight() - 24) / (double) totalH;
        double scale = Math.min(scaleX, scaleY);

        int ox = 8;
        int oy = 18;

        // Draw miniature node cards
        for (NodeModel node : scene.getDocument().nodes) {
            int nx = ox + (int) ((node.posX - (docBounds.x - pad)) * scale);
            int ny = oy + (int) ((node.posY - (docBounds.y - pad)) * scale);
            int nw = Math.max(8, (int) (Math.max(220, node.width) * scale));
            int nh = Math.max(6, (int) (Math.max(120, node.height) * scale));

            g2.setColor(new Color(node.headerColorRgb));
            g2.fillRoundRect(nx, ny, nw, nh, 3, 3);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(nx, ny, nw, nh, 3, 3);
        }

        // Draw camera viewport rectangle
        if (scrollPane != null && scrollPane.getViewport() != null) {
            JViewport vp = scrollPane.getViewport();
            Rectangle viewRect = vp.getViewRect();
            double zoom = scene.getZoomFactor();

            int vx = ox + (int) (((viewRect.x / zoom) - (docBounds.x - pad)) * scale);
            int vy = oy + (int) (((viewRect.y / zoom) - (docBounds.y - pad)) * scale);
            int vw = Math.max(12, (int) ((viewRect.width / zoom) * scale));
            int vh = Math.max(10, (int) ((viewRect.height / zoom) * scale));

            g2.setColor(new Color(0, 229, 255, 40));
            g2.fillRect(vx, vy, vw, vh);
            g2.setColor(new Color(0, 229, 255, 220));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRect(vx, vy, vw, vh);
        }

        g2.dispose();
    }
}
