package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.core.viewport.GdxAwtViewport;
import com.atomgdx.editor.scene2d.data.vo.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

/**
 * Full HyperLap2D 2D Composite Scene Editor Panel in AtomGDX.
 * Features hardware OpenGL viewport on LwjglAWTCanvas, Hierarchy & Layer Manager,
 * Interactive Transform Gizmos, Box2D Physics Inspector, Asset Library, and JetBrains Dark Theme.
 */
public class Scene2DEditorPanel extends JPanel {

    public static class DarkThemeColors {
        public static final Color BG_WINDOW = new Color(30, 31, 34);           // #1e1f22
        public static final Color BG_PANEL = new Color(43, 45, 48);            // #2b2d30
        public static final Color BG_DARK = new Color(30, 31, 34);             // #1e1f22
        public static final Color ACCENT_PRIMARY = new Color(53, 116, 240);    // #3574f0 (IDE Blue)
        public static final Color TEXT_PRIMARY = new Color(223, 225, 229);     // #dfe1e5
        public static final Color TEXT_SECONDARY = new Color(154, 160, 166);   // #9aa0a6
        public static final Color BORDER = new Color(60, 63, 65);              // #3c3f41
    }

    private final SceneVO scene;
    private final GdxAwtViewport gdxViewport;
    private final Scene2DViewportListener viewportListener;

    private final SceneHierarchyTreePanel hierarchyPanel;
    private final SceneItemInspectorPanel inspectorPanel;
    private final SceneAssetBrowserPanel assetBrowserPanel;

    private int lastMouseX, lastMouseY;
    private boolean isPanning = false;

    public Scene2DEditorPanel() {
        this(new SceneVO("MainScene"));
    }

    public Scene2DEditorPanel(com.atomgdx.editor.scene2d.Scene2DModel oldModel) {
        this(oldModel != null ? new SceneVO(oldModel.getSceneName()) : new SceneVO("MainScene"));
    }

    public Scene2DEditorPanel(SceneVO scene) {
        this.scene = scene != null ? scene : new SceneVO("MainScene");
        if (this.scene.composite.sImages.isEmpty()) {
            this.scene.composite.sImages.add(new SimpleImageVO("player_ship", 600, 300));
            this.scene.composite.sImages.add(new SimpleImageVO("nebula_bg", 400, 200));
        }

        setLayout(new BorderLayout(6, 6));
        setBackground(DarkThemeColors.BG_WINDOW);
        setBorder(new EmptyBorder(4, 4, 4, 4));

        // Center Viewport powered by LibGDX LwjglAWTCanvas
        viewportListener = new Scene2DViewportListener(this.scene);
        gdxViewport = new GdxAwtViewport(viewportListener);
        gdxViewport.setBorder(new LineBorder(DarkThemeColors.BORDER, 1));

        // Sidebars
        hierarchyPanel = new SceneHierarchyTreePanel(this.scene);
        inspectorPanel = new SceneItemInspectorPanel(this.scene);
        assetBrowserPanel = new SceneAssetBrowserPanel();

        JTabbedPane leftTabs = new JTabbedPane();
        leftTabs.setBackground(DarkThemeColors.BG_PANEL);
        leftTabs.setForeground(DarkThemeColors.TEXT_PRIMARY);
        leftTabs.setPreferredSize(new Dimension(280, 600));
        leftTabs.addTab("Hierarchy", getIcon("folder.png"), hierarchyPanel);
        leftTabs.addTab("Assets", getIcon("picture.png"), assetBrowserPanel);

        inspectorPanel.setPreferredSize(new Dimension(300, 600));

        // Connect Listeners
        hierarchyPanel.setSelectionListener(item -> {
            viewportListener.getGizmo().setTargetItem(item);
            inspectorPanel.setItem(item);
        });

        hierarchyPanel.setChangeListener(() -> {
            inspectorPanel.setItem(hierarchyPanel.getSelectedItem());
        });

        inspectorPanel.setChangeListener(() -> {
            hierarchyPanel.rebuildTree();
            hierarchyPanel.selectItem(viewportListener.getGizmo().getTargetItem());
        });

        assetBrowserPanel.setAssetAddListener(file -> {
            String name = file.getName();
            viewportListener.loadTexture(name, file);
            SimpleImageVO img = new SimpleImageVO(name, 640, 360);
            this.scene.composite.sImages.add(img);
            hierarchyPanel.rebuildTree();
            hierarchyPanel.selectItem(img);
            viewportListener.getGizmo().setTargetItem(img);
            inspectorPanel.setItem(img);
        });

        // Setup Viewport Mouse Controls
        setupCanvasInteractions();

        // Top Toolbar
        JToolBar toolBar = createToolBar();

        add(toolBar, BorderLayout.NORTH);
        add(leftTabs, BorderLayout.WEST);
        add(gdxViewport, BorderLayout.CENTER);
        add(inspectorPanel, BorderLayout.EAST);

        // Select first item by default
        if (!this.scene.composite.sImages.isEmpty()) {
            MainItemVO first = this.scene.composite.sImages.get(0);
            hierarchyPanel.selectItem(first);
            viewportListener.getGizmo().setTargetItem(first);
            inspectorPanel.setItem(first);
        }
    }

