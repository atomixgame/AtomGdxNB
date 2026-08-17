package com.atomgdx.editor.particle2d.ui;

import com.atomgdx.core.SciFiColors;
import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.Particle2DEmitterModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Visual Editor for 2D Particle Effects with real-time physics simulation,
 * interactive keyframing/controls, and multi-emitter hierarchy.
 */
public class Particle2DEditorPanel extends JPanel {

    private final Particle2DEffectModel effectModel;
    private final DefaultListModel<String> emitterListModel = new DefaultListModel<>();
    private final JList<String> emitterList = new JList<>(emitterListModel);
    private final ParticlePreviewCanvas previewCanvas;

    // Property editors
    private JTextField nameField;
    private JSpinner countSpinner;
    private JSpinner durationSpinner;
    private JSpinner emissionSpinner;
    private JSpinner lifeMinSpinner;
    private JSpinner lifeMaxSpinner;
    private JSpinner scaleMinSpinner;
    private JSpinner scaleMaxSpinner;
    private JSpinner velMinSpinner;
    private JSpinner velMaxSpinner;
    private JSpinner angleMinSpinner;
    private JSpinner angleMaxSpinner;
    private JSpinner windSpinner;
    private JSpinner gravitySpinner;
    private JCheckBox additiveBox;
    private JCheckBox continuousBox;
    private JCheckBox attachedBox;
    private JCheckBox behindBox;

    private boolean updatingUI = false;

