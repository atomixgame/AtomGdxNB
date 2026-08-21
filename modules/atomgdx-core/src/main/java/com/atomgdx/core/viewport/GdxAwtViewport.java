package com.atomgdx.core.viewport;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Reusable Swing host panel for LibGDX OpenGL viewports powered by LwjglAWTCanvas.
 * Ensures 100% edge-to-edge fullscreen canvas rendering without dead margins or gaps.
 */
public class GdxAwtViewport extends JPanel {

    private LwjglAWTCanvas canvas;
    private final ApplicationListener listener;

    public GdxAwtViewport(ApplicationListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(20, 21, 23));
        setBorder(null);

        LwjglNativesLoader.load();
        initCanvas();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                stretchCanvasToFit();
            }
        });
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                stretchCanvasToFit();
            }
        });
    }

    private void initCanvas() {
        try {
            canvas = new LwjglAWTCanvas(listener);
            Canvas awtCanvas = canvas.getCanvas();
            awtCanvas.setBackground(new Color(20, 21, 23));
            add(awtCanvas, BorderLayout.CENTER);
            stretchCanvasToFit();
            revalidate();
            repaint();
        } catch (Throwable t) {
            System.err.println("GdxAwtViewport fallback: " + t.getMessage());
            t.printStackTrace();
            JLabel fallback = new JLabel("LibGDX OpenGL Viewport Initializing...", SwingConstants.CENTER);
            fallback.setForeground(new Color(53, 116, 240));
            add(fallback, BorderLayout.CENTER);
        }
    }

    public void stretchCanvasToFit() {
        if (canvas != null && canvas.getCanvas() != null) {
            int w = getWidth();
            int h = getHeight();
            if (w > 0 && h > 0) {
                Canvas awtCanvas = canvas.getCanvas();
                awtCanvas.setBounds(0, 0, w, h);
                awtCanvas.setSize(w, h);
                awtCanvas.setPreferredSize(new Dimension(w, h));
                awtCanvas.setMinimumSize(new Dimension(w, h));
                awtCanvas.setMaximumSize(new Dimension(w, h));
                awtCanvas.validate();
            }
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        stretchCanvasToFit();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        g2.setColor(new Color(25, 26, 29));
        g2.fillRect(0, 0, w, h);

        // Subtle grid lines
        g2.setColor(new Color(38, 40, 44));
        int gridSize = 32;
        for (int x = 0; x < w; x += gridSize) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += gridSize) {
            g2.drawLine(0, y, w, y);
        }

        // Center Origin Axes (Red X, Green Y)
        int cx = w / 2;
        int cy = h / 2;
        g2.setColor(new Color(220, 50, 50, 180));
        g2.drawLine(0, cy, w, cy);
        g2.setColor(new Color(50, 200, 70, 180));
        g2.drawLine(cx, 0, cx, h);

        g2.dispose();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }

    public LwjglAWTCanvas getCanvas() {
        return canvas;
    }

    public void stop() {
        if (canvas != null) {
            try {
                canvas.stop();
            } catch (Throwable ignored) {
            }
        }
    }
}