    public int getRenderedFrameCount() {
        return viewportListener != null ? viewportListener.getFrameCount() : 0;
    }

    public SceneVO getScene() {
        return scene;
    }

    public static ImageIcon getIcon(String name) {
        try {
            URL url = Scene2DEditorPanel.class.getResource("icons/" + name);
            if (url == null) {
                url = Scene2DEditorPanel.class.getResource("/com/atomgdx/editor/scene2d/ui/icons/" + name);
            }
            if (url == null) {
                url = Scene2DEditorPanel.class.getResource("/com/atomgdx/editor/particle2d/ui/icons/" + name);
            }
            if (url == null) {
                url = Scene2DEditorPanel.class.getClassLoader().getResource("com/atomgdx/editor/particle2d/ui/icons/" + name);
            }
            if (url != null) {
                return new ImageIcon(url);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(DarkThemeColors.BG_PANEL);
        tb.setBorder(new LineBorder(DarkThemeColors.BORDER, 1));

        JButton transBtn = new JButton("Translate", getIcon("arrow_refresh.png"));
        JButton scaleBtn = new JButton("Scale", getIcon("cog.png"));
        JButton rotBtn = new JButton("Rotate", getIcon("star.png"));
        JToggleButton gridBtn = new JToggleButton("Grid", true);
        JButton saveBtn = new JButton("Save Scene (.dt)", getIcon("folder.png"));

        for (AbstractButton b : new AbstractButton[]{transBtn, scaleBtn, rotBtn, gridBtn, saveBtn}) {
            b.setBackground(DarkThemeColors.BG_PANEL);
            b.setForeground(DarkThemeColors.TEXT_PRIMARY);
            b.setFocusPainted(false);
        }

        transBtn.addActionListener(e -> viewportListener.getGizmo().setMode(TransformGizmo.GizmoMode.TRANSLATE));
        scaleBtn.addActionListener(e -> viewportListener.getGizmo().setMode(TransformGizmo.GizmoMode.SCALE));
        rotBtn.addActionListener(e -> viewportListener.getGizmo().setMode(TransformGizmo.GizmoMode.ROTATE));
        gridBtn.addActionListener(e -> viewportListener.setShowGrid(gridBtn.isSelected()));

        saveBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(scene.sceneName + ".dt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    HyperLap2DSerializer.saveSceneToFile(scene, chooser.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Scene saved successfully to " + chooser.getSelectedFile().getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving scene: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tb.add(transBtn);
        tb.add(scaleBtn);
        tb.add(rotBtn);
        tb.addSeparator();
        tb.add(gridBtn);
        tb.addSeparator();
        tb.add(saveBtn);

        return tb;
    }

    private void setupCanvasInteractions() {
        if (gdxViewport.getCanvas() == null) return;
        Canvas canvas = gdxViewport.getCanvas().getCanvas();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = true;
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    Vector3 world = viewportListener.screenToWorld(e.getX(), e.getY());
                    TransformGizmo.HandleType handle = viewportListener.getGizmo().hitTest(world.x, world.y, 10f);

                    if (handle != TransformGizmo.HandleType.NONE) {
                        viewportListener.getGizmo().startDrag(world.x, world.y, handle);
                    } else {
                        // Hit test items in scene
                        MainItemVO hitItem = null;
                        for (SimpleImageVO img : scene.composite.sImages) {
                            float w = img.width > 0 ? img.width : 64;
                            float h = img.height > 0 ? img.height : 64;
                            Rectangle r = new Rectangle(img.x, img.y, w * img.scaleX, h * img.scaleY);
                            if (r.contains(world.x, world.y)) {
                                hitItem = img;
                                break;
                            }
                        }
                        viewportListener.getGizmo().setTargetItem(hitItem);
                        hierarchyPanel.selectItem(hitItem);
                        inspectorPanel.setItem(hitItem);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPanning = false;
                viewportListener.getGizmo().endDrag();
                inspectorPanel.setItem(viewportListener.getGizmo().getTargetItem());
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                if (isPanning) {
                    OrthographicCamera cam = viewportListener.getCamera();
                    if (cam != null) {
                        cam.translate(-dx * cam.zoom, dy * cam.zoom, 0);
                        cam.update();
                    }
                } else {
                    Vector3 world = viewportListener.screenToWorld(e.getX(), e.getY());
                    viewportListener.getGizmo().updateDrag(world.x, world.y);
                    inspectorPanel.setItem(viewportListener.getGizmo().getTargetItem());
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            OrthographicCamera cam = viewportListener.getCamera();
            if (cam != null) {
                float zoomFactor = e.getPreciseWheelRotation() > 0 ? 1.1f : 0.9f;
                cam.zoom = Math.max(0.1f, Math.min(10f, cam.zoom * zoomFactor));
                cam.update();
            }
        });
    }
}
