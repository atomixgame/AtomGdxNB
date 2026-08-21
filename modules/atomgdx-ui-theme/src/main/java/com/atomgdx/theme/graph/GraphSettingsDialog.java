package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Settings & Preferences Dialog for Visual Graph Editors.
 */
public class GraphSettingsDialog extends JDialog {

    public GraphSettingsDialog(Frame parent, AtomVisualGraphScene scene, Runnable onApply) {
        super(parent, "Visual Graph Preferences & Routing Settings", true);

        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(24, 26, 32));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel content = new JPanel(new GridLayout(0, 1, 10, 10));
        content.setOpaque(false);

        // 1. Connection Wire Routing Group
        JPanel routingPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        routingPanel.setOpaque(false);
        routingPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(60, 68, 85), 1), "Wire & Connector Routing",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 220, 255)));

        JLabel routeLbl = new JLabel("Routing Algorithm:");
        routeLbl.setForeground(new Color(220, 225, 235));
        routingPanel.add(routeLbl);

        JComboBox<String> routeBox = new JComboBox<>(new String[]{
                "Cubic Bezier Splines (Unreal / Blender style)",
                "Orthogonal Rectangular (Manhattan 90° turns)",
                "Direct Straight Lines"
        });
        if (scene.getRoutingMode() == AtomVisualGraphScene.RoutingMode.CUBIC_BEZIER_SPLINES) routeBox.setSelectedIndex(0);
        else if (scene.getRoutingMode() == AtomVisualGraphScene.RoutingMode.ORTHOGONAL_RECTANGULAR) routeBox.setSelectedIndex(1);
        else routeBox.setSelectedIndex(2);
        routingPanel.add(routeBox);

        JLabel tensionLbl = new JLabel("Spline Curve Tangent:");
        tensionLbl.setForeground(new Color(220, 225, 235));
        routingPanel.add(tensionLbl);

        JSlider tensionSlider = new JSlider(20, 120, 50);
        tensionSlider.setOpaque(false);
        routingPanel.add(tensionSlider);

        content.add(routingPanel);

        // 2. Grid & Snapping Group
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(60, 68, 85), 1), "Canvas Grid & Alignment",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 220, 255)));

        JLabel snapLbl = new JLabel("Grid Snapping:");
        snapLbl.setForeground(new Color(220, 225, 235));
        gridPanel.add(snapLbl);

        JComboBox<String> snapBox = new JComboBox<>(new String[]{"16px (Standard)", "8px (Fine)", "32px (Coarse)", "Disabled"});
        snapBox.setSelectedIndex(scene.isGridSnapEnabled() ? 0 : 3);
        gridPanel.add(snapBox);

        content.add(gridPanel);

        add(content, BorderLayout.CENTER);

        // Buttons Bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnBar.add(cancelBtn);

        JButton applyBtn = new JButton("Apply Settings");
        applyBtn.addActionListener(e -> {
            int sel = routeBox.getSelectedIndex();
            if (sel == 0) scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.CUBIC_BEZIER_SPLINES);
            else if (sel == 1) scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.ORTHOGONAL_RECTANGULAR);
            else scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.DIRECT_LINES);

            int snapSel = snapBox.getSelectedIndex();
            scene.setGridSnapEnabled(snapSel != 3);

            if (onApply != null) onApply.run();
            dispose();
        });
        btnBar.add(applyBtn);

        add(btnBar, BorderLayout.SOUTH);

        setSize(480, 320);
        setLocationRelativeTo(parent);
    }
}
