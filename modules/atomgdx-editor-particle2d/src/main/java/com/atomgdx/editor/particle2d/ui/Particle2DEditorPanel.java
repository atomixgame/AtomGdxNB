package com.atomgdx.editor.particle2d.ui;

import com.atomgdx.core.SciFiColors;
import com.atomgdx.core.viewport.GdxAwtViewport;
import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.Particle2DEmitterModel;
import com.atomgdx.editor.particle2d.presets.ParticlePreset;
import com.atomgdx.editor.particle2d.presets.ParticlePresetsLibrary;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Visual Editor for 2D Particle Effects powered by real hardware-accelerated LwjglAWTCanvas.
 * Includes a 100-preset library with category/tag filtering and asset texture browsing.
 */
public class Particle2DEditorPanel extends JPanel {

    private final Particle2DEffectModel effectModel;
    private final DefaultListModel<String> emitterListModel = new DefaultListModel<>();
    private final JList<String> emitterList = new JList<>(emitterListModel);
    private final GdxAwtViewport gdxViewport;
    private final ParticleApplicationListener particleListener;

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
    private JTextField imagePathField;
    private JButton browseImageBtn;

    // Preset selector
    private JComboBox<String> categoryCombo;
    private JTextField presetSearchField;
    private DefaultListModel<ParticlePreset> presetListModel = new DefaultListModel<>();
    private JList<ParticlePreset> presetJList;

    private boolean updatingUI = false;

