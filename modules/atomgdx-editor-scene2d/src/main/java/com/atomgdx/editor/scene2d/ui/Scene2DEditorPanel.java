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
import java.util.function.Consumer;

/**
 * Clean Center Viewport Editor for HyperLap2D 2D Composite Scenes.
 * Dedicated to hardware OpenGL rendering on LwjglAWTCanvas, top toolbars, and transform gizmos.
 * The Hierarchy and Inspector panels are promoted to first-class NetBeans TopComponents.
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

    private int lastMouseX, lastMouseY;
    private boolean isPanning = false;
    private Consumer<MainItemVO> itemSelectionListener;

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

        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeColors.BG_WINDOW);

        // Center Viewport powered by LibGDX LwjglAWTCanvas
        viewportListener = new Scene2DViewportListener(this.scene);
        gdxViewport = new GdxAwtViewport(viewportListener);
        gdxViewport.setBorder(new LineBorder(DarkThemeColors.BORDER, 1));

        // Setup Viewport Mouse Controls
        setupCanvasInteractions();

        // Top Toolbar
        JToolBar toolBar = createToolBar();

        add(toolBar, BorderLayout.NORTH);
        add(gdxViewport, BorderLayout.CENTER);

        // Select first item by default
        if (!this.scene.composite.sImages.isEmpty()) {
            MainItemVO first = this.scene.composite.sImages.get(0);
            viewportListener.getGizmo().setTargetItem(first);
        }
    }

    public void setItemSelectionListener(Consumer<MainItemVO> listener) {
        this.itemSelectionListener = listener;
    }

    public void setSelectedItem(MainItemVO item) {
        viewportListener.getGizmo().setTargetItem(item);
    }

    public int getRenderedFrameCount() {
        return viewportListener != null ? viewportListener.getFrameCount() : 0;
    }

    public SceneVO getScene() {
        return scene;
    }

    public Scene2DViewportListener getViewportListener() {
        return viewportListener;
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
                        if (itemSelectionListener != null) {
                            itemSelectionListener.accept(hitItem);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPanning = false;
                viewportListener.getGizmo().endDrag();
                if (itemSelectionListener != null) {
                    itemSelectionListener.accept(viewportListener.getGizmo().getTargetItem());
                }
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
