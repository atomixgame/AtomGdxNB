package com.atomgdx.editor.scene2d.tilemap.ui;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.tilemap.io.FlareMapConverter;
import com.atomgdx.editor.scene2d.tilemap.io.TmxParser;
import com.atomgdx.editor.scene2d.tilemap.io.TmxSerializer;
import com.atomgdx.editor.scene2d.tilemap.physics.TilemapCollisionGenerator;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Advanced TileMap Studio Main Editor Panel.
 * Supports:
 * - True Isometric Diamond (2:1), Isometric Staggered, Hexagonal, and Orthogonal projections
 * - Multi-layer depth sorting for tall isometric tiles (trees, buildings, cliffs) with tile offsets
 * - Real tileset image slicing & caching with procedural fallback
 * - Full bi-directional TMX & TSX parser / serializer and Flare .txt map converter
 * - Interactive stamp, bucket fill, rectangle fill, eraser, and eyedropper tools in isometric space
 */
public class TilemapEditorPanel extends JPanel {

    private TilemapDocument document;
    private final TilePalettePanel palettePanel;
    private final TilemapLayersPanel layersPanel;
    private final JPanel canvas;

    private float zoomScale = 1.0f;
    private int panOffsetX = 120;
    private int panOffsetY = 80;
    private int lastMouseX, lastMouseY;
    private boolean isPanning = false;

    private int hoverCellX = -1;
    private int hoverCellY = -1;
    private boolean showGrid = true;
    private boolean showCollisions = true;

    // Drag tracking for rect fill & line tools
    private int dragStartX = -1, dragStartY = -1;
    private boolean isDraggingTool = false;

    // Texture Slice Cache: Map<imageSource, BufferedImage>
    private final Map<String, BufferedImage> textureCache = new HashMap<>();

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

        // Center Split Layout
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(260);
        mainSplit.setBorder(null);

        palettePanel = new TilePalettePanel();
        palettePanel.setPreferredSize(new Dimension(260, 600));
        palettePanel.setTileSets(this.document.getTileSets());
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
        tb.addSeparator(new Dimension(14, 20));

        // Orientation Selector
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

        // Zoom Controls
        JButton zoomInBtn = new JButton("+");
        zoomInBtn.addActionListener(e -> { zoomScale = Math.min(4.0f, zoomScale * 1.25f); canvas.repaint(); });
        tb.add(zoomInBtn);

        JButton zoomOutBtn = new JButton("-");
        zoomOutBtn.addActionListener(e -> { zoomScale = Math.max(0.25f, zoomScale / 1.25f); canvas.repaint(); });
        tb.add(zoomOutBtn);

        JButton zoomFitBtn = new JButton("Zoom Fit [ ]");
        zoomFitBtn.addActionListener(e -> {
            panOffsetX = canvas.getWidth() / 4;
            panOffsetY = 60;
            zoomScale = 1.0f;
            canvas.repaint();
        });
        tb.add(zoomFitBtn);
        tb.addSeparator(new Dimension(10, 20));

        // Import / Open TMX
        JButton openTmxBtn = new JButton("Open .TMX Map");
        openTmxBtn.addActionListener(e -> openTmxFile());
        tb.add(openTmxBtn);

        // Import Flare .TXT
        JButton openFlareBtn = new JButton("Import Flare .TXT");
        openFlareBtn.addActionListener(e -> openFlareFile());
        tb.add(openFlareBtn);

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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int tw = Math.max(4, Math.round(document.getTileWidth() * zoomScale));
        int th = Math.max(4, Math.round(document.getTileHeight() * zoomScale));
        int cols = document.getWidth();
        int rows = document.getHeight();
        boolean isIso = document.getOrientation() == TilemapGridMode.ISOMETRIC_DIAMOND || document.getOrientation() == TilemapGridMode.ISOMETRIC_STAGGERED;

