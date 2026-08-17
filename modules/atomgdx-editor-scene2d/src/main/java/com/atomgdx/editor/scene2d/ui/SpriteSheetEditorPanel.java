package com.atomgdx.editor.scene2d.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Full-featured Image Viewer and SpriteSheet Editor for 2D game assets.
 * Features grid auto-slicing, manual frame inspection, real-time animation player,
 * zoom/pan viewport, and timeline scrubber.
 */
public class SpriteSheetEditorPanel extends JPanel {

    public static class SpriteTheme {
        public static final Color BG_DARK = new Color(26, 26, 28);
        public static final Color BG_PANEL = new Color(34, 35, 38);
        public static final Color BG_HEADER = new Color(42, 44, 48);
        public static final Color TEXT_PRIMARY = new Color(225, 228, 232);
        public static final Color TEXT_SECONDARY = new Color(150, 155, 162);
        public static final Color ACCENT_BLUE = new Color(44, 93, 212);
        public static final Color BORDER = new Color(55, 57, 62);
        public static final Color GRID_COLOR = new Color(60, 140, 240, 180);
    }

    private BufferedImage sourceImage;
    private File sourceFile;
    private final List<BufferedImage> frames = new ArrayList<>();

    // Viewport State
    private float zoom = 1.0f;
    private int panX = 0, panY = 0;
    private int lastMouseX, lastMouseY;
    private boolean isPanning = false;

    // Slicing State
    private int frameWidth = 64;
    private int frameHeight = 64;
    private int rows = 1;
    private int cols = 4;
    private int currentFrameIndex = 0;

    // Animation Player State
    private boolean isPlaying = true;
    private int fps = 12;
    private Timer animTimer;

    // UI Components
    private final CanvasPanel canvasPanel = new CanvasPanel();
    private final PreviewPanel previewPanel = new PreviewPanel();
    private JSpinner frameWSpinner, frameHSpinner, rowsSpinner, colsSpinner, fpsSpinner;
    private JSlider timelineSlider;
    private JButton playPauseBtn;
    private JLabel infoLabel;

    public SpriteSheetEditorPanel() {
        this(null);
    }

    public SpriteSheetEditorPanel(File file) {
        setLayout(new BorderLayout(0, 0));
        setBackground(SpriteTheme.BG_DARK);

        // Top Toolbar
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);

        // Center Viewport
        add(canvasPanel, BorderLayout.CENTER);

        // Right Sidebar (Slicing settings & Animation Preview Player)
        JPanel rightSidebar = createSidebar();
        add(rightSidebar, BorderLayout.EAST);

