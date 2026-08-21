package com.atomgdx.editor.scene2d.tilemap.ui;

import com.atomgdx.editor.scene2d.tilemap.data.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Multi-layer manager panel with layer reordering, visibility, lock, opacity, and tint.
 */
public class TilemapLayersPanel extends JPanel {

    private final TilemapDocument document;
    private final DefaultListModel<TilemapLayer> listModel = new DefaultListModel<>();
    private final JList<TilemapLayer> layerList = new JList<>(listModel);
    private final JSlider opacitySlider = new JSlider(0, 100, 100);
    private final JCheckBox visibleCheck = new JCheckBox("Visible", true);
    private final JCheckBox lockedCheck = new JCheckBox("Locked", false);
    private final JButton tintBtn = new JButton();

    private Runnable layerChangeListener;

    public TilemapLayersPanel(TilemapDocument document) {
        this.document = document;
        setLayout(new BorderLayout(4, 4));
        setBackground(new Color(24, 26, 30));
        setBorder(new EmptyBorder(6, 6, 6, 6));

        // Header actions
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        actionToolbar.setOpaque(false);

        JButton addTileLayerBtn = new JButton("+ Tile");
        addTileLayerBtn.setToolTipText("Add new Tile Grid Layer");
        addTileLayerBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Layer Name:", "New Tile Layer", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                this.document.addLayer(new TileLayer(name.trim(), this.document.getWidth(), this.document.getHeight()));
                refreshList();
                notifyChange();
            }
        });
        actionToolbar.add(addTileLayerBtn);

        JButton addObjLayerBtn = new JButton("+ Obj");
        addObjLayerBtn.setToolTipText("Add new Vector Objects Layer");
        addObjLayerBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Layer Name:", "New Object Layer", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                this.document.addLayer(new ObjectGroupLayer(name.trim()));
                refreshList();
                notifyChange();
            }
        });
        actionToolbar.add(addObjLayerBtn);

        JButton upBtn = new JButton("▲");
        upBtn.addActionListener(e -> moveSelectedLayer(-1));
        actionToolbar.add(upBtn);

        JButton downBtn = new JButton("▼");
        downBtn.addActionListener(e -> moveSelectedLayer(1));
        actionToolbar.add(downBtn);

        JButton deleteBtn = new JButton("✖");
        deleteBtn.addActionListener(e -> {
            int idx = layerList.getSelectedIndex();
            if (idx >= 0 && this.document.getLayers().size() > 1) {
                this.document.removeLayer(idx);
                refreshList();
                notifyChange();
            }
        });
        actionToolbar.add(deleteBtn);

        add(actionToolbar, BorderLayout.NORTH);

        // Center Layer List
        refreshList();
        layerList.setBackground(new Color(16, 17, 20));
        layerList.setForeground(Color.WHITE);
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TilemapLayer) {
                    TilemapLayer tl = (TilemapLayer) value;
                    String prefix = tl instanceof TileLayer ? "🧱 " : (tl instanceof ObjectGroupLayer ? "🎯 " : "🖼 ");
                    String visIcon = tl.isVisible() ? "👁 " : "🚫 ";
                    String lockIcon = tl.isLocked() ? "🔒 " : "";
                    l.setText(visIcon + lockIcon + prefix + tl.getName() + " (" + tl.getType().name() + ")");
                }
                return l;
            }
        });

        layerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = layerList.getSelectedIndex();
                if (idx >= 0) {
                    this.document.setActiveLayerIndex(idx);
                    loadSelectedLayerProps();
                    notifyChange();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(layerList);
        scroll.setBorder(createTitledBorder("Tilemap Layers"));
        add(scroll, BorderLayout.CENTER);

        // Bottom Layer Properties
        JPanel propPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        propPanel.setOpaque(false);

        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        toggleRow.setOpaque(false);
        visibleCheck.setOpaque(false);
        visibleCheck.setForeground(Color.WHITE);
        visibleCheck.addActionListener(e -> {
            TilemapLayer sel = getSelectedLayer();
            if (sel != null) {
                sel.setVisible(visibleCheck.isSelected());
                layerList.repaint();
                notifyChange();
            }
        });
        toggleRow.add(visibleCheck);

        lockedCheck.setOpaque(false);
        lockedCheck.setForeground(Color.WHITE);
        lockedCheck.addActionListener(e -> {
            TilemapLayer sel = getSelectedLayer();
            if (sel != null) {
                sel.setLocked(lockedCheck.isSelected());
                layerList.repaint();
                notifyChange();
            }
        });
        toggleRow.add(lockedCheck);
        propPanel.add(toggleRow);

        JPanel opacityRow = new JPanel(new BorderLayout(4, 0));
        opacityRow.setOpaque(false);
        opacityRow.add(new JLabel("Opacity:"), BorderLayout.WEST);
        opacitySlider.setOpaque(false);
        opacitySlider.addChangeListener(e -> {
            TilemapLayer sel = getSelectedLayer();
            if (sel != null) {
                sel.setOpacity(opacitySlider.getValue() / 100f);
                notifyChange();
            }
        });
        opacityRow.add(opacitySlider, BorderLayout.CENTER);
        propPanel.add(opacityRow);

        JPanel tintRow = new JPanel(new BorderLayout(4, 0));
        tintRow.setOpaque(false);
        tintRow.add(new JLabel("Tint Color:"), BorderLayout.WEST);
        tintBtn.setBackground(Color.WHITE);
        tintBtn.setPreferredSize(new Dimension(50, 18));
        tintBtn.addActionListener(e -> {
            TilemapLayer sel = getSelectedLayer();
            if (sel != null) {
                Color c = JColorChooser.showDialog(this, "Layer Tint Color", sel.getTintColor());
                if (c != null) {
                    sel.setTintColor(c);
                    tintBtn.setBackground(c);
                    notifyChange();
                }
            }
        });
        tintRow.add(tintBtn, BorderLayout.CENTER);
        propPanel.add(tintRow);

        add(propPanel, BorderLayout.SOUTH);

        if (!listModel.isEmpty()) {
            layerList.setSelectedIndex(0);
        }
    }

    public void setLayerChangeListener(Runnable r) {
        this.layerChangeListener = r;
    }

    private void notifyChange() {
        if (layerChangeListener != null) layerChangeListener.run();
    }

    private void refreshList() {
        listModel.clear();
        for (TilemapLayer l : document.getLayers()) {
            listModel.addElement(l);
        }
        if (document.getActiveLayerIndex() < listModel.size()) {
            layerList.setSelectedIndex(document.getActiveLayerIndex());
        }
    }

    private TilemapLayer getSelectedLayer() {
        int idx = layerList.getSelectedIndex();
        if (idx >= 0 && idx < document.getLayers().size()) {
            return document.getLayers().get(idx);
        }
        return null;
    }

    private void loadSelectedLayerProps() {
        TilemapLayer l = getSelectedLayer();
        if (l == null) return;
        visibleCheck.setSelected(l.isVisible());
        lockedCheck.setSelected(l.isLocked());
        opacitySlider.setValue(Math.round(l.getOpacity() * 100f));
        tintBtn.setBackground(l.getTintColor());
    }

    private void moveSelectedLayer(int direction) {
        int idx = layerList.getSelectedIndex();
        int target = idx + direction;
        if (idx >= 0 && target >= 0 && target < document.getLayers().size()) {
            TilemapLayer cur = document.getLayers().remove(idx);
            document.getLayers().add(target, cur);
            document.setActiveLayerIndex(target);
            refreshList();
            notifyChange();
        }
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 52, 58), 1),
                title
        );
        border.setTitleColor(new Color(220, 224, 230));
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        return border;
    }
}
