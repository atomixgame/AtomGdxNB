package com.atomgdx.editor.scene2d.tilemap.ui;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.tilemap.io.TmxSerializer;
import com.atomgdx.editor.scene2d.tilemap.physics.TilemapCollisionGenerator;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Full TileMap Studio Main Editor Panel integrating the interactive viewport canvas,
 * Unity Tile Palette, Multi-layer Hierarchy manager, and native LibGDX TMX Exporter.
 */
public class TilemapEditorPanel extends JPanel {

    private final TilemapDocument document;
    private final TilePalettePanel palettePanel;
    private final TilemapLayersPanel layersPanel;
    private final JPanel canvas;

    private float zoomScale = 1.0f;
    private int panOffsetX = 40;
    private int panOffsetY = 40;
    private int lastMouseX, lastMouseY;
    private boolean isPanning = false;

    private int hoverCellX = -1;
    private int hoverCellY = -1;
    private boolean showGrid = true;
    private boolean showCollisions = true;

    // Drag tracking for rect fill & line tools
    private int dragStartX = -1, dragStartY = -1;
    private boolean isDraggingTool = false;

    public TilemapEditorPanel() {
        this(new TilemapDocument());
    }

    public TilemapEditorPanel(TilemapDocument doc) {
        this.document = doc != null ? doc : new TilemapDocument();
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        // Top Toolbar
        JToolBar toolBar = createTopToolbar();
        add(toolBar, BorderLayout.NORTH);

        // Center Split Layout: Left Tile Palette, Center Map Canvas, Right Layers Panel
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(250);
        mainSplit.setBorder(null);

        palettePanel = new TilePalettePanel();
        palettePanel.setPreferredSize(new Dimension(250, 600));
        mainSplit.setLeftComponent(palettePanel);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplit.setResizeWeight(1.0);
        rightSplit.setDividerLocation(850);
        rightSplit.setBorder(null);

        // Canvas Viewport
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderTilemapCanvas((Graphics2D) g);
            }
        };
        canvas.setBackground(document.getBackgroundColor());
        setupCanvasInteractions();
        rightSplit.setLeftComponent(canvas);

        layersPanel = new TilemapLayersPanel(this.document);
        layersPanel.setPreferredSize(new Dimension(240, 600));
        layersPanel.setLayerChangeListener(canvas::repaint);
        rightSplit.setRightComponent(layersPanel);

        mainSplit.setRightComponent(rightSplit);
        add(mainSplit, BorderLayout.CENTER);
    }

    private JToolBar createTopToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(new Color(28, 30, 34));
        tb.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("TileMap Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        tb.add(title);
        tb.addSeparator(new Dimension(16, 20));

        // Orientation
        tb.add(new JLabel("Orientation: "));
        JComboBox<TilemapGridMode> modeCombo = new JComboBox<>(TilemapGridMode.values());
        modeCombo.setSelectedItem(document.getOrientation());
        modeCombo.addActionListener(e -> {
            document.setOrientation((TilemapGridMode) modeCombo.getSelectedItem());
            canvas.repaint();
        });
        tb.add(modeCombo);
        tb.addSeparator(new Dimension(10, 20));

        // Grid toggle
        JCheckBox gridCheck = new JCheckBox("Grid", showGrid);
        gridCheck.setOpaque(false);
        gridCheck.setForeground(Color.WHITE);
        gridCheck.addActionListener(e -> { showGrid = gridCheck.isSelected(); canvas.repaint(); });
        tb.add(gridCheck);

        // Collision toggle
        JCheckBox colCheck = new JCheckBox("Colliders", showCollisions);
        colCheck.setOpaque(false);
        colCheck.setForeground(new Color(0, 255, 180));
        colCheck.addActionListener(e -> { showCollisions = colCheck.isSelected(); canvas.repaint(); });
        tb.add(colCheck);
        tb.addSeparator(new Dimension(10, 20));

        // Export TMX button
        JButton exportTmxBtn = new JButton("Export LibGDX .TMX");
        exportTmxBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exportTmxBtn.setBackground(new Color(44, 93, 212));
        exportTmxBtn.setForeground(Color.WHITE);
        exportTmxBtn.addActionListener(e -> exportTmxFile());
        tb.add(exportTmxBtn);

        return tb;
    }

    private void renderTilemapCanvas(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tw = Math.max(4, Math.round(document.getTileWidth() * zoomScale));
        int th = Math.max(4, Math.round(document.getTileHeight() * zoomScale));
        int cols = document.getWidth();
        int rows = document.getHeight();

        // Render Layers bottom-to-top
        for (TilemapLayer layer : document.getLayers()) {
            if (!layer.isVisible()) continue;

            float alpha = layer.getOpacity();
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            if (layer instanceof TileLayer) {
                TileLayer tl = (TileLayer) layer;
                for (int y = 0; y < tl.getHeight(); y++) {
                    for (int x = 0; x < tl.getWidth(); x++) {
                        int gid = tl.getTile(x, y);
                        if (gid > 0) {
                            Point screenPos = cellToScreen(x, y, tw, th);
                            Color c = TilePalettePanel.getTileProceduralColor(gid);
                            g2.setColor(c);
                            g2.fillRect(screenPos.x, screenPos.y, tw, th);
                        }
                    }
                }
            } else if (layer instanceof ObjectGroupLayer) {
                ObjectGroupLayer ogl = (ObjectGroupLayer) layer;
                for (MapObjectVO obj : ogl.getObjects()) {
                    if (!obj.visible) continue;
                    int ox = panOffsetX + Math.round(obj.x * zoomScale);
                    int oy = panOffsetY + Math.round(obj.y * zoomScale);
                    int ow = Math.round(obj.width * zoomScale);
                    int oh = Math.round(obj.height * zoomScale);

                    g2.setColor(new Color(obj.color.getRed(), obj.color.getGreen(), obj.color.getBlue(), 90));
                    g2.fillRect(ox, oy, ow, oh);
                    g2.setColor(obj.color);
                    g2.drawRect(ox, oy, ow, oh);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.drawString(obj.name + " (" + obj.type + ")", ox + 4, oy + 14);
                }
            }

            g2.setComposite(oldComp);
        }

        // Render Colliders overlay (Green merged boxes)
        if (showCollisions) {
            TilemapLayer active = document.getActiveLayer();
            if (active instanceof TileLayer) {
                List<Rectangle> colliders = TilemapCollisionGenerator.generateMergedBoxColliders((TileLayer) active, tw, th);
                g2.setColor(new Color(0, 255, 120, 160));
                g2.setStroke(new BasicStroke(2.0f));
                for (Rectangle r : colliders) {
                    Point sp = cellToScreen(r.x / tw, r.y / th, tw, th);
                    g2.drawRect(sp.x, sp.y, r.width, r.height);
                }
            }
        }

        // Render Grid Lines
        if (showGrid) {
            g2.setColor(new Color(55, 60, 70, 100));
            g2.setStroke(new BasicStroke(1.0f));
            for (int x = 0; x <= cols; x++) {
                Point p1 = cellToScreen(x, 0, tw, th);
                Point p2 = cellToScreen(x, rows, tw, th);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            for (int y = 0; y <= rows; y++) {
                Point p1 = cellToScreen(0, y, tw, th);
                Point p2 = cellToScreen(cols, y, tw, th);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Hover ghost preview
        if (hoverCellX >= 0 && hoverCellX < cols && hoverCellY >= 0 && hoverCellY < rows) {
            Point hp = cellToScreen(hoverCellX, hoverCellY, tw, th);
            g2.setColor(new Color(0, 220, 255, 120));
            g2.fillRect(hp.x, hp.y, tw, th);
            g2.setColor(new Color(0, 220, 255));
            g2.drawRect(hp.x, hp.y, tw, th);
        }
    }

    private Point cellToScreen(int cx, int cy, int tw, int th) {
        if (document.getOrientation() == TilemapGridMode.ISOMETRIC_DIAMOND || document.getOrientation() == TilemapGridMode.ISOMETRIC_STAGGERED) {
            int sx = panOffsetX + (cx - cy) * (tw / 2) + (document.getHeight() * (tw / 2));
            int sy = panOffsetY + (cx + cy) * (th / 2);
            return new Point(sx, sy);
        }
        return new Point(panOffsetX + cx * tw, panOffsetY + cy * th);
    }

    private Point screenToCell(int sx, int sy) {
        int tw = Math.max(4, Math.round(document.getTileWidth() * zoomScale));
        int th = Math.max(4, Math.round(document.getTileHeight() * zoomScale));
        int cx = (sx - panOffsetX) / tw;
        int cy = (sy - panOffsetY) / th;
        return new Point(cx, cy);
    }

    private void setupCanvasInteractions() {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = true;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    Point cell = screenToCell(e.getX(), e.getY());
                    dragStartX = cell.x;
                    dragStartY = cell.y;
                    isDraggingTool = true;
                    applyToolAt(cell.x, cell.y, false);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = false;
                } else if (SwingUtilities.isLeftMouseButton(e) && isDraggingTool) {
                    Point cell = screenToCell(e.getX(), e.getY());
                    applyToolAt(cell.x, cell.y, true);
                    isDraggingTool = false;
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPanning) {
                    panOffsetX += (e.getX() - lastMouseX);
                    panOffsetY += (e.getY() - lastMouseY);
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    canvas.repaint();
                } else if (isDraggingTool) {
                    Point cell = screenToCell(e.getX(), e.getY());
                    if (palettePanel.getActiveTool() == TilemapTool.PAINT_BRUSH || palettePanel.getActiveTool() == TilemapTool.ERASER) {
                        applyToolAt(cell.x, cell.y, false);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point cell = screenToCell(e.getX(), e.getY());
                hoverCellX = cell.x;
                hoverCellY = cell.y;
                canvas.repaint();
            }
        });

        canvas.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                zoomScale = Math.min(4.0f, zoomScale * 1.15f);
            } else {
                zoomScale = Math.max(0.25f, zoomScale / 1.15f);
            }
            canvas.repaint();
        });
    }

    private void applyToolAt(int cx, int cy, boolean isRelease) {
        TilemapLayer activeLayer = document.getActiveLayer();
        if (!(activeLayer instanceof TileLayer) || activeLayer.isLocked()) return;

        TileLayer tl = (TileLayer) activeLayer;
        TilemapTool tool = palettePanel.getActiveTool();
        int gid = palettePanel.getSelectedTileGid();

        switch (tool) {
            case PAINT_BRUSH:
                tl.setTile(cx, cy, gid);
                break;
            case ERASER:
                tl.setTile(cx, cy, 0);
                break;
            case BUCKET_FILL:
                tl.floodFill(cx, cy, gid);
                break;
            case EYEDROPPER:
                int picked = tl.getTile(cx, cy);
                if (picked > 0) palettePanel.setSelectedTileGid(picked);
                break;
            case RECT_FILL:
                if (isRelease && dragStartX >= 0 && dragStartY >= 0) {
                    int rx = Math.min(dragStartX, cx);
                    int ry = Math.min(dragStartY, cy);
                    int rw = Math.abs(cx - dragStartX) + 1;
                    int rh = Math.abs(cy - dragStartY) + 1;
                    tl.fillRect(rx, ry, rw, rh, gid);
                }
                break;
            case AUTOTILE_RULE:
                tl.setTile(cx, cy, gid);
                // Dynamically resolve adjacent auto-tiling bitmasks
                AutoTileRule rule = new AutoTileRule("AutoTile", gid);
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = cx + dx;
                        int ny = cy + dy;
                        if (tl.getTile(nx, ny) > 0) {
                            int mask = AutoTileRule.calculateBitmask(tl, nx, ny);
                            tl.setTile(nx, ny, rule.resolveTile(mask));
                        }
                    }
                }
                break;
        }

        canvas.repaint();
    }

    private void exportTmxFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(document.getName() + ".tmx"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                TmxSerializer.saveTmx(document, target);
                JOptionPane.showMessageDialog(this,
                        "Successfully exported LibGDX TMX Level:\n" + target.getAbsolutePath() + "\nLoad with: new TmxMapLoader().load(...)",
                        "TMX Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting TMX: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
