package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Prefab3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Interactive 3D SceneGraph Tree Panel displaying hierarchical 3D entities.
 * Supports adding/removing nodes, lights, cameras, and prefabs.
 */
public class SceneGraphTreePanel extends JPanel {

    private final Scene3DVO scene;
    private final JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootTreeNode;
    private Consumer<Node3DVO> selectionListener;

    public SceneGraphTreePanel(Scene3DVO scene) {
        this.scene = scene != null ? scene : new Scene3DVO("MainScene3D");
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        // Header with "+ Create" button
        JPanel headerPanel = new JPanel(new BorderLayout(4, 0));
        headerPanel.setBackground(DarkThemeUtils.BG_HEADER);
        headerPanel.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel titleLbl = new JLabel("SceneGraph 3D");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        JButton createBtn = new JButton("+ Create");
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        createBtn.setBackground(DarkThemeUtils.BG_HEADER);
        createBtn.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        createBtn.setFocusPainted(false);
        createBtn.addActionListener(e -> showCreateMenu(createBtn));

        headerPanel.add(titleLbl, BorderLayout.WEST);
        headerPanel.add(createBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // SceneGraph Tree
        rootTreeNode = new DefaultMutableTreeNode(this.scene.rootNode);
        buildTreeNodes(rootTreeNode, this.scene.rootNode);
        treeModel = new DefaultTreeModel(rootTreeNode);

        tree = new JTree(treeModel);
        tree.setBackground(DarkThemeUtils.BG_DARK);
        tree.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        tree.setCellRenderer(new SceneGraphCellRenderer());
        tree.setRowHeight(22);

        // Expand all nodes by default
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        tree.addTreeSelectionListener(e -> {
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object uo = node.getUserObject();
                if (uo instanceof Node3DVO && selectionListener != null) {
                    selectionListener.accept((Node3DVO) uo);
                }
            }
        });

        setupContextMenu();

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setSelectionListener(Consumer<Node3DVO> listener) {
        this.selectionListener = listener;
    }

    private void buildTreeNodes(DefaultMutableTreeNode treeParent, Node3DVO nodeVo) {
        for (Node3DVO child : nodeVo.children) {
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(child);
            treeParent.add(childTreeNode);
            buildTreeNodes(childTreeNode, child);
        }
    }

    public void rebuildTree() {
        rootTreeNode.removeAllChildren();
        buildTreeNodes(rootTreeNode, scene.rootNode);
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public void addNodeToSelection(Node3DVO newNode) {
        Node3DVO targetParent = scene.rootNode;
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (selected.getUserObject() instanceof Node3DVO) {
                targetParent = (Node3DVO) selected.getUserObject();
            }
        }
        targetParent.addChild(newNode);
        rebuildTree();
        if (selectionListener != null) {
            selectionListener.accept(newNode);
        }
    }

    private void showCreateMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        JMenu meshMenu = new JMenu("3D Mesh");
        meshMenu.add(createMenuItem("Cube / Box", "box.png", () -> addNodeToSelection(new Node3DVO("Cube_Mesh", Node3DVO.MeshShape.BOX, 0, 1, 0))));
        meshMenu.add(createMenuItem("Sphere", "world.png", () -> addNodeToSelection(new Node3DVO("Sphere_Mesh", Node3DVO.MeshShape.SPHERE, 0, 1, 0))));
        meshMenu.add(createMenuItem("Cylinder", "cog.png", () -> addNodeToSelection(new Node3DVO("Cylinder_Mesh", Node3DVO.MeshShape.CYLINDER, 0, 1, 0))));
        meshMenu.add(createMenuItem("Cone", "bullet_red.png", () -> addNodeToSelection(new Node3DVO("Cone_Mesh", Node3DVO.MeshShape.CONE, 0, 1, 0))));
        meshMenu.add(createMenuItem("Plane / Grid Floor", "layout.png", () -> addNodeToSelection(new Node3DVO("Ground_Plane", Node3DVO.MeshShape.PLANE, 0, 0, 0))));

        JMenu lightMenu = new JMenu("Lighting");
        lightMenu.add(createMenuItem("Point Light", "lightning.png", () -> addNodeToSelection(new Node3DVO("PointLight_3D", Node3DVO.NodeType.LIGHT_POINT))));
        lightMenu.add(createMenuItem("Directional Light", "weather_sun.png", () -> addNodeToSelection(new Node3DVO("DirectionalLight_Sun", Node3DVO.NodeType.LIGHT_DIRECTIONAL))));

        JMenu prefabMenu = new JMenu("Prefab");
        prefabMenu.add(createMenuItem("Spacecraft Fighter", "car.png", () -> addNodeToSelection(Prefab3DVO.createSpacecraftFighter().rootNode)));
        prefabMenu.add(createMenuItem("Asteroid Rock", "world.png", () -> addNodeToSelection(Prefab3DVO.createAsteroidRock().rootNode)));
        prefabMenu.add(createMenuItem("SciFi Defense Turret", "shield.png", () -> addNodeToSelection(Prefab3DVO.createSciFiTurret().rootNode)));
        prefabMenu.add(createMenuItem("Energy Shield Bubble", "lightning.png", () -> addNodeToSelection(Prefab3DVO.createEnergyShield().rootNode)));

        menu.add(meshMenu);
        menu.add(lightMenu);
        menu.add(prefabMenu);
        menu.add(createMenuItem("Perspective Camera", "camera.png", () -> addNodeToSelection(new Node3DVO("Camera_Perspective", Node3DVO.NodeType.CAMERA))));
        menu.add(createMenuItem("Empty GameObject", "folder.png", () -> addNodeToSelection(new Node3DVO("Empty_GameObject", Node3DVO.NodeType.EMPTY))));

        menu.show(invoker, 0, invoker.getHeight());
    }