    public Particle2DEditorPanel(Particle2DEffectModel effectModel) {
        this.effectModel = effectModel != null ? effectModel : new Particle2DEffectModel("New Effect");
        if (this.effectModel.getEmitters().isEmpty()) {
            this.effectModel.addEmitter(new Particle2DEmitterModel("Default Emitter"));
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Center Viewport
        previewCanvas = new ParticlePreviewCanvas(this.effectModel);

        // Left Sidebar
        JPanel sidebar = createSidebar();

        // Top Toolbar
        JToolBar toolBar = createToolBar();

        add(toolBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(previewCanvas, BorderLayout.CENTER);

        refreshEmitterList();
        setupEvents();
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SciFiColors.BG_PANEL);

        JButton playBtn = new JButton("Play");
        JButton pauseBtn = new JButton("Pause");
        JButton restartBtn = new JButton("Restart");
        JButton addEmitterBtn = new JButton("+ Add Emitter");
        JButton deleteEmitterBtn = new JButton("- Delete Emitter");

        playBtn.addActionListener(e -> previewCanvas.start());
        pauseBtn.addActionListener(e -> previewCanvas.pause());
        restartBtn.addActionListener(e -> previewCanvas.restart());
        addEmitterBtn.addActionListener(e -> {
            Particle2DEmitterModel newEmitter = new Particle2DEmitterModel("Emitter " + (effectModel.getEmitters().size() + 1));
            effectModel.addEmitter(newEmitter);
            refreshEmitterList();
            emitterList.setSelectedIndex(effectModel.getEmitters().size() - 1);
        });
        deleteEmitterBtn.addActionListener(e -> {
            int selected = emitterList.getSelectedIndex();
            if (selected >= 0 && effectModel.getEmitters().size() > 1) {
                effectModel.getEmitters().remove(selected);
                refreshEmitterList();
                emitterList.setSelectedIndex(Math.max(0, selected - 1));
            }
        });

        tb.add(playBtn);
        tb.add(pauseBtn);
        tb.add(restartBtn);
        tb.addSeparator();
        tb.add(addEmitterBtn);
        tb.add(deleteEmitterBtn);
        return tb;
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(320, 600));
        panel.setBackground(SciFiColors.BG_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE),
                "Emitters & Parameters",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                SciFiColors.ACCENT_CYAN
        ));

        emitterList.setBackground(SciFiColors.BG_DARK);
        emitterList.setForeground(SciFiColors.TEXT_PRIMARY);
        panel.add(new JScrollPane(emitterList), BorderLayout.NORTH);

        // Property Controls Grid
        JPanel propsPanel = new JPanel(new GridBagLayout());
        propsPanel.setOpaque(false);
        propsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        int row = 0;

        // Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        nameField = new JTextField();
        propsPanel.add(nameField, gbc);
        row++;

        // Count Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Max Particles:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        countSpinner = new JSpinner(new SpinnerNumberModel(200, 1, 10000, 10));
        propsPanel.add(countSpinner, gbc);
        row++;

        // Duration
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Duration (ms):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        durationSpinner = new JSpinner(new SpinnerNumberModel(1000f, 50f, 60000f, 100f));
        propsPanel.add(durationSpinner, gbc);
        row++;

        // Emission Rate
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Emission Rate:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        emissionSpinner = new JSpinner(new SpinnerNumberModel(50f, 1f, 2000f, 10f));
        propsPanel.add(emissionSpinner, gbc);
        row++;

        // Life (Min/Max)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Life Min/Max (ms):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel lifeP = new JPanel(new GridLayout(1, 2, 4, 0));
        lifeP.setOpaque(false);
        lifeMinSpinner = new JSpinner(new SpinnerNumberModel(500f, 10f, 20000f, 50f));
        lifeMaxSpinner = new JSpinner(new SpinnerNumberModel(1000f, 10f, 20000f, 50f));
        lifeP.add(lifeMinSpinner);
        lifeP.add(lifeMaxSpinner);
        propsPanel.add(lifeP, gbc);
        row++;

        // Scale (Min/Max)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Scale Min/Max (px):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel scaleP = new JPanel(new GridLayout(1, 2, 4, 0));
        scaleP.setOpaque(false);
        scaleMinSpinner = new JSpinner(new SpinnerNumberModel(10f, 1f, 500f, 2f));
        scaleMaxSpinner = new JSpinner(new SpinnerNumberModel(30f, 1f, 500f, 2f));
        scaleP.add(scaleMinSpinner);
        scaleP.add(scaleMaxSpinner);
        propsPanel.add(scaleP, gbc);
        row++;

        // Velocity (Min/Max)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Velocity Min/Max:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel velP = new JPanel(new GridLayout(1, 2, 4, 0));
        velP.setOpaque(false);
        velMinSpinner = new JSpinner(new SpinnerNumberModel(50f, 0f, 2000f, 10f));
        velMaxSpinner = new JSpinner(new SpinnerNumberModel(150f, 0f, 2000f, 10f));
        velP.add(velMinSpinner);
        velP.add(velMaxSpinner);
        propsPanel.add(velP, gbc);
        row++;

        // Angle (Min/Max)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Angle Min/Max:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel angP = new JPanel(new GridLayout(1, 2, 4, 0));
        angP.setOpaque(false);
        angleMinSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 360f, 5f));
        angleMaxSpinner = new JSpinner(new SpinnerNumberModel(360f, 0f, 360f, 5f));
        angP.add(angleMinSpinner);
        angP.add(angleMaxSpinner);
        propsPanel.add(angP, gbc);
        row++;

        // Wind & Gravity
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Wind / Gravity:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel phyP = new JPanel(new GridLayout(1, 2, 4, 0));
        phyP.setOpaque(false);
        windSpinner = new JSpinner(new SpinnerNumberModel(0f, -1000f, 1000f, 10f));
        gravitySpinner = new JSpinner(new SpinnerNumberModel(0f, -1000f, 1000f, 10f));
        phyP.add(windSpinner);
        phyP.add(gravitySpinner);
        propsPanel.add(phyP, gbc);
        row++;

        // Checkboxes
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel checkP = new JPanel(new GridLayout(2, 2, 4, 4));
        checkP.setOpaque(false);
        additiveBox = new JCheckBox("Additive", true);
        continuousBox = new JCheckBox("Continuous", true);
        attachedBox = new JCheckBox("Attached", false);
        behindBox = new JCheckBox("Behind", false);
        checkP.add(additiveBox);
        checkP.add(continuousBox);
        checkP.add(attachedBox);
        checkP.add(behindBox);
        propsPanel.add(checkP, gbc);

        panel.add(new JScrollPane(propsPanel), BorderLayout.CENTER);
        return panel;
    }

    private void setupEvents() {
        emitterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmitter();
            }
        });

        // Add change listeners to sync values back to the active model
        nameField.addActionListener(e -> syncToModel());
        countSpinner.addChangeListener(e -> syncToModel());
        durationSpinner.addChangeListener(e -> syncToModel());
        emissionSpinner.addChangeListener(e -> syncToModel());
        lifeMinSpinner.addChangeListener(e -> syncToModel());
        lifeMaxSpinner.addChangeListener(e -> syncToModel());
        scaleMinSpinner.addChangeListener(e -> syncToModel());
        scaleMaxSpinner.addChangeListener(e -> syncToModel());
        velMinSpinner.addChangeListener(e -> syncToModel());
        velMaxSpinner.addChangeListener(e -> syncToModel());
        angleMinSpinner.addChangeListener(e -> syncToModel());
        angleMaxSpinner.addChangeListener(e -> syncToModel());
        windSpinner.addChangeListener(e -> syncToModel());
        gravitySpinner.addChangeListener(e -> syncToModel());
        additiveBox.addActionListener(e -> syncToModel());
        continuousBox.addActionListener(e -> syncToModel());
        attachedBox.addActionListener(e -> syncToModel());
        behindBox.addActionListener(e -> syncToModel());
    }

    private Particle2DEmitterModel getSelectedEmitter() {
        int idx = emitterList.getSelectedIndex();
        if (idx >= 0 && idx < effectModel.getEmitters().size()) {
            return effectModel.getEmitters().get(idx);
        }
        return null;
    }

    private void loadSelectedEmitter() {
        Particle2DEmitterModel emitter = getSelectedEmitter();
        if (emitter == null) return;

        updatingUI = true;
        nameField.setText(emitter.getName());
        countSpinner.setValue(emitter.getMaxParticleCount());
        durationSpinner.setValue(emitter.getDuration());
        emissionSpinner.setValue(emitter.getEmissionRate());
        lifeMinSpinner.setValue(emitter.getLifeMin());
        lifeMaxSpinner.setValue(emitter.getLifeMax());
        scaleMinSpinner.setValue(emitter.getScaleMin());
        scaleMaxSpinner.setValue(emitter.getScaleMax());
        velMinSpinner.setValue(emitter.getVelocityMin());
        velMaxSpinner.setValue(emitter.getVelocityMax());
        angleMinSpinner.setValue(emitter.getAngleMin());
        angleMaxSpinner.setValue(emitter.getAngleMax());
        windSpinner.setValue(emitter.getWind());
        gravitySpinner.setValue(emitter.getGravity());
        additiveBox.setSelected(emitter.isAdditive());
        continuousBox.setSelected(emitter.isContinuous());
        attachedBox.setSelected(emitter.isAttached());
        behindBox.setSelected(emitter.isBehind());
        updatingUI = false;
    }

    private void syncToModel() {
        if (updatingUI) return;
        Particle2DEmitterModel emitter = getSelectedEmitter();
        if (emitter == null) return;

        emitter.setName(nameField.getText().trim());
        emitter.setMaxParticleCount(((Number) countSpinner.getValue()).intValue());
        emitter.setDuration(((Number) durationSpinner.getValue()).floatValue());
        emitter.setEmissionRate(((Number) emissionSpinner.getValue()).floatValue());
        emitter.setLifeMin(((Number) lifeMinSpinner.getValue()).floatValue());
        emitter.setLifeMax(((Number) lifeMaxSpinner.getValue()).floatValue());
        emitter.setScaleMin(((Number) scaleMinSpinner.getValue()).floatValue());
        emitter.setScaleMax(((Number) scaleMaxSpinner.getValue()).floatValue());
        emitter.setVelocityMin(((Number) velMinSpinner.getValue()).floatValue());
        emitter.setVelocityMax(((Number) velMaxSpinner.getValue()).floatValue());
        emitter.setAngleMin(((Number) angleMinSpinner.getValue()).floatValue());
        emitter.setAngleMax(((Number) angleMaxSpinner.getValue()).floatValue());
        emitter.setWind(((Number) windSpinner.getValue()).floatValue());
        emitter.setGravity(((Number) gravitySpinner.getValue()).floatValue());
        emitter.setAdditive(additiveBox.isSelected());
        emitter.setContinuous(continuousBox.isSelected());
        emitter.setAttached(attachedBox.isSelected());
        emitter.setBehind(behindBox.isSelected());
    }

    private void refreshEmitterList() {
        emitterListModel.clear();
        for (Particle2DEmitterModel e : effectModel.getEmitters()) {
            emitterListModel.addElement(e.getName());
        }
        if (!emitterListModel.isEmpty()) {
            emitterList.setSelectedIndex(0);
            loadSelectedEmitter();
        }
    }

    /**
     * Real-time Physics Particle Simulation Canvas with LibGDX trajectory & alpha blend calculations.
     */
    public static class ParticlePreviewCanvas extends JPanel {
        private final Particle2DEffectModel effectModel;
        private final List<SimulatedParticle> activeParticles = new ArrayList<>();
        private final Random random = new Random();
        private final Timer simulationTimer;
        private boolean running = true;
        private float emitterX = 0f;
        private float emitterY = 0f;

        public ParticlePreviewCanvas(Particle2DEffectModel effectModel) {
            this.effectModel = effectModel;
            setBackground(new Color(0x06, 0x08, 0x0C));
            setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));

            // Mouse interaction to move emitter spawn origin
            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    emitterX = e.getX() - getWidth() / 2f;
                    emitterY = e.getY() - getHeight() / 2f;
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);

            // 60 FPS simulation loop
            simulationTimer = new Timer(16, e -> {
                if (running) {
                    updateSimulation(0.016f);
                    repaint();
                }
            });
            simulationTimer.start();
        }

        public void start() { running = true; }
        public void pause() { running = false; }
        public void restart() {
            activeParticles.clear();
            emitterX = 0f;
            emitterY = 0f;
            repaint();
        }

        private void updateSimulation(float delta) {
            // Spawn new particles from all enabled emitters
            for (Particle2DEmitterModel emitter : effectModel.getEmitters()) {
                if (!emitter.isEnabled()) continue;

                int spawnCount = Math.max(1, (int) (emitter.getEmissionRate() * delta));
                for (int i = 0; i < spawnCount; i++) {
                    if (activeParticles.size() >= emitter.getMaxParticleCount()) break;

                    SimulatedParticle p = new SimulatedParticle();
                    p.x = emitterX;
                    p.y = emitterY;

                    float angleDeg = emitter.getAngleMin() + random.nextFloat() * (emitter.getAngleMax() - emitter.getAngleMin());
                    float angleRad = (float) Math.toRadians(angleDeg);
                    float speed = emitter.getVelocityMin() + random.nextFloat() * (emitter.getVelocityMax() - emitter.getVelocityMin());

                    p.vx = (float) Math.cos(angleRad) * speed;
                    p.vy = (float) Math.sin(angleRad) * speed;
                    p.totalLife = (emitter.getLifeMin() + random.nextFloat() * (emitter.getLifeMax() - emitter.getLifeMin())) / 1000f;
                    p.currentLife = p.totalLife;
                    p.scaleMin = emitter.getScaleMin();
                    p.scaleMax = emitter.getScaleMax();
                    p.wind = emitter.getWind();
                    p.gravity = emitter.getGravity();
                    p.additive = emitter.isAdditive();

                    activeParticles.add(p);
                }
            }

            // Update existing particles
            Iterator<SimulatedParticle> it = activeParticles.iterator();
            while (it.hasNext()) {
                SimulatedParticle p = it.next();
                p.currentLife -= delta;
                if (p.currentLife <= 0) {
                    it.remove();
                    continue;
                }

                p.vx += p.wind * delta;
                p.vy += p.gravity * delta;
                p.x += p.vx * delta;
                p.y += p.vy * delta;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            // Coordinate grid
            g2.setColor(new Color(0x13, 0x1A, 0x24));
            g2.drawLine(0, cy, getWidth(), cy);
            g2.drawLine(cx, 0, cx, getHeight());

            // Render particles
            for (SimulatedParticle p : activeParticles) {
                float lifePercent = Math.max(0f, Math.min(1f, p.currentLife / p.totalLife));
                float currentScale = p.scaleMin + (p.scaleMax - p.scaleMin) * (1f - lifePercent);
                int alpha = (int) (255 * lifePercent);

                int px = (int) (cx + p.x);
                int py = (int) (cy + p.y);
                int r = (int) (currentScale / 2);

                if (p.additive) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lifePercent * 0.85f));
                } else {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lifePercent));
                }

                // Outer glow
                g2.setColor(new Color(0, 240, 255, Math.min(255, alpha)));
                g2.fillOval(px - r, py - r, r * 2, r * 2);

                // Core highlight
                g2.setColor(new Color(255, 255, 255, Math.min(255, alpha)));
                g2.fillOval(px - r / 3, py - r / 3, Math.max(2, r * 2 / 3), Math.max(2, r * 2 / 3));
            }

            // HUD stats
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(SciFiColors.ACCENT_CYAN);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.drawString("Particles: " + activeParticles.size(), 12, 20);
            g2.drawString("Emitter: (" + (int) emitterX + ", " + (int) emitterY + ") [Drag to move]", 12, 36);

            g2.dispose();
        }

        private static class SimulatedParticle {
            float x, y;
            float vx, vy;
            float totalLife;
            float currentLife;
            float scaleMin, scaleMax;
            float wind, gravity;
            boolean additive;
        }
    }
}
