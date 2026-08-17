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
        setBackground(new Color(0x06, 0x08, 0x0C));
        setPreferredSize(new Dimension(640, 480));
        setMinimumSize(new Dimension(320, 240));

        LwjglNativesLoader.load();
        initCanvas();
    }

    private void initCanvas() {
        try {
            canvas = new LwjglAWTCanvas(listener);
            Canvas awtCanvas = canvas.getCanvas();
            awtCanvas.setBackground(new Color(0x06, 0x08, 0x0C));
            add(awtCanvas, BorderLayout.CENTER);
            revalidate();
            repaint();
        } catch (Throwable t) {
            System.err.println("GdxAwtViewport fallback: " + t.getMessage());
            t.printStackTrace();
            JLabel fallback = new JLabel("LibGDX OpenGL Viewport Initializing...", SwingConstants.CENTER);
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
