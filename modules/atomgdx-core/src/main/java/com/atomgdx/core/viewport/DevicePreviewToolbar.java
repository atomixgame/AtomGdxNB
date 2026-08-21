package com.atomgdx.core.viewport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Reusable Unity-style Game View & Device Resolution Toolbar for 2D & 3D viewports.
 */
public class DevicePreviewToolbar extends JPanel {

    public interface DevicePreviewListener {
        void onResolutionChanged(DeviceResolution resolution, DeviceResolution.Orientation orientation, float zoomScale, boolean showSafeArea);
    }

    private final JComboBox<DeviceResolution> resolutionCombo;
    private final JToggleButton orientationToggle;
    private final JComboBox<String> zoomCombo;
    private final JCheckBox safeAreaCheckBox;
    private DevicePreviewListener listener;

    public DevicePreviewToolbar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        setBackground(new Color(28, 30, 34));
        setBorder(new EmptyBorder(2, 6, 2, 6));

        // Resolution label & combo
        JLabel resLabel = new JLabel("Display:");
        resLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        resLabel.setForeground(new Color(0, 220, 255));
        add(resLabel);

        List<DeviceResolution> presets = DeviceResolution.getStandardPresets();
        resolutionCombo = new JComboBox<>(presets.toArray(new DeviceResolution[0]));
        resolutionCombo.setPreferredSize(new Dimension(190, 24));
        resolutionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        resolutionCombo.addActionListener(e -> fireChange());
        add(resolutionCombo);

        // Orientation toggle
        orientationToggle = new JToggleButton("Landscape", false);
        orientationToggle.setPreferredSize(new Dimension(95, 24));
        orientationToggle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        orientationToggle.addActionListener(e -> {
            orientationToggle.setText(orientationToggle.isSelected() ? "Portrait" : "Landscape");
            fireChange();
        });
        add(orientationToggle);

        // Zoom scale combo
        JLabel scaleLabel = new JLabel("Scale:");
        scaleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        scaleLabel.setForeground(new Color(180, 185, 195));
        add(scaleLabel);

        zoomCombo = new JComboBox<>(new String[]{"Fit", "25%", "50%", "75%", "100%", "150%", "200%"});
        zoomCombo.setPreferredSize(new Dimension(75, 24));
        zoomCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        zoomCombo.addActionListener(e -> fireChange());
        add(zoomCombo);

        // Safe Area overlay toggle
        safeAreaCheckBox = new JCheckBox("Safe Area", false);
        safeAreaCheckBox.setOpaque(false);
        safeAreaCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        safeAreaCheckBox.setForeground(new Color(200, 205, 215));
        safeAreaCheckBox.addActionListener(e -> fireChange());
        add(safeAreaCheckBox);
    }

    public void setListener(DevicePreviewListener listener) {
        this.listener = listener;
    }

    public DeviceResolution getSelectedResolution() {
        return (DeviceResolution) resolutionCombo.getSelectedItem();
    }

    public DeviceResolution.Orientation getSelectedOrientation() {
        return orientationToggle.isSelected() ? DeviceResolution.Orientation.PORTRAIT : DeviceResolution.Orientation.LANDSCAPE;
    }

    public float getSelectedZoom() {
        String item = (String) zoomCombo.getSelectedItem();
        if (item == null || item.equalsIgnoreCase("Fit")) return 1.0f;
        try {
            return Float.parseFloat(item.replace("%", "").trim()) / 100.0f;
        } catch (NumberFormatException ignored) {
            return 1.0f;
        }
    }

    public boolean isShowSafeArea() {
        return safeAreaCheckBox.isSelected();
    }

    private void fireChange() {
        if (listener != null) {
            listener.onResolutionChanged(
                    getSelectedResolution(),
                    getSelectedOrientation(),
                    getSelectedZoom(),
                    isShowSafeArea()
            );
        }
    }
}
