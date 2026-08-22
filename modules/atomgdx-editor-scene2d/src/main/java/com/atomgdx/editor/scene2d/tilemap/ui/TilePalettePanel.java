package com.atomgdx.editor.scene2d.tilemap.ui;

import com.atomgdx.editor.scene2d.tilemap.data.AutoTileRule;
import com.atomgdx.editor.scene2d.tilemap.data.TileSetVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Unity-Style Tile Palette Panel featuring tileset grid picker, active brush tools,
 * random weighted pools, and auto-tiling rule selectors.
 */
public class TilePalettePanel extends JPanel {

    private final List<TileSetVO> tileSets = new ArrayList<>();
    private TileSetVO currentTileSet;
    private int selectedTileGid = 1;
    private TilemapTool activeTool = TilemapTool.PAINT_BRUSH;
    private AutoTileRule activeRule;

    private final JComboBox<String> tilesetCombo = new JComboBox<>();
    private final JComboBox<TilemapTool> toolCombo = new JComboBox<>(TilemapTool.values());
    private final JSlider zoomSlider = new JSlider(50, 200, 100);
    private final JLabel statusLabel = new JLabel("Tile GID: 1 (32x32)");
    private final JPanel gridCanvas;

    private Consumer<TilemapTool> toolChangeListener;
    private Consumer<Integer> tileSelectionListener;

    public TilePalettePanel() {
        gridCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(16, 17, 20));
                g2.fillRect(0, 0, w, h);

                if (currentTileSet == null) {
                    g2.setColor(Color.GRAY);
                    g2.drawString("No Tileset Loaded", 20, 30);
                    g2.dispose();
                    return;
                }

                float scale = zoomSlider.getValue() / 100f;
                int tw = Math.max(8, Math.round(currentTileSet.tileWidth * scale));
                int th = Math.max(8, Math.round(currentTileSet.tileHeight * scale));
                int cols = Math.max(1, w / tw);

