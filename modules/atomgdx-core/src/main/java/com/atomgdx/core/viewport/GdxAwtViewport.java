package com.atomgdx.core.viewport;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Reusable Swing host panel for LibGDX OpenGL viewports powered by LwjglAWTCanvas.
 * Ensures edge-to-edge fullscreen canvas rendering without forced sizes or gaps.
 */
public class GdxAwtViewport extends JPanel {

    private LwjglAWTCanvas canvas;
    private final ApplicationListener listener;

    public GdxAwtViewport(ApplicationListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(20, 21, 23));

        LwjglNativesLoader.load();
        initCanvas();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (canvas != null && canvas.getCanvas() != null) {
                    canvas.getCanvas().setSize(getSize());
                }
            }
        });
    }

    private void initCanvas() {
        try {
            canvas = new LwjglAWTCanvas(listener);
            Canvas awtCanvas = canvas.getCanvas();
            awtCanvas.setBackground(new Color(20, 21, 23));
            add(awtCanvas, BorderLayout.CENTER);
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