    private JMenuItem createMenuItem(String text, String iconName, Runnable action) {
        JMenuItem item = new JMenuItem(text, DarkThemeUtils.getFatcowIcon(iconName));
        item.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.addActionListener(e -> action.run());
        return item;
    }

    private void setupContextMenu() {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        tree.setSelectionPath(path);
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (treeNode.getUserObject() instanceof Node3DVO) {
                            Node3DVO node = (Node3DVO) treeNode.getUserObject();
                            showNodeContextMenu(node, e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }

    private void showNodeContextMenu(Node3DVO node, Component invoker, int x, int y) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem renameItem = new JMenuItem("Rename...", DarkThemeUtils.getFatcowIcon("textfield_rename.png"));
        renameItem.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Enter new node name:", node.nodeName);
            if (newName != null && !newName.trim().isEmpty()) {
                node.nodeName = newName.trim();
                rebuildTree();
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete", DarkThemeUtils.getFatcowIcon("delete.png"));
        deleteItem.addActionListener(e -> {
            removeNodeFromScene(scene.rootNode, node);
            rebuildTree();
        });

        menu.add(renameItem);
        menu.add(deleteItem);
        menu.show(invoker, x, y);
    }

    private boolean removeNodeFromScene(Node3DVO parent, Node3DVO target) {
        if (parent.children.remove(target)) {
            return true;
        }
        for (Node3DVO child : parent.children) {
            if (removeNodeFromScene(child, target)) {
                return true;
            }
        }
        return false;
    }

    private static class SceneGraphCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            setBackgroundNonSelectionColor(DarkThemeUtils.BG_DARK);
            setBackgroundSelectionColor(new Color(44, 93, 212));
            setTextNonSelectionColor(DarkThemeUtils.TEXT_PRIMARY);
            setTextSelectionColor(Color.WHITE);
            setBorderSelectionColor(null);

            if (value instanceof DefaultMutableTreeNode) {
                Object uo = ((DefaultMutableTreeNode) value).getUserObject();
                if (uo instanceof Node3DVO) {
                    Node3DVO n = (Node3DVO) uo;
                    setText(n.nodeName);
                    switch (n.nodeType) {
                        case MESH:
                            setIcon(DarkThemeUtils.getFatcowIcon("box.png"));
                            break;
                        case LIGHT_POINT:
                        case LIGHT_DIRECTIONAL:
                            setIcon(DarkThemeUtils.getFatcowIcon("lightning.png"));
                            break;
                        case CAMERA:
                            setIcon(DarkThemeUtils.getFatcowIcon("camera.png"));
                            break;
                        case PREFAB:
                            setIcon(DarkThemeUtils.getFatcowIcon("brick.png"));
                            break;
                        default:
                            setIcon(DarkThemeUtils.getFatcowIcon("folder.png"));
                            break;
                    }
                }
            }
            return this;
        }
    }
}
