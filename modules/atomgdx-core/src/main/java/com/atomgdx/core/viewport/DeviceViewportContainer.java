package com.atomgdx.core.viewport;

import javax.swing.*;
import java.awt.*;

/**
 * Container component that hosts a LibGDX viewport and dynamically applies Unity-style
 * Device Resolution letterboxing, aspect framing, and Safe Area notch/home bar overlays.
 */
public class DeviceViewportContainer extends JPanel {

    private final Component viewportComponent;
    private final DevicePreviewToolbar toolbar;
    private final JPanel canvasWrapper;

    private DeviceResolution currentResolution;
    private DeviceResolution.Orientation currentOrientation = DeviceResolution.Orientation.LANDSCAPE;
    private float currentZoom = 1.0f;
    private boolean showSafeArea = false;

    public DeviceViewportContainer(Component viewportComponent) {
        this.viewportComponent = viewportComponent;
        this.currentResolution = DeviceResolution.getStandardPresets().get(0); // Free Aspect

        setLayout(new BorderLayout());
        setBackground(new Color(18, 19, 22));

        toolbar = new DevicePreviewToolbar();
        toolbar.setListener((res, ori, zoom, safe) -> {
            this.currentResolution = res;
            this.currentOrientation = ori;
            this.currentZoom = zoom;
            this.showSafeArea = safe;
            revalidate();
            repaint();
        });
        add(toolbar, BorderLayout.NORTH);

        canvasWrapper = new JPanel() {
            @Override
            public void doLayout() {
                int w = getWidth();
                int h = getHeight();
                if (currentResolution.isFreeAspect()) {
                    viewportComponent.setBounds(0, 0, w, h);
                } else {
                    Rectangle letterbox = currentResolution.calculateLetterboxBounds(w, h, currentOrientation);
                    if (currentZoom != 1.0f && !Float.isNaN(currentZoom)) {
                        int scaledW = Math.round(letterbox.width * currentZoom);
                        int scaledH = Math.round(letterbox.height * currentZoom);
                        int sx = (w - scaledW) / 2;
                        int sy = (h - scaledH) / 2;
                        viewportComponent.setBounds(sx, sy, scaledW, scaledH);
                    } else {
                        viewportComponent.setBounds(letterbox);
                    }
                }
            }

            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);

                // Overlay Safe Area guides & resolution tags
                if (!currentResolution.isFreeAspect()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Rectangle bounds = viewportComponent.getBounds();

                    // Letterbox frame border
                    g2.setColor(new Color(0, 220, 255, 140));
                    g2.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);

                    // Dimension badge
                    String badge = currentResolution.getEffectiveWidth(currentOrientation) + "x" +
                            currentResolution.getEffectiveHeight(currentOrientation) + " (" + currentResolution.getAspectRatio() + ")";
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    int bw = fm.stringWidth(badge) + 12;
                    int bh = 18;
                    int bx = bounds.x + 8;
                    int by = bounds.y + 8;

                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillRoundRect(bx, by, bw, bh, 6, 6);
                    g2.setColor(new Color(0, 220, 255));
                    g2.drawString(badge, bx + 6, by + 13);

                    // Safe Area overlay
                    if (showSafeArea) {
                        int notchH = currentResolution.getNotchTop();
                        int barH = currentResolution.getHomeBarBottom();
                        if (notchH > 0) {
                            int scaledNotch = Math.round(notchH * ((float) bounds.height / currentResolution.getEffectiveHeight(currentOrientation)));
                            g2.setColor(new Color(255, 60, 60, 40));
                            g2.fillRect(bounds.x, bounds.y, bounds.width, scaledNotch);
                            g2.setColor(new Color(255, 60, 60, 160));
                            g2.drawRect(bounds.x, bounds.y, bounds.width, scaledNotch);
                        }
                        if (barH > 0) {
                            int scaledBar = Math.round(barH * ((float) bounds.height / currentResolution.getEffectiveHeight(currentOrientation)));
                            int barY = bounds.y + bounds.height - scaledBar;
                            g2.setColor(new Color(255, 200, 0, 40));
                            g2.fillRect(bounds.x, barY, bounds.width, scaledBar);
                            g2.setColor(new Color(255, 200, 0, 160));
                            g2.drawRect(bounds.x, barY, bounds.width, scaledBar);
                        }
                    }

                    g2.dispose();
                }
            }
        };

        canvasWrapper.setLayout(null);
        canvasWrapper.setBackground(new Color(12, 13, 15));
        canvasWrapper.add(viewportComponent);
        add(canvasWrapper, BorderLayout.CENTER);
    }

    public DevicePreviewToolbar getToolbar() {
        return toolbar;
    }

    public DeviceResolution getCurrentResolution() {
        return currentResolution;
    }
}
