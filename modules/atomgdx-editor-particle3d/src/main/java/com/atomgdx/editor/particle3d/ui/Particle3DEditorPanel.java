package com.atomgdx.editor.particle3d.ui;

import com.atomgdx.editor.particle3d.Particle3DEffectModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Visual Editor for 3D Flame particle systems and emitter physics influencers.
 */
public class Particle3DEditorPanel extends JPanel {

    private final Particle3DEffectModel model;
    private final DefaultListModel<String> emitterListModel = new DefaultListModel<>();
    private final JList<String> emitterList = new JList<>(emitterListModel);

    private final JTextField nameField = new JTextField();
    private final JComboBox<Particle3DEffectModel.RendererType> rendererCombo = new JComboBox<>(Particle3DEffectModel.RendererType.values());
    private final JSpinner maxParticlesSpinner = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 50));
    private final JSpinner lifeTimeSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.1, 30.0, 0.1));
    private final JSpinner spawnRateSpinner = new JSpinner(new SpinnerNumberModel(100.0, 1.0, 2000.0, 10.0));
    private final JCheckBox useGravityBox = new JCheckBox("Enable Gravity / Physics", false);
    private final JSpinner gravitySpinner = new JSpinner(new SpinnerNumberModel(-9.8, -50.0, 50.0, 0.5));
    private final JCheckBox useColorBox = new JCheckBox("Color Gradient Influencer", true);

    private final JPanel previewCanvas;
    private final List<SimulatedParticle> activeParticles = new ArrayList<>();
    private final Timer simulationTimer;

    public Particle3DEditorPanel() {
        this(new Particle3DEffectModel());
    }

    public Particle3DEditorPanel(Particle3DEffectModel model) {
        this.model = model != null ? model : new Particle3DEffectModel();
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(25, 26, 29));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header Toolbar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(34, 35, 38));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("3D Particle Flame Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(255, 140, 0));
        header.add(title, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerActions.setOpaque(false);
        JButton addEmitterBtn = new JButton("+ Add Emitter");
        addEmitterBtn.addActionListener(e -> addEmitter());
        JButton removeEmitterBtn = new JButton("- Delete");
        removeEmitterBtn.addActionListener(e -> removeSelectedEmitter());
        headerActions.add(addEmitterBtn);
        headerActions.add(removeEmitterBtn);
        header.add(headerActions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center Split: Left Emitter Tree & Config, Right Simulation Preview
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setBackground(new Color(25, 26, 29));
        splitPane.setBorder(null);

        // Left Container
        JPanel leftContainer = new JPanel(new BorderLayout(4, 4));
        leftContainer.setBackground(new Color(34, 35, 38));

        // Emitter List
        emitterList.setBackground(new Color(20, 21, 23));
        emitterList.setForeground(Color.WHITE);
        emitterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshEmitterList();
        emitterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmitter();
            }
        });
        JScrollPane listScroll = new JScrollPane(emitterList);
        listScroll.setPreferredSize(new Dimension(300, 110));
        listScroll.setBorder(createTitledBorder("Emitters"));
        leftContainer.add(listScroll, BorderLayout.NORTH);

        // Config Grid
        JPanel configPanel = new JPanel(new GridLayout(8, 2, 6, 4));
        configPanel.setBackground(new Color(34, 35, 38));
        configPanel.setBorder(createTitledBorder("Emitter Parameters"));

        configPanel.add(new JLabel("Emitter Name:"));
        configPanel.add(nameField);
        configPanel.add(new JLabel("Renderer Type:"));
        configPanel.add(rendererCombo);
        configPanel.add(new JLabel("Max Particles:"));
        configPanel.add(maxParticlesSpinner);
        configPanel.add(new JLabel("Life Time (sec):"));
        configPanel.add(lifeTimeSpinner);
        configPanel.add(new JLabel("Spawn Rate (/s):"));
        configPanel.add(spawnRateSpinner);
        configPanel.add(useGravityBox);
        configPanel.add(gravitySpinner);
        configPanel.add(useColorBox);

        JButton applyBtn = new JButton("Apply Parameters");
        applyBtn.setBackground(new Color(255, 140, 0));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        applyBtn.addActionListener(e -> saveSelectedEmitter());
        configPanel.add(applyBtn);

        leftContainer.add(configPanel, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftContainer);

        // Right Preview Simulation Canvas
        previewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark 3D grid viewport
                g2.setColor(new Color(18, 19, 21));
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(35, 37, 42));
                for (int x = 0; x < w; x += 32) g2.drawLine(x, 0, x, h);
                for (int y = 0; y < h; y += 32) g2.drawLine(0, y, w, y);

                // Origin axes cross
                g2.setColor(new Color(220, 60, 60, 160));
                g2.drawLine(0, h / 2, w, h / 2);
                g2.setColor(new Color(60, 200, 80, 160));
                g2.drawLine(w / 2, 0, w / 2, h);

                // Draw active particles
                synchronized (activeParticles) {
                    for (SimulatedParticle p : activeParticles) {
                        float alpha = Math.max(0f, Math.min(1f, 1f - (p.age / p.maxLife)));
                        g2.setColor(new Color(1f, 0.55f, 0.1f, alpha));
                        int px = (int) (w / 2 + p.x);
                        int py = (int) (h / 2 - p.y);
                        int size = (int) (p.size * (1f + p.age * 0.5f));
                        g2.fillOval(px - size / 2, py - size / 2, size, size);
                    }
                }

                g2.setColor(Color.LIGHT_GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString("3D Particles: " + activeParticles.size(), 12, 20);

                g2.dispose();
            }
        };
        previewCanvas.setBackground(new Color(18, 19, 21));
        previewCanvas.setBorder(createTitledBorder("Live 3D Viewport"));
        splitPane.setRightComponent(previewCanvas);

        add(splitPane, BorderLayout.CENTER);

        // Select first
        if (!this.model.getEmitters().isEmpty()) {
            emitterList.setSelectedIndex(0);
        }

        // 60 FPS Particle Simulation Timer
        simulationTimer = new Timer(16, e -> updateSimulation());
        simulationTimer.start();
    }

    private void refreshEmitterList() {
        emitterListModel.clear();
        for (Particle3DEffectModel.Emitter3D emitter : model.getEmitters()) {
            emitterListModel.addElement(emitter.getName());
        }
    }

    private void loadSelectedEmitter() {
        int idx = emitterList.getSelectedIndex();
        if (idx >= 0 && idx < model.getEmitters().size()) {
            Particle3DEffectModel.Emitter3D em = model.getEmitters().get(idx);
            nameField.setText(em.getName());
            rendererCombo.setSelectedItem(em.getRendererType());
            maxParticlesSpinner.setValue(em.getMaxParticleCount());
            lifeTimeSpinner.setValue((double) em.getLifeTime());
            spawnRateSpinner.setValue((double) em.getSpawnRate());
            useGravityBox.setSelected(em.isUseGravity());
            gravitySpinner.setValue((double) em.getGravityStrength());
            useColorBox.setSelected(em.isUseColorInfluencer());
        }
    }

    private void saveSelectedEmitter() {
        int idx = emitterList.getSelectedIndex();
        if (idx >= 0 && idx < model.getEmitters().size()) {
            Particle3DEffectModel.Emitter3D em = model.getEmitters().get(idx);
            em.setName(nameField.getText().trim());
            em.setRendererType((Particle3DEffectModel.RendererType) rendererCombo.getSelectedItem());
            em.setMaxParticleCount((Integer) maxParticlesSpinner.getValue());
            em.setLifeTime(((Double) lifeTimeSpinner.getValue()).floatValue());
            em.setSpawnRate(((Double) spawnRateSpinner.getValue()).floatValue());
            em.setUseGravity(useGravityBox.isSelected());
            em.setGravityStrength(((Double) gravitySpinner.getValue()).floatValue());
            em.setUseColorInfluencer(useColorBox.isSelected());
            refreshEmitterList();
            emitterList.setSelectedIndex(idx);
        }
    }

    private void addEmitter() {
        Particle3DEffectModel.Emitter3D newEmitter = new Particle3DEffectModel.Emitter3D("Emitter " + (model.getEmitters().size() + 1));
        model.addEmitter(newEmitter);
        refreshEmitterList();
        emitterList.setSelectedIndex(model.getEmitters().size() - 1);
    }

    private void removeSelectedEmitter() {
        int idx = emitterList.getSelectedIndex();
        if (idx >= 0 && model.getEmitters().size() > 1) {
            model.removeEmitter(model.getEmitters().get(idx));
            refreshEmitterList();
            emitterList.setSelectedIndex(Math.max(0, idx - 1));
        }
    }

    private void updateSimulation() {
        int idx = emitterList.getSelectedIndex();
        if (idx < 0 || idx >= model.getEmitters().size()) return;
        Particle3DEffectModel.Emitter3D em = model.getEmitters().get(idx);

        float dt = 0.016f;
        synchronized (activeParticles) {
            // Spawn new particles
            int spawnCount = Math.max(1, (int) (em.getSpawnRate() * dt));
            for (int i = 0; i < spawnCount && activeParticles.size() < em.getMaxParticleCount(); i++) {
                float vx = (float) ((Math.random() - 0.5) * 80.0);
                float vy = (float) (Math.random() * 90.0 + 30.0);
                activeParticles.add(new SimulatedParticle(0, 0, vx, vy, em.getLifeTime()));
            }

            // Update existing
            for (int i = activeParticles.size() - 1; i >= 0; i--) {
                SimulatedParticle p = activeParticles.get(i);
                p.age += dt;
                if (p.age >= p.maxLife) {
                    activeParticles.remove(i);
                    continue;
                }
                if (em.isUseGravity()) {
                    p.vy += em.getGravityStrength() * 10f * dt;
                }
                p.x += p.vx * dt;
                p.y += p.vy * dt;
            }
        }

        previewCanvas.repaint();
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(55, 57, 62), 1),
                title
        );
        border.setTitleColor(new Color(225, 228, 232));
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        return border;
    }

    private static class SimulatedParticle {
        float x, y, vx, vy, age, maxLife, size;
        SimulatedParticle(float x, float y, float vx, float vy, float maxLife) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
            this.maxLife = maxLife; this.age = 0;
            this.size = (float) (Math.random() * 8.0 + 4.0);
        }
    }
}