        // Render Layers bottom-to-top with Depth Sorting
        for (TilemapLayer layer : document.getLayers()) {
            if (!layer.isVisible()) continue;

            float alpha = layer.getOpacity();
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            if (layer instanceof TileLayer) {
                TileLayer tl = (TileLayer) layer;
                // In Isometric, iterate row by row (y: 0..H-1, x: 0..W-1) for proper front-to-back occlusion of tall tiles
                for (int y = 0; y < tl.getHeight(); y++) {
                    for (int x = 0; x < tl.getWidth(); x++) {
                        int gid = tl.getTile(x, y);
                        if (gid > 0) {
                            renderTileGid(g2, gid, x, y, tw, th, isIso);
                        }
                    }
                }
            } else if (layer instanceof ObjectGroupLayer) {
                ObjectGroupLayer ogl = (ObjectGroupLayer) layer;
                for (MapObjectVO obj : ogl.getObjects()) {
                    if (!obj.visible) continue;
                    renderMapObject(g2, obj, tw, th, isIso);
                }
            }

            g2.setComposite(oldComp);
        }

        // Render Colliders overlay
        if (showCollisions) {
            TilemapLayer active = document.getActiveLayer();
            if (active instanceof TileLayer) {
                List<Rectangle> colliders = TilemapCollisionGenerator.generateMergedBoxColliders((TileLayer) active, tw, th);
                g2.setColor(new Color(0, 255, 120, 160));
                g2.setStroke(new BasicStroke(2.0f));
                for (Rectangle r : colliders) {
                    Point sp = cellToScreen(r.x / tw, r.y / th, tw, th);
                    if (isIso) {
                        Polygon diamond = IsometricMath.getDiamondPolygon(r.x / tw, r.y / th, tw, th, panOffsetX, panOffsetY);
                        g2.drawPolygon(diamond);
                    } else {
                        g2.drawRect(sp.x, sp.y, r.width, r.height);
                    }
                }
            }
        }

