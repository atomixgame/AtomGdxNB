package com.atomgdx.core.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Shared DRY UI utilities and styling tokens for AtomGDX editors and viewers.
 * Provides unified dark theme colors, compact input controls, and collapsible component sections.
 */
public final class DarkThemeUtils {

    private DarkThemeUtils() {}

    // Color Palette (JetBrains / Unity Dark)
    public static final Color BG_DARK = new Color(26, 26, 28);           // #1a1a1c
    public static final Color BG_PANEL = new Color(34, 35, 38);          // #222326
    public static final Color BG_HEADER = new Color(42, 44, 48);         // #2a2c30
    public static final Color BG_HEADER_HOVER = new Color(50, 52, 58);   // #32343a
    public static final Color BG_INPUT = new Color(20, 21, 23);          // #141517
    public static final Color TEXT_PRIMARY = new Color(225, 228, 232);    // #e1e4e8
    public static final Color TEXT_SECONDARY = new Color(150, 155, 162);  // #969ba2
    public static final Color TEXT_MUTED = new Color(105, 110, 118);      // #696e76
    public static final Color BORDER = new Color(55, 57, 62);             // #37393e
    public static final Color ACCENT_BLUE = new Color(44, 93, 212);      // #2c5dd4
    public static final Color AXIS_X = new Color(220, 75, 75);           // Red
    public static final Color AXIS_Y = new Color(75, 190, 85);           // Green
    public static final Color AXIS_Z = new Color(65, 140, 230);          // Blue

    private static final Map<String, ImageIcon> ICON_CACHE = new HashMap<>();

    public static ImageIcon getFatcowIcon(String name) {
        if (name == null || name.isEmpty()) return createFallbackIcon("?");
        if (ICON_CACHE.containsKey(name)) return ICON_CACHE.get(name);

        try {
            // 1. Classpath core icons
            URL url = DarkThemeUtils.class.getResource("/com/atomgdx/core/icons/" + name);
            if (url == null) url = DarkThemeUtils.class.getResource("/com/atomgdx/theme/icons/" + name);
            if (url == null) url = DarkThemeUtils.class.getResource("icons/" + name);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                ICON_CACHE.put(name, icon);
                return icon;
            }

            // 2. Direct Dev path fallback
            File devFatcow = new File("G:/Dev/Resources/fatcow-master/16x16/" + name);
            if (devFatcow.exists()) {
                ImageIcon icon = new ImageIcon(devFatcow.getAbsolutePath());
                ICON_CACHE.put(name, icon);
                return icon;
            }
        } catch (Throwable ignored) {}

        // 3. Dynamic crisp 16x16 icon generator fallback
        ImageIcon fallback = createFallbackIcon(name);
        ICON_CACHE.put(name, fallback);
        return fallback;
    }

    private static ImageIcon createFallbackIcon(String name) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color c = new Color(44, 93, 212);
        if (name.contains("red") || name.contains("x") || name.contains("delete")) c = AXIS_X;
        else if (name.contains("green") || name.contains("y") || name.contains("play")) c = AXIS_Y;
        else if (name.contains("blue") || name.contains("z") || name.contains("cube")) c = AXIS_Z;
        else if (name.contains("sun") || name.contains("gold")) c = new Color(234, 179, 8);

        g2.setColor(c);
        g2.fillRoundRect(1, 1, 14, 14, 3, 3);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));

        String letter = name.substring(0, 1).toUpperCase();
        FontMetrics fm = g2.getFontMetrics();
        int x = (16 - fm.stringWidth(letter)) / 2;
        int y = (16 - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letter, x, y);

        g2.dispose();
        return new ImageIcon(img);
    }

    public static JTextField createCompactTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(1, 4, 1, 4)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return tf;
    }

    public static JSpinner createCompactSpinner(float val, float min, float max, float step) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, step));
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(BG_INPUT);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(TEXT_PRIMARY);
            tf.setBorder(new EmptyBorder(1, 2, 1, 2));
        }
        sp.setBorder(new LineBorder(BORDER, 1));
        sp.setPreferredSize(new Dimension(55, 20));
        return sp;
    }

    public static JPanel createPropContainer(String label) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(500, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(75, 20));

        r.add(lbl, BorderLayout.WEST);
        return r;
    }

    public static JPanel createVector2Row(String rowLabel, String l1, JSpinner sp1, String l2, JSpinner sp2) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(500, 22));

        JLabel lbl = new JLabel(rowLabel);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(75, 20));

        JPanel inputs = new JPanel(new GridLayout(1, 2, 6, 0));
        inputs.setOpaque(false);
        inputs.add(createLabeledInputRow(l1, sp1, AXIS_X));
        inputs.add(createLabeledInputRow(l2, sp2, AXIS_Y));

        r.add(lbl, BorderLayout.WEST);
        r.add(inputs, BorderLayout.CENTER);
        return r;
    }

    public static JPanel createLabeledInputRow(String label, JComponent input, Color labelColor) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(500, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(labelColor != null ? labelColor : TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(14, 20));

        r.add(lbl, BorderLayout.WEST);
        r.add(input, BorderLayout.CENTER);
        return r;
    }

    public static JPanel createAxisField(String axis, Color color, JSpinner spinner) {
        return createLabeledInputRow(axis, spinner, color);
    }

    public static JPanel createSinglePropRow(String label, JComponent input) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(500, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(75, 20));

        r.add(lbl, BorderLayout.WEST);
        r.add(input, BorderLayout.CENTER);
        return r;
    }

    /**
     * Unity-Style collapsible section component with animated foldout arrow (▼/▶).
     */
    public static class CollapsibleSection extends JPanel {
        private final JPanel contentPanel;
        private final JLabel toggleIcon;
        private boolean isExpanded = true;

        public CollapsibleSection(String title, Icon headerIcon, JPanel content, JComponent rightComponent) {
            this.contentPanel = content;
            setLayout(new BorderLayout(0, 0));
            setOpaque(false);

            JPanel header = new JPanel(new BorderLayout(4, 0));
            header.setBackground(BG_HEADER);
            header.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER, 1),
                    new EmptyBorder(3, 6, 3, 6)
            ));
            header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            leftHeader.setOpaque(false);

            toggleIcon = new JLabel("▼");
            toggleIcon.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            toggleIcon.setForeground(TEXT_SECONDARY);
            leftHeader.add(toggleIcon);

            if (headerIcon != null) {
                leftHeader.add(new JLabel(headerIcon));
            }

            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            titleLbl.setForeground(TEXT_PRIMARY);
            leftHeader.add(titleLbl);

            header.add(leftHeader, BorderLayout.WEST);
            if (rightComponent != null) {
                header.add(rightComponent, BorderLayout.EAST);
            }

            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggle();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    header.setBackground(BG_HEADER_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    header.setBackground(BG_HEADER);
                }
            });

            add(header, BorderLayout.NORTH);
            add(contentPanel, BorderLayout.CENTER);
        }

        public void toggle() {
            isExpanded = !isExpanded;
            toggleIcon.setText(isExpanded ? "▼" : "▶");
            contentPanel.setVisible(isExpanded);
            revalidate();
            repaint();
        }

        public void setExpanded(boolean expanded) {
            this.isExpanded = expanded;
            toggleIcon.setText(isExpanded ? "▼" : "▶");
            contentPanel.setVisible(isExpanded);
            revalidate();
            repaint();
        }
    }
}
