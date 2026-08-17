package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.editor.scene2d.data.vo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Scene Hierarchy Tree Panel showing layers and placed items with selection and controls.
 */
public class SceneHierarchyTreePanel extends JPanel {

    private static class DarkColors {
        public static final Color BG_PANEL = new Color(43, 45, 48);
        public static final Color BG_DARK = new Color(30, 31, 34);
        public static final Color TEXT_PRIMARY = new Color(223, 225, 229);
        public static final Color ACCENT = new Color(53, 116, 240);
        public static final Color BORDER = new Color(60, 63, 65);
    }

    private final SceneVO scene;
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Scene Root");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JTree tree = new JTree(treeModel);

    private Consumer<MainItemVO> selectionListener;
    private Runnable changeListener;

    public SceneHierarchyTreePanel(SceneVO scene) {
        this.scene = scene != null ? scene : new SceneVO("MainScene");

        setLayout(new BorderLayout(4, 4));
        setBackground(DarkColors.BG_PANEL);
        setBorder(new EmptyBorder(4, 4, 4, 4));

        // Header
        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(2, 2, 4, 2));
        JLabel title = new JLabel("Scene Hierarchy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(DarkColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);

        // Layer & Item Action buttons
        JPanel actions = new JPanel(new GridLayout(1, 3, 4, 0));
        actions.setOpaque(false);
        JButton addLayerBtn = new JButton("+ Layer");
        JButton addImgBtn = new JButton("+ Sprite");
        JButton deleteBtn = new JButton("- Delete");

        for (JButton b : new JButton[]{addLayerBtn, addImgBtn, deleteBtn}) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            b.setBackground(DarkColors.BG_PANEL);
            b.setForeground(DarkColors.TEXT_PRIMARY);
            b.setFocusPainted(false);
            actions.add(b);
        }

        addLayerBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Layer Name:", "Layer " + (scene.composite.layers.size() + 1));
            if (name != null && !name.trim().isEmpty()) {
                scene.composite.layers.add(new LayerItemVO(name.trim()));
                rebuildTree();
                if (changeListener != null) changeListener.run();
            }
        });

        addImgBtn.addActionListener(e -> {
            SimpleImageVO img = new SimpleImageVO("sprite_" + (scene.composite.sImages.size() + 1), 600, 300);
            scene.composite.sImages.add(img);
            rebuildTree();
            selectItem(img);
            if (changeListener != null) changeListener.run();
        });

        deleteBtn.addActionListener(e -> {
            MainItemVO selected = getSelectedItem();
            if (selected != null) {
                scene.composite.sImages.remove(selected);
                scene.composite.sLabels.remove(selected);
                scene.composite.sComposites.remove(selected);
                rebuildTree();
                if (selectionListener != null) selectionListener.accept(null);
                if (changeListener != null) changeListener.run();
            }
        });

        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // JTree
        tree.setBackground(DarkColors.BG_DARK);
        tree.setForeground(DarkColors.TEXT_PRIMARY);
        tree.setBorder(new LineBorder(DarkColors.BORDER, 1));
        tree.setCellRenderer(new SceneTreeRenderer());

        tree.addTreeSelectionListener(e -> {
            MainItemVO item = getSelectedItem();
            if (selectionListener != null) {
                selectionListener.accept(item);
            }
        });

        add(new JScrollPane(tree), BorderLayout.CENTER);
        rebuildTree();
    }

    public void setSelectionListener(Consumer<MainItemVO> listener) {
        this.selectionListener = listener;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    public MainItemVO getSelectedItem() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.getUserObject() instanceof MainItemVO) {
            return (MainItemVO) node.getUserObject();
        }
        return null;
    }

    public void selectItem(MainItemVO item) {
        if (item == null) return;
        // Search node
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode layerNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            for (int j = 0; j < layerNode.getChildCount(); j++) {
                DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) layerNode.getChildAt(j);
                if (itemNode.getUserObject() == item) {
                    tree.setSelectionPath(new TreePath(itemNode.getPath()));
                    return;
                }
            }
        }
    }

    public void rebuildTree() {
        rootNode.removeAllChildren();
        rootNode.setUserObject(scene.sceneName);

        for (LayerItemVO layer : scene.composite.layers) {
            DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer);

            // Add images in layer
            for (SimpleImageVO img : scene.composite.sImages) {
                if (layer.layerName.equals(img.layerName)) {
                    layerNode.add(new DefaultMutableTreeNode(img));
                }
            }

            // Add labels in layer
            for (LabelVO lbl : scene.composite.sLabels) {
                if (layer.layerName.equals(lbl.layerName)) {
                    layerNode.add(new DefaultMutableTreeNode(lbl));
                }
            }

            // Add composites in layer
            for (CompositeItemVO comp : scene.composite.sComposites) {
                if (layer.layerName.equals(comp.layerName)) {
                    layerNode.add(new DefaultMutableTreeNode(comp));
                }
            }

            rootNode.add(layerNode);
        }

        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private static class SceneTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();

            if (obj instanceof LayerItemVO) {
                LayerItemVO layer = (LayerItemVO) obj;
                label.setText("Layer: " + layer.layerName);
            } else if (obj instanceof SimpleImageVO) {
                SimpleImageVO img = (SimpleImageVO) obj;
                label.setText("Sprite: " + (img.itemName != null && !img.itemName.isEmpty() ? img.itemName : img.imageName));
            } else if (obj instanceof LabelVO) {
                LabelVO lbl = (LabelVO) obj;
                label.setText("Text: " + lbl.text);
            } else if (obj instanceof CompositeItemVO) {
                CompositeItemVO comp = (CompositeItemVO) obj;
                label.setText("Composite: " + comp.itemName);
            }

            if (sel) {
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
