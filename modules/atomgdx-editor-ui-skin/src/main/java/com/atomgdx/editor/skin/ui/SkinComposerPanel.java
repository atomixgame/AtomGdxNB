package com.atomgdx.editor.skin.ui;

import com.atomgdx.editor.skin.SkinModel;
import com.atomgdx.editor.skin.WidgetStyle;
import com.atomgdx.core.SciFiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Visual Skin Composer panel for designing and testing LibGDX Scene2D / VisUI skins.
 */
public class SkinComposerPanel extends JPanel {
    private final SkinModel skinModel;
    private final JTree styleTree;
    private final JPanel previewPanel;

    public SkinComposerPanel(SkinModel skinModel) {
        this.skinModel = skinModel != null ? skinModel : new SkinModel();
        setLayout(new BorderLayout(8, 8));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Style Tree
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setPreferredSize(new Dimension(240, 500));
        treePanel.setBackground(SciFiColors.BG_PANEL);
        treePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE),
                "Widget Styles & Classes",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                SciFiColors.ACCENT_CYAN
        ));

        styleTree = new JTree();
        styleTree.setBackground(SciFiColors.BG_DARK);
        styleTree.setForeground(SciFiColors.TEXT_PRIMARY);
        treePanel.add(new JScrollPane(styleTree), BorderLayout.CENTER);

        // Center Live Preview Gallery
        previewPanel = createPreviewGallery();

        add(createToolBar(), BorderLayout.NORTH);
        add(treePanel, BorderLayout.WEST);
        add(new JScrollPane(previewPanel), BorderLayout.CENTER);
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SciFiColors.BG_PANEL);

        tb.add(new JButton("+ New Style"));
        tb.add(new JButton("Color Palette"));
        tb.add(new JButton("Font Manager"));
        tb.addSeparator();
        tb.add(new JButton("Export Skin JSON"));
        return tb;
    }

    private JPanel createPreviewGallery() {
        JPanel gallery = new JPanel();
        gallery.setLayout(new BoxLayout(gallery, BoxLayout.Y_AXIS));
        gallery.setBackground(SciFiColors.BG_DARK);
        gallery.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Live Scene2D Widget Preview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(SciFiColors.ACCENT_CYAN);
        gallery.add(title);
        gallery.add(Box.createVerticalStrut(15));

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnRow.setOpaque(false);
        JButton defBtn = new JButton("Default Button");
        JButton primaryBtn = new JButton("Primary Action");
        primaryBtn.setBackground(SciFiColors.ACCENT_CYAN);
        primaryBtn.setForeground(SciFiColors.BG_DARKEST);
        JButton dangerBtn = new JButton("Danger");
        dangerBtn.setBackground(SciFiColors.ACCENT_RED);
        btnRow.add(defBtn);
        btnRow.add(primaryBtn);
        btnRow.add(dangerBtn);
        gallery.add(btnRow);
        gallery.add(Box.createVerticalStrut(10));

        // Inputs row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputRow.setOpaque(false);
        inputRow.add(new JTextField("Text Field Input", 18));
        inputRow.add(new JCheckBox("Checked Option", true));
        inputRow.add(new JRadioButton("Radio Option", true));
        gallery.add(inputRow);
        gallery.add(Box.createVerticalStrut(10));

        // Slider & Progress
        JPanel sliderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        sliderRow.setOpaque(false);
        JSlider slider = new JSlider(0, 100, 65);
        slider.setPreferredSize(new Dimension(200, 25));
        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(75);
        pb.setStringPainted(true);
        pb.setPreferredSize(new Dimension(200, 22));
        sliderRow.add(slider);
        sliderRow.add(pb);
        gallery.add(sliderRow);

        return gallery;
    }
}
