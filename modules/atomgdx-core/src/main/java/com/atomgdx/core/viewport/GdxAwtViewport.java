package com.atomgdx.core.viewport;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable Swing host panel for LibGDX OpenGL viewports powered by LwjglAWTCanvas.
 */
public class GdxAwtViewport extends JPanel {

    private LwjglAWTCanvas canvas;
    private final ApplicationListener listener;

    public GdxAwtViewport(ApplicationListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        initCanvas();
    }

    private void initCanvas() {
        try {
            canvas = new LwjglAWTCanvas(listener);
            add(canvas.getCanvas(), BorderLayout.CENTER);
        } catch (Throwable t) {
            System.err.println("GdxAwtViewport fallback: " + t.getMessage());
            JLabel fallback = new JLabel("LibGDX OpenGL Viewport: " + t.getMessage(), SwingConstants.CENTER);
            fallback.setForeground(Color.CYAN);
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
