package com.atomgdx.tools.texturepacker.ui;

import com.atomgdx.core.SciFiColors;
import com.atomgdx.tools.texturepacker.TexturePackerEngine;
import com.atomgdx.tools.texturepacker.TexturePackerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

/**
 * Visual TexturePacker Studio panel for batch packing sprites into LibGDX Texture Atlases.
 */
public class TexturePackerPanel extends JPanel {

    private final TexturePackerSettings settings;
    private final JTextField inputDirField = new JTextField();
    private final JTextField outputDirField = new JTextField();
    private final JTextField atlasNameField = new JTextField("textures.atlas");
    private final JSpinner maxWidthSpinner = new JSpinner(new SpinnerNumberModel(2048, 64, 8192, 64));
    private final JSpinner maxHeightSpinner = new JSpinner(new SpinnerNumberModel(2048, 64, 8192, 64));
    private final JSpinner paddingSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 32, 1));
    private final JCheckBox stripWhitespaceBox = new JCheckBox("Strip Whitespace", true);
    private final JCheckBox duplicatePaddingBox = new JCheckBox("Duplicate Edge Padding", false);
    private final JCheckBox rotationBox = new JCheckBox("Allow Rotation", false);
    private final JComboBox<String> formatCombo = new JComboBox<>(new String[]{"RGBA8888", "RGB888", "RGBA4444"});
    private final JTextArea logArea = new JTextArea();

    public TexturePackerPanel() {
        this(new TexturePackerSettings());
    }

    public TexturePackerPanel(TexturePackerSettings settings) {
        this.settings = settings != null ? settings : new TexturePackerSettings();
        setLayout(new BorderLayout(8, 8));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SciFiColors.BG_PANEL);
        header.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel title = new JLabel("TexturePacker Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(SciFiColors.ACCENT_CYAN);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Center Config Panel
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(SciFiColors.BG_DARKEST);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        // Directories box
        JPanel dirBox = new JPanel(new GridLayout(3, 3, 6, 6));
        dirBox.setBackground(SciFiColors.BG_PANEL);
        dirBox.setBorder(createTitledBorder("Directories & Atlas Target"));

        JButton chooseInBtn = new JButton("Browse...");
        chooseInBtn.addActionListener(e -> chooseDirectory(inputDirField));

        JButton chooseOutBtn = new JButton("Browse...");
        chooseOutBtn.addActionListener(e -> chooseDirectory(outputDirField));

        dirBox.add(new JLabel("Input Sprites Dir:"));
        dirBox.add(inputDirField);
        dirBox.add(chooseInBtn);

        dirBox.add(new JLabel("Output Atlas Dir:"));
        dirBox.add(outputDirField);
        dirBox.add(chooseOutBtn);

        dirBox.add(new JLabel("Atlas File Name:"));
        dirBox.add(atlasNameField);
        dirBox.add(new JLabel(".atlas"));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        center.add(dirBox, gbc);

        // Settings Box
        JPanel optsBox = new JPanel(new GridLayout(4, 2, 8, 6));
        optsBox.setBackground(SciFiColors.BG_PANEL);
        optsBox.setBorder(createTitledBorder("Page & Packing Options"));

        optsBox.add(new JLabel("Max Page Width:"));
        optsBox.add(maxWidthSpinner);
        optsBox.add(new JLabel("Max Page Height:"));
        optsBox.add(maxHeightSpinner);
        optsBox.add(new JLabel("Padding (px):"));
        optsBox.add(paddingSpinner);
        optsBox.add(new JLabel("Pixel Format:"));
        optsBox.add(formatCombo);

        gbc.gridy = 1;
        center.add(optsBox, gbc);

        // Flags Box
        JPanel flagsBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        flagsBox.setBackground(SciFiColors.BG_PANEL);
        flagsBox.setBorder(createTitledBorder("Optimization Flags"));
        flagsBox.add(stripWhitespaceBox);
        flagsBox.add(duplicatePaddingBox);
        flagsBox.add(rotationBox);

        gbc.gridy = 2;
        center.add(flagsBox, gbc);

        // Action Button
        JButton packBtn = new JButton("Pack Texture Atlas");
        packBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        packBtn.setBackground(SciFiColors.ACCENT_BLUE);
        packBtn.setForeground(Color.WHITE);
        packBtn.addActionListener(e -> runPacker());

        gbc.gridy = 3;
        center.add(packBtn, gbc);

        // Log Console
        logArea.setEditable(false);
        logArea.setBackground(SciFiColors.BG_DARK);
        logArea.setForeground(SciFiColors.TEXT_PRIMARY);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setText("Ready to pack texture atlases.\n");

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(500, 120));
        logScroll.setBorder(createTitledBorder("Build Console"));

        gbc.gridy = 4; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        center.add(logScroll, gbc);

        add(center, BorderLayout.CENTER);
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                title
        );
        border.setTitleColor(SciFiColors.TEXT_PRIMARY);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        return border;
    }

    private void chooseDirectory(JTextField target) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void runPacker() {
        String inPath = inputDirField.getText().trim();
        String outPath = outputDirField.getText().trim();
        String atlasName = atlasNameField.getText().trim();

        if (inPath.isEmpty() || outPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select both Input and Output directories.", "Missing Directory", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File inDir = new File(inPath);
        File outDir = new File(outPath);

        if (!inDir.exists()) {
            JOptionPane.showMessageDialog(this, "Input directory does not exist.", "Invalid Path", JOptionPane.ERROR_MESSAGE);
            return;
        }

        settings.setMaxWidth((Integer) maxWidthSpinner.getValue());
        settings.setMaxHeight((Integer) maxHeightSpinner.getValue());
        settings.setPaddingX((Integer) paddingSpinner.getValue());
        settings.setPaddingY((Integer) paddingSpinner.getValue());
        settings.setStripWhitespaceX(stripWhitespaceBox.isSelected());
        settings.setStripWhitespaceY(stripWhitespaceBox.isSelected());
        settings.setDuplicatePadding(duplicatePaddingBox.isSelected());
        settings.setRotation(rotationBox.isSelected());
        settings.setFormat((String) formatCombo.getSelectedItem());

        logArea.append("Packing images from: " + inDir.getAbsolutePath() + "\n");
        logArea.append("Target atlas: " + new File(outDir, atlasName).getAbsolutePath() + "\n");

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    TexturePackerEngine.pack(inDir, outDir, atlasName, settings);
                } catch (Exception ex) {
                    publish("Pack failed: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                logArea.append("Texture packing complete!\n");
            }
        };
        worker.execute();
    }
}
