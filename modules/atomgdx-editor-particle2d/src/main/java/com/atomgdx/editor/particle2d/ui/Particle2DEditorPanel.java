package com.atomgdx.editor.particle2d.ui;

import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.Particle2DEmitterModel;
import com.atomgdx.core.SciFiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Visual Editor UI for 2D Particle Effects with emitter properties and interactive preview canvas.
 */
public class Particle2DEditorPanel extends JPanel {
    private final Particle2DEffectModel effectModel;
    private final DefaultListModel<String> emitterListModel = new DefaultListModel<>();
    private final JList<String> emitterList = new JList<>(emitterListModel);
    private final ParticlePreviewCanvas previewCanvas;

    public Particle2DEditorPanel(Particle2DEffectModel effectModel) {
        this.effectModel = effectModel != null ? effectModel : new Particle2DEffectModel("New Effect");
        setLayout(new BorderLayout(10, 10));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Center Viewport
        previewCanvas = new ParticlePreviewCanvas();

        // Left Emitter & Property Sidebar
        JPanel sidebar = createSidebar();

        // Top Toolbar
        JToolBar toolBar = createToolBar();

        add(toolBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(previewCanvas, BorderLayout.CENTER);

        refreshEmitterList();
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SciFiColors.BG_PANEL);

        JButton playBtn = new JButton("Play");
        JButton pauseBtn = new JButton("Pause");
        JButton restartBtn = new JButton("Restart");
        JButton addEmitterBtn = new JButton("+ Add Emitter");

        playBtn.addActionListener(e -> previewCanvas.start());
        pauseBtn.addActionListener(e -> previewCanvas.pause());
        restartBtn.addActionListener(e -> previewCanvas.restart());
        addEmitterBtn.addActionListener(e -> {
            effectModel.addEmitter(new Particle2DEmitterModel("Emitter " + (effectModel.getEmitters().size() + 1)));
            refreshEmitterList();
        });

        tb.add(playBtn);
        tb.add(pauseBtn);
        tb.add(restartBtn);
        tb.addSeparator();
        tb.add(addEmitterBtn);
        return tb;
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(280, 500));
        panel.setBackground(SciFiColors.BG_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE),
                "Emitters & Properties",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                SciFiColors.ACCENT_CYAN
        ));

        emitterList.setBackground(SciFiColors.BG_DARK);
        emitterList.setForeground(SciFiColors.TEXT_PRIMARY);
        panel.add(new JScrollPane(emitterList), BorderLayout.NORTH);

        // Property Controls
        JPanel propsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        propsPanel.setOpaque(false);
        propsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        propsPanel.add(new JLabel("Count (Max):"));
        propsPanel.add(new JSpinner(new SpinnerNumberModel(200, 1, 5000, 10)));

        propsPanel.add(new JLabel("Duration (ms):"));
        propsPanel.add(new JSpinner(new SpinnerNumberModel(1000, 100, 60000, 100)));

        propsPanel.add(new JLabel("Velocity:"));
        propsPanel.add(new JSpinner(new SpinnerNumberModel(100, 0, 1000, 10)));

        propsPanel.add(new JLabel("Scale (px):"));
        propsPanel.add(new JSpinner(new SpinnerNumberModel(20, 1, 500, 5)));

        propsPanel.add(new JLabel("Additive Blend:"));
        propsPanel.add(new JCheckBox("", true));

        panel.add(propsPanel, BorderLayout.CENTER);
        return panel;
    }

    private void refreshEmitterList() {
        emitterListModel.clear();
        for (Particle2DEmitterModel e : effectModel.getEmitters()) {
            emitterListModel.addElement(e.getName());
        }
        if (!emitterListModel.isEmpty()) {
            emitterList.setSelectedIndex(0);
        }
    }

    public static class ParticlePreviewCanvas extends JPanel {
        private final List<Point2D.Float> simulatedParticles = new ArrayList<>();
        private final Random random = new Random();
        private boolean running = true;

        public ParticlePreviewCanvas() {
            setBackground(new Color(0x05, 0x07, 0x0A));
            setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));
            for (int i = 0; i < 80; i++) {
                simulatedParticles.add(new Point2D.Float(random.nextFloat() * 200 - 100, random.nextFloat() * 200 - 100));
            }
        }

        public void start() { running = true; repaint(); }
        public void pause() { running = false; }
        public void restart() { simulatedParticles.clear(); for (int i = 0; i < 80; i++) simulatedParticles.add(new Point2D.Float(random.nextFloat() * 200 - 100, random.nextFloat() * 200 - 100)); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            g2.setColor(new Color(0x16, 0x1B, 0x22));
            g2.drawLine(0, cy, getWidth(), cy);
            g2.drawLine(cx, 0, cx, getHeight());

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            for (Point2D.Float p : simulatedParticles) {
                float dist = (float) Math.hypot(p.x, p.y);
                int alpha = Math.max(20, Math.min(255, (int) (255 - dist * 1.5f)));
                g2.setColor(new Color(0, 240, 255, alpha));
                g2.fillOval((int) (cx + p.x - 6), (int) (cy + p.y - 6), 12, 12);
                g2.setColor(new Color(255, 255, 255, alpha));
                g2.fillOval((int) (cx + p.x - 2), (int) (cy + p.y - 2), 4, 4);
            }

            g2.dispose();
        }
    }
}
