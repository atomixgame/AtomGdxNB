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
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Advanced HyperLap2D Scene Hierarchy & Layer Tree Panel with rich context menus,
 * inline node editing, item duplication, layer moving, and visibility/lock toggles.
 */
public class SceneHierarchyTreePanel extends JPanel {

    public static class HierarchyColors {
        public static final Color BG_PANEL = new Color(34, 35, 38);
        public static final Color BG_TREE = new Color(26, 26, 28);
        public static final Color TEXT_PRIMARY = new Color(225, 228, 232);
        public static final Color TEXT_SECONDARY = new Color(150, 155, 162);
        public static final Color TEXT_MUTED = new Color(105, 110, 118);
        public static final Color ACCENT_BLUE = new Color(44, 93, 212);
        public static final Color BORDER = new Color(55, 57, 62);
    }

    private final SceneVO scene;
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Scene Root");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JTree tree = new JTree(treeModel);

    private Consumer<MainItemVO> selectionListener;
    private Consumer<MainItemVO> doubleClickListener;
    private Runnable changeListener;

    public SceneHierarchyTreePanel(SceneVO scene) {
        this.scene = scene != null ? scene : new SceneVO("MainScene");

        setLayout(new BorderLayout(0, 0));
        setBackground(HierarchyColors.BG_PANEL);

        // Header Toolbar with Quick Action Buttons
        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setBackground(HierarchyColors.BG_PANEL);
        header.setBorder(new EmptyBorder(4, 6, 4, 6));

        JLabel title = new JLabel("Scene Hierarchy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(HierarchyColors.TEXT_PRIMARY);
        title.setIcon(Scene2DEditorPanel.getIcon("star.png"));
        header.add(title, BorderLayout.WEST);

        // Quick Add dropdown button
        JButton addBtn = new JButton("+ Create", Scene2DEditorPanel.getIcon("add.png"));
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        addBtn.setBackground(new Color(42, 44, 48));
        addBtn.setForeground(HierarchyColors.TEXT_PRIMARY);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showCreateMenu(addBtn));

        JButton deleteBtn = new JButton(Scene2DEditorPanel.getIcon("delete.png"));
        deleteBtn.setToolTipText("Delete selected item (Del)");
        deleteBtn.setPreferredSize(new Dimension(26, 24));
        deleteBtn.setBackground(new Color(42, 44, 48));
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteSelectedItem());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);
        actions.add(addBtn);
        actions.add(deleteBtn);
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // JTree
        tree.setBackground(HierarchyColors.BG_TREE);
        tree.setForeground(HierarchyColors.TEXT_PRIMARY);
        tree.setBorder(new EmptyBorder(4, 4, 4, 4));
        tree.setCellRenderer(new AdvancedSceneTreeRenderer());
        tree.setRowHeight(22);

        // Selection Listener
        tree.addTreeSelectionListener(e -> {
            MainItemVO item = getSelectedItem();
            if (selectionListener != null) {
                selectionListener.accept(item);
            }
        });

        // Mouse & Context Menu Listener
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    showContextMenu(e.getX(), e.getY());
                } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    MainItemVO item = getSelectedItem();
                    if (item != null && doubleClickListener != null) {
                        doubleClickListener.accept(item);
                    }
                }
            }
        });

        // Keyboard Shortcuts
        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedItem();
                } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                    renameSelectedItem();
                } else if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {
                    duplicateSelectedItem();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(new LineBorder(HierarchyColors.BORDER, 1));
        scrollPane.getViewport().setBackground(HierarchyColors.BG_TREE);
        add(scrollPane, BorderLayout.CENTER);

        rebuildTree();
    }

    public void setSelectionListener(Consumer<MainItemVO> listener) {
        this.selectionListener = listener;
    }

    public void setDoubleClickListener(Consumer<MainItemVO> listener) {
        this.doubleClickListener = listener;
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

    public LayerItemVO getSelectedLayer() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.getUserObject() instanceof LayerItemVO) {
            return (LayerItemVO) node.getUserObject();
        } else if (node.getUserObject() instanceof MainItemVO) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null && parent.getUserObject() instanceof LayerItemVO) {
                return (LayerItemVO) parent.getUserObject();
            }
        }
        return !scene.composite.layers.isEmpty() ? scene.composite.layers.get(0) : null;
    }

    public void selectItem(MainItemVO item) {
        if (item == null) return;
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

            for (SimpleImageVO img : scene.composite.sImages) {
                if (layer.layerName.equals(img.layerName)) layerNode.add(new DefaultMutableTreeNode(img));
            }
            for (ParticleEffectVO p : scene.composite.sParticleEffects) {
                if (layer.layerName.equals(p.layerName)) layerNode.add(new DefaultMutableTreeNode(p));
            }
            for (LightVO lt : scene.composite.sLights) {
                if (layer.layerName.equals(lt.layerName)) layerNode.add(new DefaultMutableTreeNode(lt));
            }
            for (LabelVO lbl : scene.composite.sLabels) {
                if (layer.layerName.equals(lbl.layerName)) layerNode.add(new DefaultMutableTreeNode(lbl));
            }
            for (NinePatchVO np : scene.composite.sNinePatches) {
                if (layer.layerName.equals(np.layerName)) layerNode.add(new DefaultMutableTreeNode(np));
            }
            for (CompositeItemVO comp : scene.composite.sComposites) {
                if (layer.layerName.equals(comp.layerName)) layerNode.add(new DefaultMutableTreeNode(comp));
            }

            rootNode.add(layerNode);
        }

        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void showCreateMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem addSprite = new JMenuItem("2D Sprite Image", Scene2DEditorPanel.getIcon("picture.png"));
        JMenuItem addParticle = new JMenuItem("Particle Emitter", Scene2DEditorPanel.getIcon("fire.png"));
        JMenuItem addLight = new JMenuItem("Dynamic Light 2D", Scene2DEditorPanel.getIcon("lightning.png"));
        JMenuItem addText = new JMenuItem("Text Label", Scene2DEditorPanel.getIcon("wand.png"));
        JMenuItem addNinePatch = new JMenuItem("9-Patch Image", Scene2DEditorPanel.getIcon("picture.png"));
        JMenuItem addLayer = new JMenuItem("New Layer", Scene2DEditorPanel.getIcon("folder.png"));

        LayerItemVO curLayer = getSelectedLayer();
        String layerName = curLayer != null ? curLayer.layerName : "Default Layer";

        addSprite.addActionListener(e -> {
            SimpleImageVO img = new SimpleImageVO("sprite_" + (scene.composite.sImages.size() + 1), 640, 360);
            img.layerName = layerName;
            scene.composite.sImages.add(img);
            rebuildTree();
            selectItem(img);
            if (changeListener != null) changeListener.run();
        });

        addParticle.addActionListener(e -> {
            ParticleEffectVO p = new ParticleEffectVO("particle_" + (scene.composite.sParticleEffects.size() + 1), 640, 360);
            p.layerName = layerName;
            scene.composite.sParticleEffects.add(p);
            rebuildTree();
            selectItem(p);
            if (changeListener != null) changeListener.run();
        });

        addLight.addActionListener(e -> {
            LightVO lt = new LightVO("light_" + (scene.composite.sLights.size() + 1), LightVO.LightType.POINT, 640, 360);
            lt.layerName = layerName;
            scene.composite.sLights.add(lt);
            rebuildTree();
            selectItem(lt);
            if (changeListener != null) changeListener.run();
        });

        addText.addActionListener(e -> {
            LabelVO lbl = new LabelVO("New Text", 640, 360);
            lbl.layerName = layerName;
            scene.composite.sLabels.add(lbl);
            rebuildTree();
            selectItem(lbl);
            if (changeListener != null) changeListener.run();
        });

        addNinePatch.addActionListener(e -> {
            NinePatchVO np = new NinePatchVO("ninepatch_" + (scene.composite.sNinePatches.size() + 1), 100, 100);
            np.layerName = layerName;
            np.x = 640; np.y = 360;
            scene.composite.sNinePatches.add(np);
            rebuildTree();
            selectItem(np);
            if (changeListener != null) changeListener.run();
        });

        addLayer.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Layer Name:", "Layer " + (scene.composite.layers.size() + 1));
            if (name != null && !name.trim().isEmpty()) {
                scene.composite.layers.add(new LayerItemVO(name.trim()));
                rebuildTree();
                if (changeListener != null) changeListener.run();
            }
        });

        menu.add(addSprite);
        menu.add(addParticle);
        menu.add(addLight);
        menu.add(addText);
        menu.add(addNinePatch);
        menu.addSeparator();
        menu.add(addLayer);

        menu.show(invoker, 0, invoker.getHeight());
    }

    private void showContextMenu(int x, int y) {
        MainItemVO selectedItem = getSelectedItem();
        LayerItemVO selectedLayer = getSelectedLayer();

        JPopupMenu menu = new JPopupMenu();

        if (selectedItem != null) {
            JMenuItem renameItem = new JMenuItem("Rename (F2)", Scene2DEditorPanel.getIcon("wand.png"));
            JMenuItem duplicateItem = new JMenuItem("Duplicate (Ctrl+D)", Scene2DEditorPanel.getIcon("add.png"));
            JMenuItem toggleVis = new JMenuItem(selectedItem.isVisible ? "Hide Item" : "Show Item", Scene2DEditorPanel.getIcon("picture.png"));
            JMenuItem toggleLock = new JMenuItem(selectedItem.isLocked ? "Unlock Item" : "Lock Item", Scene2DEditorPanel.getIcon("cog.png"));
            JMenuItem deleteItem = new JMenuItem("Delete (Del)", Scene2DEditorPanel.getIcon("delete.png"));

            renameItem.addActionListener(e -> renameSelectedItem());
            duplicateItem.addActionListener(e -> duplicateSelectedItem());
            toggleVis.addActionListener(e -> {
                selectedItem.isVisible = !selectedItem.isVisible;
                rebuildTree();
                selectItem(selectedItem);
                if (changeListener != null) changeListener.run();
            });
            toggleLock.addActionListener(e -> {
                selectedItem.isLocked = !selectedItem.isLocked;
                rebuildTree();
                selectItem(selectedItem);
                if (changeListener != null) changeListener.run();
            });
            deleteItem.addActionListener(e -> deleteSelectedItem());

            menu.add(renameItem);
            menu.add(duplicateItem);
            menu.addSeparator();
            menu.add(toggleVis);
            menu.add(toggleLock);
            menu.addSeparator();
            menu.add(deleteItem);
        } else if (selectedLayer != null) {
            JMenuItem addSprite = new JMenuItem("Add Sprite here", Scene2DEditorPanel.getIcon("picture.png"));
            JMenuItem renameLayer = new JMenuItem("Rename Layer", Scene2DEditorPanel.getIcon("wand.png"));
            JMenuItem toggleLayerVis = new JMenuItem(selectedLayer.isVisible ? "Hide Layer" : "Show Layer", Scene2DEditorPanel.getIcon("folder.png"));
            JMenuItem deleteLayer = new JMenuItem("Delete Layer", Scene2DEditorPanel.getIcon("delete.png"));

            addSprite.addActionListener(e -> {
                SimpleImageVO img = new SimpleImageVO("sprite_" + (scene.composite.sImages.size() + 1), 640, 360);
                img.layerName = selectedLayer.layerName;
                scene.composite.sImages.add(img);
                rebuildTree();
                selectItem(img);
                if (changeListener != null) changeListener.run();
            });

            renameLayer.addActionListener(e -> {
                String newName = JOptionPane.showInputDialog(this, "Rename Layer:", selectedLayer.layerName);
                if (newName != null && !newName.trim().isEmpty()) {
                    String old = selectedLayer.layerName;
                    selectedLayer.layerName = newName.trim();
                    for (SimpleImageVO img : scene.composite.sImages) {
                        if (old.equals(img.layerName)) img.layerName = selectedLayer.layerName;
                    }
                    rebuildTree();
                    if (changeListener != null) changeListener.run();
                }
            });

            toggleLayerVis.addActionListener(e -> {
                selectedLayer.isVisible = !selectedLayer.isVisible;
                for (SimpleImageVO img : scene.composite.sImages) {
                    if (selectedLayer.layerName.equals(img.layerName)) img.isVisible = selectedLayer.isVisible;
                }
                rebuildTree();
                if (changeListener != null) changeListener.run();
            });

            deleteLayer.addActionListener(e -> {
                if (scene.composite.layers.size() > 1) {
                    scene.composite.layers.remove(selectedLayer);
                    rebuildTree();
                    if (changeListener != null) changeListener.run();
                }
            });

            menu.add(addSprite);
            menu.add(renameLayer);
            menu.add(toggleLayerVis);
            menu.addSeparator();
            menu.add(deleteLayer);
        } else {
            JMenuItem addLayer = new JMenuItem("New Layer", Scene2DEditorPanel.getIcon("folder.png"));
            addLayer.addActionListener(e -> {
                String name = JOptionPane.showInputDialog(this, "Enter Layer Name:", "New Layer");
                if (name != null && !name.trim().isEmpty()) {
                    scene.composite.layers.add(new LayerItemVO(name.trim()));
                    rebuildTree();
                    if (changeListener != null) changeListener.run();
                }
            });
            menu.add(addLayer);
        }

        menu.show(tree, x, y);
    }

    private void renameSelectedItem() {
        MainItemVO item = getSelectedItem();
        if (item == null) return;
        String newName = JOptionPane.showInputDialog(this, "Rename Item:", item.itemName);
        if (newName != null && !newName.trim().isEmpty()) {
            item.itemName = newName.trim();
            rebuildTree();
            selectItem(item);
            if (changeListener != null) changeListener.run();
        }
    }

    private void duplicateSelectedItem() {
        MainItemVO item = getSelectedItem();
        if (item instanceof SimpleImageVO) {
            SimpleImageVO src = (SimpleImageVO) item;
            SimpleImageVO dup = new SimpleImageVO(src.imageName, src.x + 25f, src.y + 25f);
            dup.itemName = src.itemName + "_Copy";
            dup.layerName = src.layerName;
            dup.scaleX = src.scaleX;
            dup.scaleY = src.scaleY;
            dup.rotation = src.rotation;
            dup.originX = src.originX;
            dup.originY = src.originY;
            if (src.physics != null) {
                dup.physics = new PhysicsBodyDataVO();
                dup.physics.bodyType = src.physics.bodyType;
                dup.physics.density = src.physics.density;
                dup.physics.friction = src.physics.friction;
                dup.physics.restitution = src.physics.restitution;
            }
            scene.composite.sImages.add(dup);
            rebuildTree();
            selectItem(dup);
            if (changeListener != null) changeListener.run();
        }
    }

    private void deleteSelectedItem() {
        MainItemVO selected = getSelectedItem();
        if (selected != null) {
            scene.composite.sImages.remove(selected);
            scene.composite.sLabels.remove(selected);
            scene.composite.sLights.remove(selected);
            scene.composite.sParticleEffects.remove(selected);
            scene.composite.sNinePatches.remove(selected);
            scene.composite.sComposites.remove(selected);
            rebuildTree();
            if (selectionListener != null) selectionListener.accept(null);
            if (changeListener != null) changeListener.run();
        }
    }

    /**
     * Advanced Tree Cell Renderer displaying custom Fatcow icons, layer badges, and status labels.
     */
    private static class AdvancedSceneTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();

            if (obj instanceof LayerItemVO) {
                LayerItemVO layer = (LayerItemVO) obj;
                label.setText(layer.layerName + (!layer.isVisible ? " [Hidden]" : ""));
                label.setIcon(Scene2DEditorPanel.getIcon("folder.png"));
            } else if (obj instanceof SimpleImageVO) {
                SimpleImageVO img = (SimpleImageVO) obj;
                label.setText(img.itemName != null && !img.itemName.isEmpty() ? img.itemName : img.imageName);
                label.setIcon(Scene2DEditorPanel.getIcon("picture.png"));
            } else if (obj instanceof ParticleEffectVO) {
                ParticleEffectVO p = (ParticleEffectVO) obj;
                label.setText(p.itemName);
                label.setIcon(Scene2DEditorPanel.getIcon("fire.png"));
            } else if (obj instanceof LightVO) {
                LightVO lt = (LightVO) obj;
                label.setText(lt.itemName + " (" + lt.type + ")");
                label.setIcon(Scene2DEditorPanel.getIcon("lightning.png"));
            } else if (obj instanceof LabelVO) {
                LabelVO lbl = (LabelVO) obj;
                label.setText("\"" + lbl.text + "\"");
                label.setIcon(Scene2DEditorPanel.getIcon("wand.png"));
            } else if (obj instanceof NinePatchVO) {
                NinePatchVO np = (NinePatchVO) obj;
                label.setText("9-Patch: " + np.itemName);
                label.setIcon(Scene2DEditorPanel.getIcon("picture.png"));
            } else if (obj instanceof CompositeItemVO) {
                CompositeItemVO comp = (CompositeItemVO) obj;
                label.setText("Composite: " + comp.itemName);
                label.setIcon(Scene2DEditorPanel.getIcon("star.png"));
            } else {
                label.setIcon(Scene2DEditorPanel.getIcon("star.png"));
            }

            if (sel) {
                label.setBackground(HierarchyColors.ACCENT_BLUE);
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
            } else {
                label.setBackground(HierarchyColors.BG_TREE);
                label.setForeground(HierarchyColors.TEXT_PRIMARY);
                label.setOpaque(false);
            }
            return label;
        }
    }
}
