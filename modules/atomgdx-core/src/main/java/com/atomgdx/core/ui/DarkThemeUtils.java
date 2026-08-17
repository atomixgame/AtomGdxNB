package com.atomgdx.core.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

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

    public static ImageIcon getFatcowIcon(String name) {
        try {
            URL url = DarkThemeUtils.class.getResource("/com/atomgdx/theme/icons/" + name);
            if (url == null) {
                url = DarkThemeUtils.class.getResource("icons/" + name);
            }
            if (url != null) return new ImageIcon(url);
        } catch (Throwable ignored) {}
        return null;
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
        return sp;
    }

    public static JPanel createPropContainer(String labelText) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(500, 22));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(75, 20));
        r.add(lbl, BorderLayout.WEST);
        return r;
    }

    public static JPanel createSinglePropRow(String label, JComponent comp) {
        JPanel r = createPropContainer(label);
        r.add(comp, BorderLayout.CENTER);
        return r;
    }

    public static JPanel createVector2Row(String labelText, String axis1, JSpinner s1, String axis2, JSpinner s2) {
        JPanel r = createPropContainer(labelText);
        JPanel inner = new JPanel(new GridLayout(1, 2, 4, 0));
        inner.setOpaque(false);

        inner.add(createAxisField(axis1, AXIS_X, s1));
        inner.add(createAxisField(axis2, AXIS_Y, s2));

        r.add(inner, BorderLayout.CENTER);
        return r;
    }

    public static JPanel createAxisField(String axis, Color axisColor, JSpinner spinner) {
        JPanel p = new JPanel(new BorderLayout(2, 0));
        p.setOpaque(false);

        JLabel tag = new JLabel(axis);
        tag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tag.setForeground(axisColor);
        tag.setPreferredSize(new Dimension(12, 20));

        p.add(tag, BorderLayout.WEST);
        p.add(spinner, BorderLayout.CENTER);
        return p;
    }

    /**
     * Reusable Collapsible Section for Inspectors and Property Panels.
     */
    public static class CollapsibleSection extends JPanel {
        private final JPanel contentPanel;
        private final JLabel toggleArrow = new JLabel("▼");
        private boolean isExpanded = true;

        public CollapsibleSection(String title, ImageIcon icon, JPanel content, JCheckBox enableCheckbox) {
            setLayout(new BorderLayout());
            setBackground(BG_PANEL);
            setBorder(new LineBorder(BORDER, 1));
            this.contentPanel = content;

            JPanel header = new JPanel(new BorderLayout(4, 0));
            header.setBackground(BG_HEADER);
            header.setBorder(new EmptyBorder(3, 6, 3, 6));
            header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            left.setOpaque(false);

            toggleArrow.setFont(new Font("Segoe UI", Font.BOLD, 10));
            toggleArrow.setForeground(TEXT_SECONDARY);
            left.add(toggleArrow);

            if (enableCheckbox != null) {
                left.add(enableCheckbox);
            }

            if (icon != null) {
                left.add(new JLabel(icon));
            }

            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            titleLbl.setForeground(TEXT_PRIMARY);
            left.add(titleLbl);

            header.add(left, BorderLayout.WEST);

            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setExpanded(!isExpanded);
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

        public void setExpanded(boolean expanded) {
            this.isExpanded = expanded;
            toggleArrow.setText(expanded ? "▼" : "▶");
            contentPanel.setVisible(expanded);
            revalidate();
            repaint();
        }
    }
}
