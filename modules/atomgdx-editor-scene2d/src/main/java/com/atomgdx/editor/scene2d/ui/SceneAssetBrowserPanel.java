package com.atomgdx.editor.scene2d.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Workspace Asset Browser for HyperLap2D scenes.
 */
public class SceneAssetBrowserPanel extends JPanel {

    private static class DarkColors {
        public static final Color BG_PANEL = new Color(43, 45, 48);
        public static final Color BG_DARK = new Color(30, 31, 34);
        public static final Color TEXT_PRIMARY = new Color(223, 225, 229);
        public static final Color ACCENT = new Color(53, 116, 240);
        public static final Color BORDER = new Color(60, 63, 65);
    }

    private final DefaultListModel<File> assetListModel = new DefaultListModel<>();
    private final JList<File> assetJList = new JList<>(assetListModel);
    private final JTextField searchField = new JTextField();
    private Consumer<File> assetAddListener;

    public SceneAssetBrowserPanel() {
        setLayout(new BorderLayout(4, 4));
        setBackground(DarkColors.BG_PANEL);
        setBorder(new EmptyBorder(4, 4, 4, 4));

        // Header with Search
        JPanel top = new JPanel(new BorderLayout(4, 4));
        top.setOpaque(false);
        JLabel title = new JLabel("Asset Library");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(DarkColors.TEXT_PRIMARY);
        top.add(title, BorderLayout.NORTH);

        searchField.setBackground(DarkColors.BG_DARK);
        searchField.setForeground(DarkColors.TEXT_PRIMARY);
        searchField.putClientProperty("JTextField.placeholderText", "Search assets (.png, .jpg)...");
        searchField.addActionListener(e -> refreshAssets());
        top.add(searchField, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // List
        assetJList.setBackground(DarkColors.BG_DARK);
        assetJList.setForeground(DarkColors.TEXT_PRIMARY);
        assetJList.setBorder(new LineBorder(DarkColors.BORDER, 1));
        assetJList.setCellRenderer(new AssetCellRenderer());

        assetJList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File f = assetJList.getSelectedValue();
                    if (f != null && assetAddListener != null) {
                        assetAddListener.accept(f);
                    }
                }
            }
        });

        add(new JScrollPane(assetJList), BorderLayout.CENTER);

        // Bottom Add button
        JButton addBtn = new JButton("Add Selected to Scene");
        addBtn.setBackground(DarkColors.ACCENT);
        addBtn.setForeground(java.awt.Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            File f = assetJList.getSelectedValue();
            if (f != null && assetAddListener != null) {
                assetAddListener.accept(f);
            }
        });
        add(addBtn, BorderLayout.SOUTH);

        refreshAssets();
    }

    public void setAssetAddListener(Consumer<File> listener) {
        this.assetAddListener = listener;
    }

    public void refreshAssets() {
        assetListModel.clear();
        String query = searchField.getText().trim().toLowerCase();

        File assetsDir = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets");
        if (assetsDir.exists()) {
            scanDirectory(assetsDir, query);
        }
    }

    private void scanDirectory(File dir, String query) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                scanDirectory(f, query);
            } else {
                String name = f.getName().toLowerCase();
                if ((name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".p")) &&
                        (query.isEmpty() || name.contains(query))) {
                    assetListModel.addElement(f);
                }
            }
        }
    }

    private static class AssetCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                File f = (File) value;
                label.setText(f.getName());
                label.setToolTipText(f.getAbsolutePath());
            }
            if (isSelected) {
                label.setBackground(DarkColors.ACCENT);
                label.setForeground(java.awt.Color.WHITE);
            } else {
                label.setBackground(DarkColors.BG_DARK);
                label.setForeground(DarkColors.TEXT_PRIMARY);
            }
            return label;
        }
    }
}