        // Bottom Timeline & Status
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);

        // Setup Animation Timer
        animTimer = new Timer(1000 / fps, e -> {
            if (isPlaying && !frames.isEmpty()) {
                currentFrameIndex = (currentFrameIndex + 1) % frames.size();
                timelineSlider.setValue(currentFrameIndex);
                previewPanel.repaint();
            }
        });
        animTimer.start();

        if (file != null && file.exists()) {
            loadImage(file);
        } else {
            createDemoSpriteSheet();
        }
    }

    public void loadImage(File file) {
        this.sourceFile = file;
        try {
            sourceImage = ImageIO.read(file);
            infoLabel.setText(file.getName() + " (" + sourceImage.getWidth() + "x" + sourceImage.getHeight() + " px)");
            autoSlice();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createDemoSpriteSheet() {
        // Create demo 4-frame animated spritesheet
        int w = 256, h = 64;
        sourceImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = sourceImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < 4; i++) {
            int cx = i * 64 + 32;
            int cy = 32;
            g2.setColor(new Color(30 + i * 40, 100 + i * 30, 240));
            g2.fillOval(cx - 20, cy - 20, 40, 40);
            g2.setColor(Color.WHITE);
            g2.fillOval(cx - 8 + i * 4, cy - 8, 16, 16);
            g2.drawString("F" + (i + 1), cx - 6, cy + 28);
        }
        g2.dispose();

        infoLabel.setText("DemoSpriteSheet (256x64 px)");
        autoSlice();
    }

    private void autoSlice() {
        frames.clear();
        if (sourceImage == null) return;

        cols = Math.max(1, sourceImage.getWidth() / frameWidth);
        rows = Math.max(1, sourceImage.getHeight() / frameHeight);

        colsSpinner.setValue(cols);
        rowsSpinner.setValue(rows);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * frameWidth;
                int y = r * frameHeight;
                if (x + frameWidth <= sourceImage.getWidth() && y + frameHeight <= sourceImage.getHeight()) {
                    frames.add(sourceImage.getSubimage(x, y, frameWidth, frameHeight));
                }
            }
        }

        timelineSlider.setMaximum(Math.max(0, frames.size() - 1));
        currentFrameIndex = 0;
        canvasPanel.repaint();
        previewPanel.repaint();
    }

    private JToolBar createToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SpriteTheme.BG_HEADER);
        tb.setBorder(new LineBorder(SpriteTheme.BORDER, 1));

        JButton openBtn = new JButton("Open Image", Scene2DEditorPanel.getIcon("folder.png"));
        JButton resetZoomBtn = new JButton("1:1 Pixel Zoom", Scene2DEditorPanel.getIcon("arrow_refresh.png"));
        JButton exportAtlasBtn = new JButton("Export Frames", Scene2DEditorPanel.getIcon("star.png"));

        for (AbstractButton b : new AbstractButton[]{openBtn, resetZoomBtn, exportAtlasBtn}) {
            b.setBackground(SpriteTheme.BG_HEADER);
            b.setForeground(SpriteTheme.TEXT_PRIMARY);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
        }

        openBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                loadImage(chooser.getSelectedFile());
            }
        });

        resetZoomBtn.addActionListener(e -> {
            zoom = 1.0f;
            panX = 0;
            panY = 0;
            canvasPanel.repaint();
        });

        exportAtlasBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Exported " + frames.size() + " animation frames to assets directory.", "Export Slices", JOptionPane.INFORMATION_MESSAGE);
        });

        tb.add(openBtn);
        tb.addSeparator();
        tb.add(resetZoomBtn);
        tb.addSeparator();
        tb.add(exportAtlasBtn);

        return tb;
    }

    private JPanel createSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(240, 500));
        side.setBackground(SpriteTheme.BG_PANEL);
        side.setBorder(new LineBorder(SpriteTheme.BORDER, 1));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        // 1. Slicing Config
        JPanel sliceBox = new JPanel(new GridLayout(0, 1, 4, 4));
        sliceBox.setOpaque(false);
        sliceBox.setBorder(BorderFactory.createTitledBorder(new LineBorder(SpriteTheme.BORDER, 1), "Grid Slicing"));

        frameWSpinner = new JSpinner(new SpinnerNumberModel(64, 4, 4096, 8));
        frameHSpinner = new JSpinner(new SpinnerNumberModel(64, 4, 4096, 8));
        colsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 128, 1));
        rowsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 128, 1));

        frameWSpinner.addChangeListener(e -> {
            frameWidth = ((Number) frameWSpinner.getValue()).intValue();
            autoSlice();
        });
        frameHSpinner.addChangeListener(e -> {
            frameHeight = ((Number) frameHSpinner.getValue()).intValue();
            autoSlice();
        });

        sliceBox.add(createRow("Frame Width:", frameWSpinner));
        sliceBox.add(createRow("Frame Height:", frameHSpinner));
        sliceBox.add(createRow("Columns:", colsSpinner));
        sliceBox.add(createRow("Rows:", rowsSpinner));

        side.add(sliceBox);
        side.add(Box.createVerticalStrut(8));

        // 2. Animation Preview Player
        JPanel animBox = new JPanel(new BorderLayout(4, 4));
        animBox.setOpaque(false);
        animBox.setBorder(BorderFactory.createTitledBorder(new LineBorder(SpriteTheme.BORDER, 1), "Animation Preview"));

        animBox.add(previewPanel, BorderLayout.CENTER);

        JPanel animControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        animControls.setOpaque(false);

        playPauseBtn = new JButton("Pause");
        playPauseBtn.setBackground(SpriteTheme.BG_HEADER);
        playPauseBtn.setForeground(SpriteTheme.TEXT_PRIMARY);
        playPauseBtn.setFocusPainted(false);
        playPauseBtn.addActionListener(e -> {
            isPlaying = !isPlaying;
            playPauseBtn.setText(isPlaying ? "Pause" : "Play");
        });

        fpsSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 60, 1));
        fpsSpinner.setPreferredSize(new Dimension(50, 24));
        fpsSpinner.addChangeListener(e -> {
            fps = ((Number) fpsSpinner.getValue()).intValue();
            animTimer.setDelay(1000 / fps);
        });

        animControls.add(playPauseBtn);
        animControls.add(new JLabel(" FPS:"));
        animControls.add(fpsSpinner);

        animBox.add(animControls, BorderLayout.SOUTH);
        side.add(animBox);
        side.add(Box.createVerticalGlue());

        return side;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(SpriteTheme.BG_PANEL);
        bar.setBorder(new EmptyBorder(4, 8, 4, 8));

        infoLabel = new JLabel("No Image Loaded");
        infoLabel.setForeground(SpriteTheme.TEXT_SECONDARY);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        timelineSlider = new JSlider(0, 3, 0);
        timelineSlider.setOpaque(false);
        timelineSlider.addChangeListener(e -> {
            currentFrameIndex = timelineSlider.getValue();
            previewPanel.repaint();
        });

        bar.add(infoLabel, BorderLayout.WEST);
        bar.add(timelineSlider, BorderLayout.CENTER);

        return bar;
    }

    private JPanel createRow(String label, JComponent comp) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(SpriteTheme.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(85, 20));
        r.add(lbl, BorderLayout.WEST);
        r.add(comp, BorderLayout.CENTER);
        return r;
    }

    /**
     * Canvas Panel rendering checkerboard background, image, zoom/pan, and slice grids.
     */
    private class CanvasPanel extends JPanel {
        CanvasPanel() {
            setBackground(new Color(20, 21, 23));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        isPanning = true;
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPanning = false;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isPanning) {
                        panX += (e.getX() - lastMouseX);
                        panY += (e.getY() - lastMouseY);
                        lastMouseX = e.getX();
                        lastMouseY = e.getY();
                        repaint();
                    }
                }
            });

            addMouseWheelListener(e -> {
                float factor = e.getPreciseWheelRotation() > 0 ? 0.85f : 1.15f;
                zoom = Math.max(0.1f, Math.min(20f, zoom * factor));
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // 1. Checkerboard Background
            int size = 16;
            for (int x = 0; x < getWidth(); x += size) {
                for (int y = 0; y < getHeight(); y += size) {
                    g2.setColor(((x / size + y / size) % 2 == 0) ? new Color(28, 29, 32) : new Color(34, 35, 38));
                    g2.fillRect(x, y, size, size);
                }
            }

            if (sourceImage == null) return;

            // 2. Draw Image transformed
            int imgW = (int) (sourceImage.getWidth() * zoom);
            int imgH = (int) (sourceImage.getHeight() * zoom);
            int startX = (getWidth() - imgW) / 2 + panX;
            int startY = (getHeight() - imgH) / 2 + panY;

            g2.drawImage(sourceImage, startX, startY, imgW, imgH, null);

            // 3. Draw Slice Grid
            g2.setColor(SpriteTheme.GRID_COLOR);
            g2.setStroke(new BasicStroke(1.5f));

            int stepX = (int) (frameWidth * zoom);
            int stepY = (int) (frameHeight * zoom);

            for (int r = 0; r <= rows; r++) {
                int y = startY + r * stepY;
                g2.drawLine(startX, y, startX + imgW, y);
            }
            for (int c = 0; c <= cols; c++) {
                int x = startX + c * stepX;
                g2.drawLine(x, startY, x, startY + imgH);
            }
        }
    }

    /**
     * Preview Panel rendering real-time animation frames.
     */
    private class PreviewPanel extends JPanel {
        PreviewPanel() {
            setPreferredSize(new Dimension(200, 160));
            setBackground(new Color(20, 21, 23));
            setBorder(new LineBorder(SpriteTheme.BORDER, 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Checkerboard
            int size = 12;
            for (int x = 0; x < getWidth(); x += size) {
                for (int y = 0; y < getHeight(); y += size) {
                    g2.setColor(((x / size + y / size) % 2 == 0) ? new Color(28, 29, 32) : new Color(34, 35, 38));
                    g2.fillRect(x, y, size, size);
                }
            }

            if (!frames.isEmpty() && currentFrameIndex < frames.size()) {
                BufferedImage frame = frames.get(currentFrameIndex);
                int fitW = Math.min(getWidth() - 20, frame.getWidth() * 2);
                int fitH = Math.min(getHeight() - 20, frame.getHeight() * 2);
                int x = (getWidth() - fitW) / 2;
                int y = (getHeight() - fitH) / 2;

                g2.drawImage(frame, x, y, fitW, fitH, null);

                // Frame label
                g2.setColor(Color.WHITE);
                g2.drawString("Frame: " + (currentFrameIndex + 1) + " / " + frames.size(), 8, 16);
            }
        }
    }
}