                for (int i = 0; i < currentTileSet.tileCount; i++) {
                    int col = i % cols;
                    int row = i / cols;
                    int x = col * tw;
                    int y = row * th;
                    int gid = currentTileSet.firstGid + i;

                    // Procedural tile rendering simulation
                    Color c = getTileProceduralColor(gid);
                    g2.setColor(c);
                    g2.fillRect(x + 1, y + 1, tw - 2, th - 2);

                    // Grid line
                    g2.setColor(new Color(40, 43, 50));
                    g2.drawRect(x, y, tw, th);

                    // Selection highlight
                    if (gid == selectedTileGid) {
                        g2.setColor(new Color(0, 220, 255));
                        g2.setStroke(new BasicStroke(2.5f));
                        g2.drawRect(x + 1, y + 1, tw - 2, th - 2);
                    }
                }

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                if (currentTileSet == null) return new Dimension(220, 300);
                float scale = zoomSlider.getValue() / 100f;
                int th = Math.max(8, Math.round(currentTileSet.tileHeight * scale));
                int rows = (currentTileSet.tileCount / 6) + 2;
                return new Dimension(220, rows * th);
            }
        };

        gridCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTileSet == null) return;
                float scale = zoomSlider.getValue() / 100f;
                int tw = Math.max(8, Math.round(currentTileSet.tileWidth * scale));
                int th = Math.max(8, Math.round(currentTileSet.tileHeight * scale));
                int cols = Math.max(1, gridCanvas.getWidth() / tw);

                int col = e.getX() / tw;
                int row = e.getY() / th;
                int idx = row * cols + col;

                if (idx >= 0 && idx < currentTileSet.tileCount) {
                    selectedTileGid = currentTileSet.firstGid + idx;
                    statusLabel.setText("Tile GID: " + selectedTileGid + " (" + currentTileSet.tileWidth + "x" + currentTileSet.tileHeight + ")");
                    gridCanvas.repaint();
                    if (tileSelectionListener != null) {
                        tileSelectionListener.accept(selectedTileGid);
                    }
                }
            }
        });

        setLayout(new BorderLayout(4, 4));
        setBackground(new Color(24, 26, 30));
        setBorder(new EmptyBorder(6, 6, 6, 6));

        // Header controls
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        topPanel.setOpaque(false);

        // Tileset selector
        JPanel tsRow = new JPanel(new BorderLayout(4, 0));
        tsRow.setOpaque(false);
        tsRow.add(new JLabel("Tileset:"), BorderLayout.WEST);
        tilesetCombo.addActionListener(e -> {
            int idx = tilesetCombo.getSelectedIndex();
            if (idx >= 0 && idx < tileSets.size()) {
                currentTileSet = tileSets.get(idx);
                gridCanvas.repaint();
            }
        });
        tsRow.add(tilesetCombo, BorderLayout.CENTER);
        topPanel.add(tsRow);

        // Tool selector
        JPanel toolRow = new JPanel(new BorderLayout(4, 0));
        toolRow.setOpaque(false);
        toolRow.add(new JLabel("Tool:"), BorderLayout.WEST);
        toolCombo.addActionListener(e -> {
            activeTool = (TilemapTool) toolCombo.getSelectedItem();
            if (toolChangeListener != null) toolChangeListener.accept(activeTool);
        });
        toolRow.add(toolCombo, BorderLayout.CENTER);
        topPanel.add(toolRow);

        // Zoom & info
        JPanel zoomRow = new JPanel(new BorderLayout(4, 0));
        zoomRow.setOpaque(false);
        zoomRow.add(new JLabel("Zoom:"), BorderLayout.WEST);
        zoomSlider.setOpaque(false);
        zoomSlider.addChangeListener(e -> gridCanvas.repaint());
        zoomRow.add(zoomSlider, BorderLayout.CENTER);
        topPanel.add(zoomRow);

        add(topPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridCanvas);
        scroll.setBorder(createTitledBorder("Tile Palette Grid"));
        add(scroll, BorderLayout.CENTER);

        // Status bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0, 220, 255));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load standard default tileset
        TileSetVO defSet = new TileSetVO("SciFi Station", 1, "tilesets/scifi_station_tiles.png", 32, 32);
        defSet.tileCount = 64;
        addTileset(defSet);
    }

    
    public void setTileSets(List<TileSetVO> sets) {
        this.tileSets.clear();
        this.tilesetCombo.removeAllItems();
        this.currentTileSet = null;
        if (sets != null) {
            for (TileSetVO ts : sets) {
                addTileset(ts);
            }
        }
        if (!tileSets.isEmpty()) {
            currentTileSet = tileSets.get(0);
        }
        gridCanvas.repaint();
    }
public void addTileset(TileSetVO ts) {
        tileSets.add(ts);
        tilesetCombo.addItem(ts.name);
        if (currentTileSet == null) {
            currentTileSet = ts;
            gridCanvas.repaint();
        }
    }

    public int getSelectedTileGid() { return selectedTileGid; }
    public void setSelectedTileGid(int gid) {
        this.selectedTileGid = gid;
        statusLabel.setText("Tile GID: " + gid);
        gridCanvas.repaint();
    }

    public TilemapTool getActiveTool() { return activeTool; }
    public void setToolChangeListener(Consumer<TilemapTool> l) { this.toolChangeListener = l; }
    public void setTileSelectionListener(Consumer<Integer> l) { this.tileSelectionListener = l; }

    public static Color getTileProceduralColor(int gid) {
        int hash = (gid * 73856093) ^ 0x5bd1e995;
        int r = 40 + Math.abs((hash & 0xFF) % 180);
        int g = 40 + Math.abs(((hash >> 8) & 0xFF) % 180);
        int b = 50 + Math.abs(((hash >> 16) & 0xFF) % 180);
        return new Color(r, g, b);
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