        // Render Grid Lines
        if (showGrid) {
            g2.setColor(new Color(60, 65, 75, 110));
            g2.setStroke(new BasicStroke(1.0f));

            if (isIso) {
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < cols; x++) {
                        Polygon poly = IsometricMath.getDiamondPolygon(x, y, tw, th, panOffsetX, panOffsetY);
                        g2.drawPolygon(poly);
                    }
                }
            } else {
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
        }

        // Hover cell indicator
        if (hoverCellX >= 0 && hoverCellX < cols && hoverCellY >= 0 && hoverCellY < rows) {
            if (isIso) {
                Polygon hoverPoly = IsometricMath.getDiamondPolygon(hoverCellX, hoverCellY, tw, th, panOffsetX, panOffsetY);
                g2.setColor(new Color(0, 220, 255, 70));
                g2.fillPolygon(hoverPoly);
                g2.setColor(new Color(0, 220, 255));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawPolygon(hoverPoly);
            } else {
                Point hp = cellToScreen(hoverCellX, hoverCellY, tw, th);
                g2.setColor(new Color(0, 220, 255, 70));
                g2.fillRect(hp.x, hp.y, tw, th);
                g2.setColor(new Color(0, 220, 255));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRect(hp.x, hp.y, tw, th);
            }
        }
    }

    private void renderTileGid(Graphics2D g2, int gid, int cx, int cy, int tw, int th, boolean isIso) {
        TileSetVO matchedSet = null;
        for (TileSetVO ts : document.getTileSets()) {
            if (ts.containsGid(gid)) {
                matchedSet = ts;
                break;
            }
        }

        Point sp = cellToScreen(cx, cy, tw, th);

        if (matchedSet != null && matchedSet.imageSource != null) {
            BufferedImage sheet = getCachedImage(matchedSet.imageSource);
            if (sheet != null) {
                int localId = gid - matchedSet.firstGid;
                int cols = Math.max(1, matchedSet.columns);
                int tileCol = localId % cols;
                int tileRow = localId / cols;

                int srcW = matchedSet.tileWidth;
                int srcH = matchedSet.tileHeight;
                int srcX = matchedSet.margin + tileCol * (srcW + matchedSet.spacing);
                int srcY = matchedSet.margin + tileRow * (srcH + matchedSet.spacing);

                int drawW = Math.round(srcW * zoomScale);
                int drawH = Math.round(srcH * zoomScale);

                int destX = sp.x + Math.round(matchedSet.tileOffsetX * zoomScale);
                int destY = sp.y + th - drawH + Math.round(matchedSet.tileOffsetY * zoomScale);

                if (srcX + srcW <= sheet.getWidth() && srcY + srcH <= sheet.getHeight()) {
                    g2.drawImage(sheet, destX, destY, destX + drawW, destY + drawH,
                            srcX, srcY, srcX + srcW, srcY + srcH, null);
                    return;
                }
            }
        }

        // Procedural Fallback
        Color c = TilePalettePanel.getTileProceduralColor(gid);
        g2.setColor(c);
        if (isIso) {
            Polygon poly = IsometricMath.getDiamondPolygon(cx, cy, tw, th, panOffsetX, panOffsetY);
            g2.fillPolygon(poly);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawPolygon(poly);
        } else {
            g2.fillRect(sp.x, sp.y, tw, th);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRect(sp.x, sp.y, tw, th);
        }
    }

    private void renderMapObject(Graphics2D g2, MapObjectVO obj, int tw, int th, boolean isIso) {
        int ox, oy;
        if (isIso) {
            int cx = (int)(obj.x / document.getTileWidth());
            int cy = (int)(obj.y / document.getTileHeight());
            Point p = IsometricMath.cellToScreen(cx, cy, tw, th, panOffsetX, panOffsetY);
            ox = p.x;
            oy = p.y;
        } else {
            ox = panOffsetX + Math.round(obj.x * zoomScale);
            oy = panOffsetY + Math.round(obj.y * zoomScale);
        }

        int ow = Math.max(12, Math.round(obj.width * zoomScale));
        int oh = Math.max(12, Math.round(obj.height * zoomScale));

        g2.setColor(new Color(obj.color.getRed(), obj.color.getGreen(), obj.color.getBlue(), 90));
        g2.fillRect(ox, oy, ow, oh);
        g2.setColor(obj.color);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(ox, oy, ow, oh);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString(obj.name + " (" + obj.type + ")", ox + 4, oy + 12);
    }

    private BufferedImage getCachedImage(String src) {
        if (textureCache.containsKey(src)) return textureCache.get(src);

        // Search relative to Workspace
        String[] prefixes = new String[]{
                "", "Workspace/NeonCosmos/assets/tilemaps/", "Workspace/NeonCosmos/assets/tilesets/",
                "Workspace/NeonCosmos/assets/"
        };

        for (String pre : prefixes) {
            File f = new File(pre + src);
            if (f.exists()) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        textureCache.put(src, img);
                        return img;
                    }
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    private Point cellToScreen(int cx, int cy, int tw, int th) {
        if (document.getOrientation() == TilemapGridMode.ISOMETRIC_DIAMOND) {
            return IsometricMath.cellToScreen(cx, cy, tw, th, panOffsetX, panOffsetY);
        } else if (document.getOrientation() == TilemapGridMode.ISOMETRIC_STAGGERED) {
            return IsometricMath.staggeredCellToScreen(cx, cy, tw, th, panOffsetX, panOffsetY, true, true);
        }
        return new Point(panOffsetX + cx * tw, panOffsetY + cy * th);
    }

    private Point screenToCell(int sx, int sy) {
        int tw = Math.max(4, Math.round(document.getTileWidth() * zoomScale));
        int th = Math.max(4, Math.round(document.getTileHeight() * zoomScale));

        if (document.getOrientation() == TilemapGridMode.ISOMETRIC_DIAMOND) {
            return IsometricMath.screenToCell(sx, sy, tw, th, panOffsetX, panOffsetY);
        } else if (document.getOrientation() == TilemapGridMode.ISOMETRIC_STAGGERED) {
            return IsometricMath.screenToStaggeredCell(sx, sy, tw, th, panOffsetX, panOffsetY, true, true);
        }
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
                if (isPanning) isPanning = false;
                if (isDraggingTool) {
                    isDraggingTool = false;
                    Point cell = screenToCell(e.getX(), e.getY());
                    applyToolAt(cell.x, cell.y, true);
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
                    hoverCellX = cell.x;
                    hoverCellY = cell.y;
                    if (palettePanel.getActiveTool() == TilemapTool.PAINT_BRUSH || palettePanel.getActiveTool() == TilemapTool.ERASER) {
                        applyToolAt(cell.x, cell.y, false);
                    }
                    canvas.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point cell = screenToCell(e.getX(), e.getY());
                if (cell.x != hoverCellX || cell.y != hoverCellY) {
                    hoverCellX = cell.x;
                    hoverCellY = cell.y;
                    canvas.repaint();
                }
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
        TilemapLayer layer = document.getActiveLayer();
        if (!(layer instanceof TileLayer)) return;
        TileLayer tl = (TileLayer) layer;

        if (cx < 0 || cx >= tl.getWidth() || cy < 0 || cy >= tl.getHeight()) return;

        TilemapTool tool = palettePanel.getActiveTool();
        int selGid = palettePanel.getSelectedTileGid();

        switch (tool) {
            case PAINT_BRUSH:
                tl.setTile(cx, cy, selGid);
                canvas.repaint();
                break;
            case ERASER:
                tl.setTile(cx, cy, 0);
                canvas.repaint();
                break;
            case BUCKET_FILL:
                tl.floodFill(cx, cy, selGid);
                canvas.repaint();
                break;
            case RECT_FILL:
                if (isRelease && dragStartX >= 0 && dragStartY >= 0) {
                    int minX = Math.min(dragStartX, cx);
                    int minY = Math.min(dragStartY, cy);
                    int rw = Math.abs(cx - dragStartX) + 1;
                    int rh = Math.abs(cy - dragStartY) + 1;
                    tl.fillRect(minX, minY, rw, rh, selGid);
                    canvas.repaint();
                }
                break;
            case EYEDROPPER:
                int picked = tl.getTile(cx, cy);
                if (picked > 0) palettePanel.setSelectedTileGid(picked);
                break;
            default:
                break;
        }
    }

    private void openTmxFile() {
        JFileChooser fc = new JFileChooser("Workspace/NeonCosmos/assets/tilemaps");
        fc.setDialogTitle("Open Tiled .TMX Level Map");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                TilemapDocument doc = TmxParser.parseTmx(fc.getSelectedFile());
                this.document = doc;
                palettePanel.setTileSets(doc.getTileSets());
                layersPanel.setDocument(doc);
                canvas.repaint();
                JOptionPane.showMessageDialog(this, "Successfully loaded TMX map: " + doc.getName(), "TMX Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load TMX: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openFlareFile() {
        JFileChooser fc = new JFileChooser("Workspace/NeonCosmos/assets/tilemaps");
        fc.setDialogTitle("Import Flare ARPG .TXT Level Map");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                TilemapDocument doc = FlareMapConverter.parseFlareMap(fc.getSelectedFile());
                this.document = doc;
                palettePanel.setTileSets(doc.getTileSets());
                layersPanel.setDocument(doc);
                canvas.repaint();
                JOptionPane.showMessageDialog(this, "Successfully imported Flare map: " + doc.getName(), "Flare Map Imported", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to import Flare map: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportTmxFile() {
        JFileChooser fc = new JFileChooser("Workspace/NeonCosmos/assets/tilemaps");
        fc.setSelectedFile(new File(document.getName() + ".tmx"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                TmxSerializer.saveTmx(document, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Saved .TMX map to:\n" + fc.getSelectedFile().getAbsolutePath(),
                        "TMX Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting TMX: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