    public Particle2DEditorPanel(Particle2DEffectModel effectModel) {
        this.effectModel = effectModel != null ? effectModel : new Particle2DEffectModel("New Effect");
        if (this.effectModel.getEmitters().isEmpty()) {
            this.effectModel.addEmitter(new Particle2DEmitterModel("Default Emitter"));
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Center Viewport powered by real LibGDX LwjglAWTCanvas
        particleListener = new ParticleApplicationListener(this.effectModel);
        gdxViewport = new GdxAwtViewport(particleListener);
        if (gdxViewport.getCanvas() != null) {
            Canvas awtCanvas = gdxViewport.getCanvas().getCanvas();
            awtCanvas.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    float cx = awtCanvas.getWidth() / 2f;
                    float cy = awtCanvas.getHeight() / 2f;
                    particleListener.setEmitterPosition(e.getX() - cx, -(e.getY() - cy));
                }
            });
        }

        // Sidebar Tabs (Properties & 100 Presets Library)
        JTabbedPane sidebarTabs = new JTabbedPane();
        sidebarTabs.setPreferredSize(new Dimension(360, 600));
        sidebarTabs.addTab("Emitter Properties", createPropertiesPanel());
        sidebarTabs.addTab("Presets Library (100)", createPresetsPanel());

        // Top Toolbar
        JToolBar toolBar = createToolBar();

        add(toolBar, BorderLayout.NORTH);
        add(sidebarTabs, BorderLayout.WEST);
        add(gdxViewport, BorderLayout.CENTER);

        refreshEmitterList();
        setupEvents();
        filterPresets();
    }

    public int getRenderedFrameCount() {
        return particleListener != null ? particleListener.getFrameCount() : 0;
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

        playBtn.addActionListener(e -> particleListener.start());
        pauseBtn.addActionListener(e -> particleListener.pauseSimulation());
        restartBtn.addActionListener(e -> particleListener.restart());
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

    private JPanel createPropertiesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(SciFiColors.BG_PANEL);

        emitterList.setBackground(SciFiColors.BG_DARK);
        emitterList.setForeground(SciFiColors.TEXT_PRIMARY);
        emitterList.setVisibleRowCount(4);
        panel.add(new JScrollPane(emitterList), BorderLayout.NORTH);

        // Property Controls Grid
        JPanel propsPanel = new JPanel(new GridBagLayout());
        propsPanel.setOpaque(false);
        propsPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
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

        // Particle Image Asset
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel imgP = new JPanel(new BorderLayout(4, 0));
        imgP.setOpaque(false);
        imagePathField = new JTextField();
        browseImageBtn = new JButton("...");
        browseImageBtn.setToolTipText("Browse texture in assets folder");
        browseImageBtn.setPreferredSize(new Dimension(32, 22));
        browseImageBtn.addActionListener(e -> browseAssetTexture());
        imgP.add(imagePathField, BorderLayout.CENTER);
        imgP.add(browseImageBtn, BorderLayout.EAST);
        propsPanel.add(imgP, gbc);
        row++;

        // Count Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        propsPanel.add(new JLabel("Max Count:"), gbc);
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
        propsPanel.add(new JLabel("Life Min/Max:"), gbc);
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
        propsPanel.add(new JLabel("Scale Min/Max:"), gbc);
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

    private JPanel createPresetsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(SciFiColors.BG_PANEL);
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Filters
        JPanel topFilters = new JPanel(new GridLayout(2, 1, 4, 4));
        topFilters.setOpaque(false);

        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.addAll(ParticlePresetsLibrary.getCategories());
        categoryCombo = new JComboBox<>(categories.toArray(new String[0]));
        categoryCombo.addActionListener(e -> filterPresets());

        presetSearchField = new JTextField();
        presetSearchField.putClientProperty("JTextField.placeholderText", "Search 100 presets (#fire, #magic, #scifi)...");
        presetSearchField.addActionListener(e -> filterPresets());

        topFilters.add(categoryCombo);
        topFilters.add(presetSearchField);
        panel.add(topFilters, BorderLayout.NORTH);

        // Preset List
        presetJList = new JList<>(presetListModel);
        presetJList.setBackground(SciFiColors.BG_DARK);
        presetJList.setForeground(SciFiColors.TEXT_PRIMARY);
        presetJList.setCellRenderer(new PresetListRenderer());
        panel.add(new JScrollPane(presetJList), BorderLayout.CENTER);

        // Bottom Apply Button
        JButton applyPresetBtn = new JButton("Apply Preset to Effect");
        applyPresetBtn.setBackground(SciFiColors.ACCENT_CYAN);
        applyPresetBtn.setForeground(java.awt.Color.BLACK);
        applyPresetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyPresetBtn.addActionListener(e -> {
            ParticlePreset preset = presetJList.getSelectedValue();
            if (preset != null) {
                applyPreset(preset);
            }
        });
        panel.add(applyPresetBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void filterPresets() {
        String category = (String) categoryCombo.getSelectedItem();
        String query = presetSearchField.getText().trim();
        List<ParticlePreset> results = ParticlePresetsLibrary.search(category, query);
        presetListModel.clear();
        for (ParticlePreset p : results) {
            presetListModel.addElement(p);
        }
        if (!presetListModel.isEmpty()) {
            presetJList.setSelectedIndex(0);
        }
    }

    private void applyPreset(ParticlePreset preset) {
        Particle2DEffectModel newEffect = preset.createEffect();
        effectModel.getEmitters().clear();
        for (Particle2DEmitterModel e : newEffect.getEmitters()) {
            effectModel.addEmitter(e);
        }
        refreshEmitterList();
        particleListener.restart();
    }

    private void browseAssetTexture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Particle Texture from Assets Folder");
        // Look into workspace assets folder if available
        File assetsDir = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets");
        if (assetsDir.exists()) {
            chooser.setCurrentDirectory(assetsDir);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            imagePathField.setText(selectedFile.getName());
            Particle2DEmitterModel emitter = getSelectedEmitter();
            if (emitter != null) {
                emitter.setImagePath(selectedFile.getName());
                particleListener.loadCustomTexture(selectedFile);
            }
        }
    }

    private void setupEvents() {
        emitterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmitter();
            }
        });

        nameField.addActionListener(e -> syncToModel());
        imagePathField.addActionListener(e -> syncToModel());
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
        imagePathField.setText(emitter.getImagePath() != null ? emitter.getImagePath() : "");
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
        emitter.setImagePath(imagePathField.getText().trim());
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
     * Custom List Cell Renderer for Particle Presets with Tags and Category.
     */
    private static class PresetListRenderer extends JPanel implements ListCellRenderer<ParticlePreset> {
        private final JLabel nameLabel = new JLabel();
        private final JLabel categoryLabel = new JLabel();
        private final JLabel tagsLabel = new JLabel();

        public PresetListRenderer() {
            setLayout(new GridLayout(3, 1, 2, 2));
            setBorder(new EmptyBorder(4, 6, 4, 6));
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            tagsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            add(nameLabel);
            add(categoryLabel);
            add(tagsLabel);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ParticlePreset> list, ParticlePreset value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                nameLabel.setText(value.getName());
                categoryLabel.setText("Category: " + value.getCategory());
                tagsLabel.setText("Tags: #" + String.join(" #", value.getTags()));
            }

            if (isSelected) {
                setBackground(SciFiColors.BG_CARD);
                nameLabel.setForeground(SciFiColors.ACCENT_CYAN);
                categoryLabel.setForeground(SciFiColors.TEXT_PRIMARY);
                tagsLabel.setForeground(SciFiColors.TEXT_SECONDARY);
            } else {
                setBackground(SciFiColors.BG_DARK);
                nameLabel.setForeground(SciFiColors.TEXT_PRIMARY);
                categoryLabel.setForeground(SciFiColors.TEXT_MUTED);
                tagsLabel.setForeground(SciFiColors.TEXT_MUTED);
            }
            return this;
        }
    }

    /**
     * Real Hardware LibGDX ApplicationListener rendering particles via SpriteBatch and OpenGL.
     */
    public static class ParticleApplicationListener implements ApplicationListener {
        private final Particle2DEffectModel effectModel;
        private final List<GdxParticle> particles = new ArrayList<>();
        private final Random random = new Random();
        private SpriteBatch batch;
        private Texture particleTexture;
        private Texture customTexture;
        private File pendingTextureFile;
        private OrthographicCamera camera;
        private boolean running = true;
        private float emitterX = 0f;
        private float emitterY = 0f;

        public ParticleApplicationListener(Particle2DEffectModel effectModel) {
            this.effectModel = effectModel;
        }

        public void start() { running = true; }
        public void pauseSimulation() { running = false; }
        public void restart() {
            particles.clear();
            emitterX = 0f;
            emitterY = 0f;
            spawnBurst(40);
        }

        public void loadCustomTexture(File file) {
            this.pendingTextureFile = file;
        }

        public void setEmitterPosition(float x, float y) {
            this.emitterX = x;
            this.emitterY = y;
        }

        @Override
        public void create() {
            batch = new SpriteBatch();
            camera = new OrthographicCamera(800, 600);
            camera.position.set(0, 0, 0);
            camera.update();

            // Generate circular soft glow particle texture procedurally
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 0, 0, 0);
            pixmap.fill();
            for (int r = 16; r > 0; r--) {
                float alpha = (float) r / 16f;
                pixmap.setColor(1f, 1f, 1f, alpha * alpha);
                pixmap.fillCircle(16, 16, r);
            }
            particleTexture = new Texture(pixmap);
            pixmap.dispose();

            spawnBurst(50);
        }

        private void spawnBurst(int count) {
            for (int i = 0; i < count; i++) {
                GdxParticle p = new GdxParticle();
                p.x = emitterX;
                p.y = emitterY;
                float angleDeg = random.nextFloat() * 360f;
                float angleRad = (float) Math.toRadians(angleDeg);
                float speed = 50f + random.nextFloat() * 150f;
                p.vx = (float) Math.cos(angleRad) * speed;
                p.vy = (float) Math.sin(angleRad) * speed;
                p.totalLife = 0.5f + random.nextFloat() * 1.0f;
                p.currentLife = p.totalLife;
                p.scaleMin = 10f;
                p.scaleMax = 30f;
                p.additive = true;
                particles.add(p);
            }
        }

        @Override
        public void resize(int width, int height) {
            if (width <= 0 || height <= 0) return;
            if (camera != null) {
                camera.viewportWidth = width;
                camera.viewportHeight = height;
                camera.position.set(0, 0, 0);
                camera.update();
            }
        }

        private int frameCount = 0;

        public int getFrameCount() {
            return frameCount;
        }

        @Override
        public void render() {
            frameCount++;
            // Load pending custom texture on GL thread
            if (pendingTextureFile != null && pendingTextureFile.exists()) {
                try {
                    if (customTexture != null) customTexture.dispose();
                    customTexture = new Texture(new FileHandle(pendingTextureFile));
                } catch (Throwable t) {
                    System.err.println("Failed to load particle texture: " + t.getMessage());
                }
                pendingTextureFile = null;
            }

            // Dark Sci-Fi background
            ScreenUtils.clear(0.04f, 0.06f, 0.10f, 1f);

            float delta = Gdx.graphics.getDeltaTime();
            if (running && delta > 0) {
                updateSimulation(Math.min(delta, 0.05f));
            }

            if (batch == null || camera == null || particleTexture == null) return;

            camera.update();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            Texture activeTex = (customTexture != null) ? customTexture : particleTexture;

            for (GdxParticle p : particles) {
                float lifePercent = Math.max(0f, Math.min(1f, p.currentLife / p.totalLife));
                float currentScale = p.scaleMin + (p.scaleMax - p.scaleMin) * (1f - lifePercent);
                float alpha = lifePercent;

                if (p.additive) {
                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                } else {
                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                }

                batch.setColor(0f, 0.94f, 1f, alpha);
                batch.draw(activeTex, p.x - currentScale / 2f, p.y - currentScale / 2f, currentScale, currentScale);
            }

            // Draw emitter center crosshair
            batch.setColor(1f, 1f, 0f, 0.8f);
            batch.draw(activeTex, emitterX - 4, emitterY - 4, 8, 8);

            batch.end();

            // Capture exact GPU backbuffer image at frame 30
            if (frameCount == 30 && Gdx.graphics.getWidth() > 0 && Gdx.graphics.getHeight() > 0) {
                try {
                    int w = Gdx.graphics.getWidth();
                    int h = Gdx.graphics.getHeight();
                    byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, w, h, true);
                    java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < h; y++) {
                        for (int x = 0; x < w; x++) {
                            int idx = (y * w + x) * 4;
                            int r = pixels[idx] & 0xFF;
                            int g = pixels[idx + 1] & 0xFF;
                            int b = pixels[idx + 2] & 0xFF;
                            int a = pixels[idx + 3] & 0xFF;
                            img.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                        }
                    }
                    javax.imageio.ImageIO.write(img, "png", new java.io.File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/particle_editor_opengl_gpu_proof.png"));
                    System.out.println(">>> SAVED GPU BACKBUFFER CAPTURE (" + w + "x" + h + ") to particle_editor_opengl_gpu_proof.png <<<");
                } catch (Throwable t) {
                    System.err.println("GPU capture error: " + t.getMessage());
                }
            }
        }

        private void updateSimulation(float delta) {
            for (Particle2DEmitterModel emitter : effectModel.getEmitters()) {
                if (!emitter.isEnabled()) continue;

                int spawnCount = Math.max(1, (int) (emitter.getEmissionRate() * delta));
                for (int i = 0; i < spawnCount; i++) {
                    if (particles.size() >= emitter.getMaxParticleCount()) break;

                    GdxParticle p = new GdxParticle();
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

                    particles.add(p);
                }
            }

            Iterator<GdxParticle> it = particles.iterator();
            while (it.hasNext()) {
                GdxParticle p = it.next();
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

        @Override public void pause() {}
        @Override public void resume() {}

        @Override
        public void dispose() {
            if (batch != null) batch.dispose();
            if (particleTexture != null) particleTexture.dispose();
            if (customTexture != null) customTexture.dispose();
        }

        private static class GdxParticle {
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
