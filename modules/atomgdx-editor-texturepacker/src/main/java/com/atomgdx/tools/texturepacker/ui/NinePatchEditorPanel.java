package com.atomgdx.tools.texturepacker.ui;

import com.atomgdx.core.SciFiColors;
import com.atomgdx.tools.texturepacker.NinePatchEditorModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Interactive 9-Patch Slice Visual Editor with 4-axis margin sliders and scalable live preview.
 */
public class NinePatchEditorPanel extends JPanel {
    private final NinePatchEditorModel model;
    private final JSpinner leftSpinner;
    private final JSpinner rightSpinner;
    private final JSpinner topSpinner;
    private final JSpinner bottomSpinner;
    private final JPanel previewCanvas;

    public NinePatchEditorPanel(NinePatchEditorModel model) {
        this.model = model != null ? model : new NinePatchEditorModel(12, 12, 12, 12);
        setLayout(new BorderLayout(10, 10));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Controls Sidebar
        JPanel sidebar = new JPanel(new GridLayout(5, 2, 8, 8));
        sidebar.setPreferredSize(new Dimension(240, 300));
        sidebar.setBackground(SciFiColors.BG_PANEL);
        sidebar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE),
                "9-Patch Margins",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                SciFiColors.ACCENT_CYAN
        ));

        leftSpinner = new JSpinner(new SpinnerNumberModel(this.model.getLeft(), 0, 200, 1));
        rightSpinner = new JSpinner(new SpinnerNumberModel(this.model.getRight(), 0, 200, 1));
        topSpinner = new JSpinner(new SpinnerNumberModel(this.model.getTop(), 0, 200, 1));
        bottomSpinner = new JSpinner(new SpinnerNumberModel(this.model.getBottom(), 0, 200, 1));

        sidebar.add(new JLabel("Left Margin:"));
        sidebar.add(leftSpinner);
        sidebar.add(new JLabel("Right Margin:"));
        sidebar.add(rightSpinner);
        sidebar.add(new JLabel("Top Margin:"));
        sidebar.add(topSpinner);
        sidebar.add(new JLabel("Bottom Margin:"));
        sidebar.add(bottomSpinner);

        JButton applyBtn = new JButton("Apply Slices");
        sidebar.add(applyBtn);

        // Center Scalable Preview
        previewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw stretched 9-patch frame
                g2.setColor(SciFiColors.BG_CARD);
                g2.fillRoundRect(40, 40, w - 80, h - 80, 16, 16);

                g2.setColor(SciFiColors.ACCENT_CYAN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(40, 40, w - 80, h - 80, 16, 16);

                // Draw margin guides
                g2.setColor(new Color(255, 0, 128, 150));
                int l = 40 + (Integer) leftSpinner.getValue() * 2;
                int r = w - 40 - (Integer) rightSpinner.getValue() * 2;
                int t = 40 + (Integer) topSpinner.getValue() * 2;
                int b = h - 40 - (Integer) bottomSpinner.getValue() * 2;

                g2.drawLine(l, 40, l, h - 40);
                g2.drawLine(r, 40, r, h - 40);
                g2.drawLine(40, t, w - 40, t);
                g2.drawLine(40, b, w - 40, b);

                g2.drawString("9-Patch Interactive Preview (Scaled & Stretched)", 60, 70);
                g2.dispose();
            }
        };
        previewCanvas.setBackground(SciFiColors.BG_DARK);
        previewCanvas.setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));

        applyBtn.addActionListener(e -> previewCanvas.repaint());

        add(sidebar, BorderLayout.WEST);
        add(previewCanvas, BorderLayout.CENTER);
    }
}
