package com.atomgdx.editor.font.ui;

import com.atomgdx.editor.font.FontGeneratorEngine;
import com.atomgdx.editor.font.FontGeneratorSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Production UI for the Bitmap & MSDF Font Studio in AtomGDX.
 */
public class FontGeneratorPanel extends JPanel {

    private final FontGeneratorSettings settings;
    private final JTextField fontFileField = new JTextField();
    private final JTextField outputDirField = new JTextField();
    private final JTextField fontNameField = new JTextField("game_font");
    private final JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(32, 8, 256, 2));
    private final JSpinner paddingSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 32, 1));
    private final JSpinner spreadSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 32, 1));
    private final JSpinner outlineSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 16, 1));
    private final JCheckBox shadowCheckBox = new JCheckBox("Drop Shadow", false);
    private final JSpinner shadowOffsetXSpinner = new JSpinner(new SpinnerNumberModel(2, -20, 20, 1));
    private final JSpinner shadowOffsetYSpinner = new JSpinner(new SpinnerNumberModel(2, -20, 20, 1));

    private final JComboBox<FontGeneratorSettings.FontType> typeCombo = new JComboBox<>(FontGeneratorSettings.FontType.values());
    private final JComboBox<Integer> pageSizeCombo = new JComboBox<>(new Integer[]{256, 512, 1024, 2048});
    private final JComboBox<String> charsetPresetCombo = new JComboBox<>(new String[]{
            "ASCII Full (95 chars)",
            "Alphanumeric (62 chars)",
            "Numbers & Symbols (28 chars)",
            "Latin Extended (160+ chars)",
            "Custom"
    });

    private final JTextArea charsetArea = new JTextArea();
    private final JTextField sampleTextField = new JTextField("The Quick Brown Fox Jumps Over The Lazy Dog! 1234567890");
    private final JSlider previewScaleSlider = new JSlider(50, 300, 100);

    // Colors
    private Color fontColor = Color.WHITE;
    private Color outlineColor = Color.BLACK;
    private Color shadowColor = new Color(0, 0, 0, 140);
    private final JButton fontColorBtn = new JButton();
    private final JButton outlineColorBtn = new JButton();

    // Previews
    private final JPanel textPreviewCanvas;
    private final JPanel atlasPreviewCanvas;
    private BufferedImage currentAtlasImage;
    private final JLabel atlasStatsLabel = new JLabel("Atlas: 512x512 | Glyphs: 95");

    public FontGeneratorPanel() {
        this(new FontGeneratorSettings());
    }

    public FontGeneratorPanel(FontGeneratorSettings settings) {
        this.settings = settings != null ? settings : new FontGeneratorSettings();
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(22, 23, 26));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header Toolbar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 32, 36));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Bitmap & MSDF Font Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        header.add(title, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerActions.setOpaque(false);

        JButton exportShaderBtn = new JButton("Export SDF Shaders");
        exportShaderBtn.addActionListener(e -> exportShaders());
        headerActions.add(exportShaderBtn);

        JButton generateBtn = new JButton("Generate .fnt & Atlas");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        generateBtn.setBackground(new Color(44, 93, 212));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.addActionListener(e -> generateFont());
        headerActions.add(generateBtn);

        header.add(headerActions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center SplitPane: Left Parameters, Right Previews & Code Snippets
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setBackground(new Color(22, 23, 26));
        splitPane.setBorder(null);

        // Left Container (Scrollable parameters)
        JPanel leftContainer = new JPanel(new GridBagLayout());
        leftContainer.setBackground(new Color(30, 32, 36));
        leftContainer.setBorder(createTitledBorder("Font Parameters"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        // TTF File
        JButton browseFontBtn = new JButton("Browse...");
        browseFontBtn.addActionListener(e -> chooseFile(fontFileField, false));
        leftContainer.add(new JLabel("Font File (.ttf/.otf):"), setGbc(gbc, 0, 0, 1));
        leftContainer.add(fontFileField, setGbc(gbc, 1, 0, 1));
        leftContainer.add(browseFontBtn, setGbc(gbc, 2, 0, 1));

        // Output Dir
        JButton browseOutBtn = new JButton("Browse...");
        browseOutBtn.addActionListener(e -> chooseFile(outputDirField, true));
        leftContainer.add(new JLabel("Output Folder:"), setGbc(gbc, 0, 1, 1));
        leftContainer.add(outputDirField, setGbc(gbc, 1, 1, 1));
        leftContainer.add(browseOutBtn, setGbc(gbc, 2, 1, 1));

        // Base Name
        leftContainer.add(new JLabel("Base Name:"), setGbc(gbc, 0, 2, 1));
        leftContainer.add(fontNameField, setGbc(gbc, 1, 2, 2));

        // Font Type & Size
        leftContainer.add(new JLabel("Rendering Mode:"), setGbc(gbc, 0, 3, 1));
        typeCombo.setSelectedItem(this.settings.getFontType());
        typeCombo.addActionListener(e -> updateLiveAtlas());
        leftContainer.add(typeCombo, setGbc(gbc, 1, 3, 2));

        leftContainer.add(new JLabel("Font Size (px):"), setGbc(gbc, 0, 4, 1));
        fontSizeSpinner.setValue(this.settings.getFontSize());
        fontSizeSpinner.addChangeListener(e -> updateLiveAtlas());
        leftContainer.add(fontSizeSpinner, setGbc(gbc, 1, 4, 2));

        leftContainer.add(new JLabel("Atlas Page Size:"), setGbc(gbc, 0, 5, 1));
        pageSizeCombo.setSelectedItem(512);
        pageSizeCombo.addActionListener(e -> updateLiveAtlas());
        leftContainer.add(pageSizeCombo, setGbc(gbc, 1, 5, 2));

        leftContainer.add(new JLabel("Padding (px):"), setGbc(gbc, 0, 6, 1));
        paddingSpinner.setValue(this.settings.getPadding());
        paddingSpinner.addChangeListener(e -> updateLiveAtlas());
        leftContainer.add(paddingSpinner, setGbc(gbc, 1, 6, 2));

        leftContainer.add(new JLabel("SDF Spread:"), setGbc(gbc, 0, 7, 1));
        spreadSpinner.setValue(this.settings.getSpread());
        spreadSpinner.addChangeListener(e -> updateLiveAtlas());
        leftContainer.add(spreadSpinner, setGbc(gbc, 1, 7, 2));

        leftContainer.add(new JLabel("Outline Width:"), setGbc(gbc, 0, 8, 1));
        outlineSpinner.setValue(this.settings.getOutlineWidth());
        outlineSpinner.addChangeListener(e -> updateLiveAtlas());
        leftContainer.add(outlineSpinner, setGbc(gbc, 1, 8, 2));

        // Colors
        setupColorButton(fontColorBtn, fontColor, c -> { fontColor = c; updateLiveAtlas(); });
        setupColorButton(outlineColorBtn, outlineColor, c -> { outlineColor = c; updateLiveAtlas(); });

        leftContainer.add(new JLabel("Font Color:"), setGbc(gbc, 0, 9, 1));
        leftContainer.add(fontColorBtn, setGbc(gbc, 1, 9, 2));

        leftContainer.add(new JLabel("Outline Color:"), setGbc(gbc, 0, 10, 1));
        leftContainer.add(outlineColorBtn, setGbc(gbc, 1, 10, 2));

        // Shadow controls
        shadowCheckBox.addActionListener(e -> updateLiveAtlas());
        leftContainer.add(shadowCheckBox, setGbc(gbc, 0, 11, 1));
        JPanel shadowOffsets = new JPanel(new GridLayout(1, 2, 4, 4));
        shadowOffsets.setOpaque(false);
        shadowOffsets.add(shadowOffsetXSpinner);
        shadowOffsets.add(shadowOffsetYSpinner);
        leftContainer.add(shadowOffsets, setGbc(gbc, 1, 11, 2));

        // Charset Preset
        leftContainer.add(new JLabel("Charset Preset:"), setGbc(gbc, 0, 12, 1));
        charsetPresetCombo.addActionListener(e -> applyCharsetPreset());
        leftContainer.add(charsetPresetCombo, setGbc(gbc, 1, 12, 2));

        // Charset textarea
        charsetArea.setText(this.settings.getCharacterSet());
        charsetArea.setLineWrap(true);
        charsetArea.setBackground(new Color(18, 19, 22));
        charsetArea.setForeground(Color.WHITE);
        charsetArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        JScrollPane charsetScroll = new JScrollPane(charsetArea);
        charsetScroll.setPreferredSize(new Dimension(340, 100));
        charsetScroll.setBorder(createTitledBorder("Character Set Glyphs"));
        leftContainer.add(charsetScroll, setGbc(gbc, 0, 13, 3));

        JScrollPane leftScroll = new JScrollPane(leftContainer);
        leftScroll.setBorder(null);
        splitPane.setLeftComponent(leftScroll);

        // Right Tabs (Live Text Preview, Atlas Sheet Preview, Code Snippets)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(30, 32, 36));

        // Tab 1: Live Text Sample
        JPanel tab1 = new JPanel(new BorderLayout(6, 6));
        tab1.setBackground(new Color(22, 23, 26));
        tab1.setBorder(new EmptyBorder(6, 6, 6, 6));

        textPreviewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark checkerboard background
                g2.setColor(new Color(16, 17, 20));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(24, 25, 28));
                for (int y = 0; y < h; y += 16) {
                    for (int x = 0; x < w; x += 16) {
                        if (((x / 16) + (y / 16)) % 2 == 0) g2.fillRect(x, y, 16, 16);
                    }
                }

                float scale = previewScaleSlider.getValue() / 100f;
                int size = (Integer) fontSizeSpinner.getValue();
                Font f = FontGeneratorEngine.loadFont(syncSettingsFromUi()).deriveFont(Font.BOLD, size * scale);
                g2.setFont(f);

                FontMetrics fm = g2.getFontMetrics();
                String text = sampleTextField.getText();
                int textX = 20;
                int textY = h / 2 + fm.getAscent() / 2 - 10;

                // Render shadow if enabled
                if (shadowCheckBox.isSelected()) {
                    g2.setColor(shadowColor);
                    g2.drawString(text, textX + (Integer) shadowOffsetXSpinner.getValue(), textY + (Integer) shadowOffsetYSpinner.getValue());
                }

                // Render outline
                int ow = (Integer) outlineSpinner.getValue();
                if (ow > 0) {
                    g2.setColor(outlineColor);
                    for (int dx = -ow; dx <= ow; dx++) {
                        for (int dy = -ow; dy <= ow; dy++) {
                            if (dx != 0 || dy != 0) g2.drawString(text, textX + dx, textY + dy);
                        }
                    }
                }

                // Primary text
                g2.setColor(fontColor);
                g2.drawString(text, textX, textY);

                g2.dispose();
            }
        };

        JPanel sampleControl = new JPanel(new BorderLayout(6, 0));
        sampleControl.setBackground(new Color(30, 32, 36));
        sampleControl.setBorder(new EmptyBorder(4, 6, 4, 6));
        sampleControl.add(new JLabel("Sample Text:"), BorderLayout.WEST);
        sampleControl.add(sampleTextField, BorderLayout.CENTER);
        sampleTextField.addActionListener(e -> textPreviewCanvas.repaint());

        JPanel scaleControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        scaleControl.setOpaque(false);
        scaleControl.add(new JLabel("Scale:"));
        previewScaleSlider.setPreferredSize(new Dimension(100, 20));
        previewScaleSlider.addChangeListener(e -> textPreviewCanvas.repaint());
        scaleControl.add(previewScaleSlider);
        sampleControl.add(scaleControl, BorderLayout.EAST);
        tab1.add(sampleControl, BorderLayout.NORTH);
        tab1.add(textPreviewCanvas, BorderLayout.CENTER);
        tabbedPane.addTab("Live Text Preview", tab1);

        // Tab 2: Atlas Sheet Preview
        JPanel tab2 = new JPanel(new BorderLayout(6, 6));
        tab2.setBackground(new Color(22, 23, 26));
        tab2.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel atlasInfoBar = new JPanel(new BorderLayout());
        atlasInfoBar.setBackground(new Color(30, 32, 36));
        atlasInfoBar.setBorder(new EmptyBorder(4, 6, 4, 6));
        atlasInfoBar.add(atlasStatsLabel, BorderLayout.WEST);

        JButton refreshAtlasBtn = new JButton("Refresh Atlas");
        refreshAtlasBtn.addActionListener(e -> updateLiveAtlas());
        atlasInfoBar.add(refreshAtlasBtn, BorderLayout.EAST);
        tab2.add(atlasInfoBar, BorderLayout.NORTH);

        atlasPreviewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(12, 13, 15));
                g2.fillRect(0, 0, w, h);

                if (currentAtlasImage != null) {
                    int imgW = currentAtlasImage.getWidth();
                    int imgH = currentAtlasImage.getHeight();
                    float scale = Math.min((float) (w - 20) / imgW, (float) (h - 20) / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int dx = (w - drawW) / 2;
                    int dy = (h - drawH) / 2;

                    g2.drawImage(currentAtlasImage, dx, dy, drawW, drawH, null);
                    g2.setColor(new Color(0, 220, 255, 120));
                    g2.drawRect(dx, dy, drawW, drawH);
                }

                g2.dispose();
            }
        };
        tab2.add(atlasPreviewCanvas, BorderLayout.CENTER);
        tabbedPane.addTab("Packed Atlas Texture", tab2);

        // Tab 3: Code & Shader Snippets
        JPanel tab3 = new JPanel(new BorderLayout(6, 6));
        tab3.setBackground(new Color(22, 23, 26));
        tab3.setBorder(new EmptyBorder(6, 6, 6, 6));

        JTextArea snippetArea = new JTextArea();
        snippetArea.setEditable(false);
        snippetArea.setBackground(new Color(16, 17, 20));
        snippetArea.setForeground(new Color(0, 255, 200));
        snippetArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        snippetArea.setText(
                "// 1. Loading standard BitmapFont in LibGDX:\n" +
                "BitmapFont font = new BitmapFont(Gdx.files.internal(\"fonts/game_font.fnt\"));\n\n" +
                "// 2. Rendering Distance Field (SDF / MSDF) with ShaderProgram in LibGDX:\n" +
                "ShaderProgram fontShader = new ShaderProgram(\n" +
                "    Gdx.files.internal(\"shaders/sdf_font.vert\"),\n" +
                "    Gdx.files.internal(\"shaders/sdf_font.frag\")\n" +
                ");\n" +
                "batch.setShader(fontShader);\n" +
                "font.draw(batch, \"Hello LibGDX!\", 100, 200);\n" +
                "batch.setShader(null);\n"
        );
        tab3.add(new JScrollPane(snippetArea), BorderLayout.CENTER);
        tabbedPane.addTab("LibGDX Integration Code", tab3);

        splitPane.setRightComponent(tabbedPane);
        add(splitPane, BorderLayout.CENTER);

        updateLiveAtlas();
    }

    private void setupColorButton(JButton btn, Color initColor, java.util.function.Consumer<Color> onColorPicked) {
        btn.setBackground(initColor);
        btn.setPreferredSize(new Dimension(50, 22));
        btn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Select Color", btn.getBackground());
            if (chosen != null) {
                btn.setBackground(chosen);
                onColorPicked.accept(chosen);
            }
        });
    }

    private void applyCharsetPreset() {
        int idx = charsetPresetCombo.getSelectedIndex();
        if (idx == 0) charsetArea.setText(FontGeneratorSettings.CHARSET_ASCII_FULL);
        else if (idx == 1) charsetArea.setText(FontGeneratorSettings.CHARSET_ALPHANUMERIC);
        else if (idx == 2) charsetArea.setText(FontGeneratorSettings.CHARSET_NUMBERS_SYMBOLS);
        else if (idx == 3) charsetArea.setText(FontGeneratorSettings.CHARSET_LATIN_EXTENDED);
        updateLiveAtlas();
    }

    private FontGeneratorSettings syncSettingsFromUi() {
        settings.setFontFilePath(fontFileField.getText().trim());
        settings.setFontSize((Integer) fontSizeSpinner.getValue());
        settings.setPadding((Integer) paddingSpinner.getValue());
        settings.setSpread((Integer) spreadSpinner.getValue());
        settings.setOutlineWidth((Integer) outlineSpinner.getValue());
        settings.setFontColor(fontColor.getRGB());
        settings.setOutlineColor(outlineColor.getRGB());
        settings.setIncludeShadow(shadowCheckBox.isSelected());
        settings.setShadowOffsetX((Integer) shadowOffsetXSpinner.getValue());
        settings.setShadowOffsetY((Integer) shadowOffsetYSpinner.getValue());
        settings.setFontType((FontGeneratorSettings.FontType) typeCombo.getSelectedItem());
        int pageSize = (Integer) pageSizeCombo.getSelectedItem();
        settings.setPageWidth(pageSize);
        settings.setPageHeight(pageSize);
        settings.setCharacterSet(charsetArea.getText());
        return settings;
    }

    private void updateLiveAtlas() {
        FontGeneratorSettings s = syncSettingsFromUi();
        currentAtlasImage = FontGeneratorEngine.renderAtlas(s);
        atlasStatsLabel.setText("Atlas: " + s.getPageWidth() + "x" + s.getPageHeight() + " | Glyphs: " + s.getCharacterSet().length() + " | Mode: " + s.getFontType());
        if (textPreviewCanvas != null) textPreviewCanvas.repaint();
        if (atlasPreviewCanvas != null) atlasPreviewCanvas.repaint();
    }

    private GridBagConstraints setGbc(GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = width > 1 ? 1.0 : 0.0;
        return gbc;
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

    private void chooseFile(JTextField target, boolean isDir) {
        JFileChooser chooser = new JFileChooser();
        if (isDir) chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().getAbsolutePath());
            if (!isDir) updateLiveAtlas();
        }
    }

    private void exportShaders() {
        String outPath = outputDirField.getText().trim();
        String baseName = fontNameField.getText().trim();
        if (outPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an output folder first.", "Missing Folder", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            FontGeneratorEngine.generateSdfShaderFiles(new File(outPath), baseName + "_sdf");
            JOptionPane.showMessageDialog(this, "SDF Shaders exported successfully (.vert & .frag) to:\n" + outPath, "Export SDF Shaders", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export shaders: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateFont() {
        String outPath = outputDirField.getText().trim();
        String fontName = fontNameField.getText().trim();

        if (outPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an output folder.", "Missing Folder", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File outDir = new File(outPath);
        FontGeneratorSettings s = syncSettingsFromUi();

        try {
            FontGeneratorEngine.FontResult result = FontGeneratorEngine.generateFont(s, outDir, fontName);
            updateLiveAtlas();
            JOptionPane.showMessageDialog(this,
                    "Font generated successfully!\n\n" +
                    "Descriptor: " + result.fntFile.getAbsolutePath() + "\n" +
                    "Atlas PNG: " + result.pngFile.getAbsolutePath() + "\n" +
                    "Packed Glyphs: " + result.glyphs.size(),
                    "Font Generator",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate font: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
