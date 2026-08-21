package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
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
    private Consumer<java.util.List<MainItemVO>> multiSelectionListener;

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
        gdxViewport.setBorder(null);

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

    public void setMultiSelectionListener(Consumer<java.util.List<MainItemVO>> listener) {
        this.multiSelectionListener = listener;
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

    public GdxAwtViewport getGdxViewport() {
        return gdxViewport;
    }

    public static ImageIcon getIcon(String name) {
        return DarkThemeUtils.getFatcowIcon(name);
    }

    public enum ToolMode {
        PAN,
        TRANSLATE,
        ROTATE,
        SCALE,
        RECT_SELECT
    }

    private ToolMode currentTool = ToolMode.TRANSLATE;
    private final Rectangle marqueeRect = new Rectangle();
    private boolean isMarqueeSelecting = false;
    private float marqueeStartX, marqueeStartY;

    private Runnable saveHandler;

    public void setSaveHandler(Runnable saveHandler) {
        this.saveHandler = saveHandler;
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(DarkThemeColors.BG_PANEL);
        tb.setBorder(new LineBorder(DarkThemeColors.BORDER, 1));

        JToggleButton panBtn = new JToggleButton("Pan", DarkThemeUtils.getFatcowIcon("hand.png"));
        panBtn.setToolTipText("Pan / Hand View (Q)");

        JToggleButton transBtn = new JToggleButton("Move", DarkThemeUtils.getFatcowIcon("transform_move.png"));
        transBtn.setToolTipText("Move / Translate Item (W)");
        transBtn.setSelected(true);

        JToggleButton rotBtn = new JToggleButton("Rotate", DarkThemeUtils.getFatcowIcon("transform_rotate.png"));
        rotBtn.setToolTipText("Rotate Item (E)");

        JToggleButton scaleBtn = new JToggleButton("Scale", DarkThemeUtils.getFatcowIcon("transform_scale.png"));
        scaleBtn.setToolTipText("Scale Item (R)");

        JToggleButton rectSelectBtn = new JToggleButton("Rect Select", DarkThemeUtils.getFatcowIcon("transform_selection.png"));
        rectSelectBtn.setToolTipText("Rectangular Marquee Select (T)");

        ButtonGroup toolsGroup = new ButtonGroup();
        for (JToggleButton b : new JToggleButton[]{panBtn, transBtn, rotBtn, scaleBtn, rectSelectBtn}) {
            toolsGroup.add(b);
            b.setBackground(DarkThemeColors.BG_PANEL);
            b.setForeground(DarkThemeColors.TEXT_PRIMARY);
            b.setFocusPainted(false);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            tb.add(b);
        }

        panBtn.addActionListener(e -> {
            currentTool = ToolMode.PAN;
        });
        transBtn.addActionListener(e -> {
            currentTool = ToolMode.TRANSLATE;
            viewportListener.getGizmo().setMode(TransformGizmo.GizmoMode.TRANSLATE);
        });
        rotBtn.addActionListener(e -> {
            currentTool = ToolMode.ROTATE;
            viewportListener.getGizmo().setMode(TransformGizmo.GizmoMode.ROTATE);
        });
        scaleBtn.addActionListener(e -> {
            currentTool = ToolMode.SCALE;
            viewportListener.getGizmo().setMode(TransformGizmo.GizmoMode.SCALE);
        });
        rectSelectBtn.addActionListener(e -> {
            currentTool = ToolMode.RECT_SELECT;
        });

        tb.addSeparator();

        JButton focusBtn = new JButton("Focus (F)", DarkThemeUtils.getFatcowIcon("zoom_selection.png"));
        focusBtn.setToolTipText("Center Viewport on Selected Item");
        focusBtn.setBackground(DarkThemeColors.BG_PANEL);
        focusBtn.setForeground(DarkThemeColors.TEXT_PRIMARY);
        focusBtn.setFocusPainted(false);
        focusBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        focusBtn.addActionListener(e -> focusSelectedItem());
        tb.add(focusBtn);

        JToggleButton gridBtn = new JToggleButton("Grid", DarkThemeUtils.getFatcowIcon("grid.png"), true);
        gridBtn.setBackground(DarkThemeColors.BG_PANEL);
        gridBtn.setForeground(DarkThemeColors.TEXT_PRIMARY);
        gridBtn.setFocusPainted(false);
        gridBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gridBtn.addActionListener(e -> viewportListener.setShowGrid(gridBtn.isSelected()));
        tb.add(gridBtn);

        tb.addSeparator();

        JButton saveBtn = new JButton("Save", DarkThemeUtils.getFatcowIcon("diskette.png"));
        saveBtn.setToolTipText("Save Scene (Ctrl+S)");
        saveBtn.setBackground(DarkThemeColors.BG_PANEL);
        saveBtn.setForeground(DarkThemeColors.TEXT_PRIMARY);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        saveBtn.addActionListener(e -> {
            if (saveHandler != null) {
                saveHandler.run();
            } else {
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
            }
        });
        tb.add(saveBtn);

        return tb;
    }

    public void selectItem(MainItemVO item) {
        if (viewportListener != null && viewportListener.getGizmo() != null) {
            viewportListener.getGizmo().setTargetItem(item);
        }
    }

    public void focusSelectedItem() {
        MainItemVO target = viewportListener.getGizmo().getTargetItem();
        if (target != null && viewportListener.getCamera() != null) {
            viewportListener.getCamera().position.set(target.x + target.originX, target.y + target.originY, 0);
            viewportListener.getCamera().update();
        } else if (viewportListener != null) {
            viewportListener.fitSceneToViewport();
        }
    }

    private void setupCanvasInteractions() {
        if (gdxViewport.getCanvas() == null) return;
        Canvas canvas = gdxViewport.getCanvas().getCanvas();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e) || currentTool == ToolMode.PAN) {
                    isPanning = true;
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    int canvasW = canvas.getWidth();
                    int canvasH = canvas.getHeight();
                    Vector3 world = viewportListener.screenToWorld(e.getX(), e.getY(), canvasW, canvasH);

                    if (currentTool == ToolMode.RECT_SELECT) {
                        isMarqueeSelecting = true;
                        marqueeStartX = world.x;
                        marqueeStartY = world.y;
                        marqueeRect.set(world.x, world.y, 0, 0);
                        viewportListener.setMarquee(marqueeRect, true);
                        return;
                    }

                    TransformGizmo.HandleType handle = viewportListener.getGizmo().hitTest(world.x, world.y, 14f);

                    if (handle != TransformGizmo.HandleType.NONE) {
                        viewportListener.getGizmo().startDrag(world.x, world.y, handle);
                    } else {
                        // Hit test all item types in scene in reverse z-order
                        MainItemVO hitItem = findItemAt(world.x, world.y);
                        setSelectedItem(hitItem);
                        if (hitItem != null) {
                            TransformGizmo.HandleType centerH = viewportListener.getGizmo().hitTest(world.x, world.y, 14f);
                            if (centerH != TransformGizmo.HandleType.NONE) {
                                viewportListener.getGizmo().startDrag(world.x, world.y, centerH);
                            }
                        }
                        if (itemSelectionListener != null) {
                            itemSelectionListener.accept(hitItem);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPanning = false;
                if (isMarqueeSelecting) {
                    isMarqueeSelecting = false;
                    viewportListener.setMarquee(null, false);
                    java.util.List<MainItemVO> hits = findAllItemsInRect(marqueeRect);
                    if (hits.size() > 1) {
                        if (multiSelectionListener != null) {
                            multiSelectionListener.accept(hits);
                        }
                    } else if (!hits.isEmpty()) {
                        setSelectedItem(hits.get(0));
                        if (itemSelectionListener != null) {
                            itemSelectionListener.accept(hits.get(0));
                        }
                    }
                } else {
                    viewportListener.getGizmo().endDrag();
                    if (itemSelectionListener != null) {
                        itemSelectionListener.accept(viewportListener.getGizmo().getTargetItem());
                    }
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

                int canvasW = canvas.getWidth();
                int canvasH = canvas.getHeight();

                if (isPanning) {
                    OrthographicCamera cam = viewportListener.getCamera();
                    if (cam != null) {
                        cam.translate(-dx * cam.zoom, dy * cam.zoom, 0);
                        cam.update();
                    }
                } else if (isMarqueeSelecting) {
                    Vector3 world = viewportListener.screenToWorld(e.getX(), e.getY(), canvasW, canvasH);
                    float minX = Math.min(marqueeStartX, world.x);
                    float minY = Math.min(marqueeStartY, world.y);
                    float w = Math.abs(world.x - marqueeStartX);
                    float h = Math.abs(world.y - marqueeStartY);
                    marqueeRect.set(minX, minY, w, h);
                    viewportListener.setMarquee(marqueeRect, true);
                } else {
                    Vector3 world = viewportListener.screenToWorld(e.getX(), e.getY(), canvasW, canvasH);
                    viewportListener.getGizmo().updateDrag(world.x, world.y);
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            OrthographicCamera cam = viewportListener.getCamera();
            if (cam != null) {
                int canvasW = canvas.getWidth();
                int canvasH = canvas.getHeight();
                Vector3 worldBefore = viewportListener.screenToWorld(e.getX(), e.getY(), canvasW, canvasH);
                float zoomFactor = e.getPreciseWheelRotation() > 0 ? 1.15f : (1f / 1.15f);
                cam.zoom = Math.max(0.05f, Math.min(20f, cam.zoom * zoomFactor));
                cam.update();
                Vector3 worldAfter = viewportListener.screenToWorld(e.getX(), e.getY(), canvasW, canvasH);
                cam.translate(worldBefore.x - worldAfter.x, worldBefore.y - worldAfter.y, 0);
                cam.update();
            }
        });
    }

    private boolean isItemInteractive(MainItemVO item) {
        if (item == null || !item.isVisible || item.isLocked) return false;
        if (scene != null && scene.composite != null && item.layerName != null) {
            for (LayerItemVO l : scene.composite.layers) {
                if (item.layerName.equals(l.layerName)) {
                    if (!l.isVisible || l.isLocked) return false;
                    break;
                }
            }
        }
        return true;
    }

    private int getLayerOrder(String layerName) {
        if (scene == null || scene.composite == null || layerName == null) return 0;
        for (int i = 0; i < scene.composite.layers.size(); i++) {
            if (layerName.equals(scene.composite.layers.get(i).layerName)) {
                return i;
            }
        }
        return 0;
    }

    private Rectangle getItemBounds(MainItemVO item) {
        float w = 64f, h = 64f;
        if (item instanceof SimpleImageVO) {
            SimpleImageVO img = (SimpleImageVO) item;
            if (img.width > 0) w = img.width;
            if (img.height > 0) h = img.height;
        } else if (item instanceof NinePatchVO) {
            NinePatchVO np = (NinePatchVO) item;
            if (np.width > 0) w = np.width;
            if (np.height > 0) h = np.height;
        } else if (item instanceof LabelVO) {
            LabelVO lbl = (LabelVO) item;
            w = lbl.width > 0 ? lbl.width : (lbl.text != null ? lbl.text.length() * 10f : 60f);
            h = lbl.height > 0 ? lbl.height : 20f;
        } else if (item instanceof CompositeItemVO) {
            CompositeItemVO comp = (CompositeItemVO) item;
            if (comp.width > 0) w = comp.width;
            if (comp.height > 0) h = comp.height;
        }

        float sx = item.scaleX != 0 ? item.scaleX : 1f;
        float sy = item.scaleY != 0 ? item.scaleY : 1f;
        float ox = item.originX;
        float oy = item.originY;
        float posX = (ox != 0 || oy != 0) ? (item.x + ox - ox * sx) : item.x;
        float posY = (ox != 0 || oy != 0) ? (item.y + oy - oy * sy) : item.y;
        return new Rectangle(posX, posY, w * sx, h * sy);
    }

    public MainItemVO findItemAt(float worldX, float worldY) {
        if (scene == null || scene.composite == null) return null;

        java.util.List<MainItemVO> allItems = new java.util.ArrayList<>();
        allItems.addAll(scene.composite.sComposites);
        allItems.addAll(scene.composite.sNinePatches);
        allItems.addAll(scene.composite.sLabels);
        allItems.addAll(scene.composite.sLights);
        allItems.addAll(scene.composite.sParticleEffects);
        allItems.addAll(scene.composite.sImages);

        // Sort by Layer Index DESC, then zIndex DESC (topmost drawn items first)
        allItems.sort((a, b) -> {
            int layerA = getLayerOrder(a.layerName);
            int layerB = getLayerOrder(b.layerName);
            if (layerA != layerB) {
                return Integer.compare(layerB, layerA);
            }
            return Integer.compare(b.zIndex, a.zIndex);
        });

        for (MainItemVO item : allItems) {
            if (!isItemInteractive(item)) continue;

            if (item instanceof LightVO) {
                LightVO lt = (LightVO) item;
                float r = lt.distance > 0 ? lt.distance : 32f;
                if (com.badlogic.gdx.math.Vector2.dst(worldX, worldY, lt.x, lt.y) <= r) return lt;
            } else if (item instanceof ParticleEffectVO) {
                ParticleEffectVO p = (ParticleEffectVO) item;
                if (new Rectangle(p.x - 30, p.y - 30, 60, 60).contains(worldX, worldY)) return p;
            } else {
                Rectangle b = getItemBounds(item);
                if (b != null && b.contains(worldX, worldY)) return item;
            }
        }
        return null;
    }

    public MainItemVO findItemInRect(Rectangle rect) {
        java.util.List<MainItemVO> hits = findAllItemsInRect(rect);
        return !hits.isEmpty() ? hits.get(0) : null;
    }

    public java.util.List<MainItemVO> findAllItemsInRect(Rectangle rect) {
        java.util.List<MainItemVO> list = new java.util.ArrayList<>();
        if (scene == null || scene.composite == null || rect == null) return list;

        java.util.List<MainItemVO> allItems = new java.util.ArrayList<>();
        allItems.addAll(scene.composite.sComposites);
        allItems.addAll(scene.composite.sNinePatches);
        allItems.addAll(scene.composite.sLabels);
        allItems.addAll(scene.composite.sLights);
        allItems.addAll(scene.composite.sParticleEffects);
        allItems.addAll(scene.composite.sImages);

        allItems.sort((a, b) -> {
            int layerA = getLayerOrder(a.layerName);
            int layerB = getLayerOrder(b.layerName);
            if (layerA != layerB) {
                return Integer.compare(layerB, layerA);
            }
            return Integer.compare(b.zIndex, a.zIndex);
        });

        for (MainItemVO item : allItems) {
            if (!isItemInteractive(item)) continue;

            if (item instanceof LightVO) {
                if (rect.contains(item.x, item.y)) list.add(item);
            } else if (item instanceof ParticleEffectVO) {
                if (rect.contains(item.x, item.y)) list.add(item);
            } else {
                Rectangle b = getItemBounds(item);
                if (b != null && rect.overlaps(b)) list.add(item);
            }
        }
        return list;
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}
