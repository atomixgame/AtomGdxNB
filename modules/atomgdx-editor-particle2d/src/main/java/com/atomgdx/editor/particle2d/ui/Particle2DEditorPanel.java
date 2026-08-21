package com.atomgdx.editor.particle2d.ui;

import com.atomgdx.core.viewport.GdxAwtViewport;
import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.Particle2DEmitterModel;
import com.atomgdx.editor.particle2d.presets.ParticlePreset;
import com.atomgdx.editor.particle2d.presets.ParticlePresetsLibrary;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Modern Dark Theme 2D Particle Editor Panel powered by native LibGDX LwjglAWTCanvas OpenGL viewport.
 * Features 100 categorized and tagged presets, asset texture browser, Fatcow UI icons, and interactive canvas.
 */
public class Particle2DEditorPanel extends JPanel {

    public static class DarkThemeColors {
        public static final Color BG_WINDOW = new Color(30, 31, 34);           // #1e1f22
        public static final Color BG_PANEL = new Color(43, 45, 48);            // #2b2d30
        public static final Color BG_DARK = new Color(30, 31, 34);             // #1e1f22
        public static final Color BG_ITEM_ALT = new Color(38, 40, 44);         // #26282c
        public static final Color ACCENT_PRIMARY = new Color(53, 116, 240);    // #3574f0 (Clean IDE Blue)
        public static final Color TEXT_PRIMARY = new Color(223, 225, 229);     // #dfe1e5
        public static final Color TEXT_SECONDARY = new Color(154, 160, 166);   // #9aa0a6
        public static final Color TEXT_MUTED = new Color(110, 118, 129);       // #6e7681
        public static final Color BORDER = new Color(60, 63, 65);              // #3c3f41
    }

    private final Particle2DEffectModel effectModel;
    private final GdxAwtViewport gdxViewport;
    private final ParticleApplicationListener particleListener;

    // UI Components
    private final DefaultListModel<String> emitterListModel = new DefaultListModel<>();
    private final JList<String> emitterList = new JList<>(emitterListModel);

    private JTextField nameField;
    private JTextField imagePathField;
    private JButton browseImageBtn;
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

    // Presets Library UI
    private JComboBox<String> categoryCombo;
    private JTextField presetSearchField;
    private final DefaultListModel<ParticlePreset> presetListModel = new DefaultListModel<>();
    private JList<ParticlePreset> presetJList;

    private boolean updatingUI = false;

    public Particle2DEditorPanel(Particle2DEffectModel effectModel) {
        this.effectModel = effectModel != null ? effectModel : new Particle2DEffectModel("New Effect");
        if (this.effectModel.getEmitters().isEmpty()) {
            this.effectModel.addEmitter(new Particle2DEmitterModel("Default Emitter"));
        }

        setLayout(new BorderLayout(8, 8));
        setBackground(DarkThemeColors.BG_WINDOW);
        setBorder(new EmptyBorder(6, 6, 6, 6));

        // Center Viewport powered by real LibGDX LwjglAWTCanvas
        particleListener = new ParticleApplicationListener(this.effectModel);
        gdxViewport = new GdxAwtViewport(particleListener);
        gdxViewport.setBorder(new LineBorder(DarkThemeColors.BORDER, 1));

        if (gdxViewport.getCanvas() != null) {
            Canvas awtCanvas = gdxViewport.getCanvas().getCanvas();
            awtCanvas.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    float cx = awtCanvas.getWidth() / 2f;
                    float cy = awtCanvas.getHeight() / 2f;
                    particleListener.setEmitterPosition(e.getX() - cx, -(e.getY() - cy));
                }
            });
        }

        // Sidebar Tabs (Properties & 100 Presets Library)
        JTabbedPane sidebarTabs = new JTabbedPane();
        sidebarTabs.setBackground(DarkThemeColors.BG_PANEL);
        sidebarTabs.setForeground(DarkThemeColors.TEXT_PRIMARY);
        sidebarTabs.setPreferredSize(new Dimension(380, 600));
        sidebarTabs.addTab("Emitter Properties", getIcon("cog.png"), createPropertiesPanel());
        sidebarTabs.addTab("Presets Library (100)", getIcon("star.png"), createPresetsPanel());

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

    public static ImageIcon getIcon(String name) {
        try {
            URL url = Particle2DEditorPanel.class.getResource("icons/" + name);
            if (url == null) {
                url = Particle2DEditorPanel.class.getResource("/com/atomgdx/editor/particle2d/ui/icons/" + name);
            }
            if (url == null) {
                url = Particle2DEditorPanel.class.getResource("/com/atomgdx/editor/particle2d/icons/" + name);
            }
            if (url == null) {
                url = Particle2DEditorPanel.class.getClassLoader().getResource("com/atomgdx/editor/particle2d/icons/" + name);
            }
            if (url != null) {
                return new ImageIcon(url);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(DarkThemeColors.BG_PANEL);
        tb.setBorder(new LineBorder(DarkThemeColors.BORDER, 1));

        JButton playBtn = new JButton("Play", getIcon("control_play_blue.png"));
        JButton pauseBtn = new JButton("Pause", getIcon("control_pause_blue.png"));
        JButton restartBtn = new JButton("Restart", getIcon("arrow_refresh.png"));
        JButton addEmitterBtn = new JButton("Add Emitter", getIcon("add.png"));
        JButton deleteEmitterBtn = new JButton("Delete Emitter", getIcon("delete.png"));

        for (JButton b : new JButton[]{playBtn, pauseBtn, restartBtn, addEmitterBtn, deleteEmitterBtn}) {
            b.setBackground(DarkThemeColors.BG_PANEL);
            b.setForeground(DarkThemeColors.TEXT_PRIMARY);
            b.setFocusPainted(false);
        }

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
                Particle2DEmitterModel toRemove = effectModel.getEmitters().get(selected);
                effectModel.removeEmitter(toRemove);
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
        panel.setBackground(DarkThemeColors.BG_PANEL);
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        emitterList.setBackground(DarkThemeColors.BG_DARK);
        emitterList.setForeground(DarkThemeColors.TEXT_PRIMARY);
        emitterList.setSelectionBackground(DarkThemeColors.ACCENT_PRIMARY);
        emitterList.setSelectionForeground(java.awt.Color.WHITE);
        emitterList.setVisibleRowCount(4);
        panel.add(new JScrollPane(emitterList), BorderLayout.NORTH);

        // Property Controls Grid
        JPanel propsPanel = new JPanel(new GridBagLayout());
        propsPanel.setOpaque(false);
        propsPanel.setBorder(new EmptyBorder(6, 4, 6, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        int row = 0;

        // Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel nameLbl = new JLabel("Name:");
        nameLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(nameLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        nameField = new JTextField();
        nameField.setBackground(DarkThemeColors.BG_DARK);
        nameField.setForeground(DarkThemeColors.TEXT_PRIMARY);
        nameField.setCaretColor(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(nameField, gbc);
        row++;

        // Particle Image Asset
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel imgLbl = new JLabel("Image:");
        imgLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(imgLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel imgP = new JPanel(new BorderLayout(4, 0));
        imgP.setOpaque(false);
        imagePathField = new JTextField();
        imagePathField.setBackground(DarkThemeColors.BG_DARK);
        imagePathField.setForeground(DarkThemeColors.TEXT_PRIMARY);
        imagePathField.setCaretColor(DarkThemeColors.TEXT_PRIMARY);
        browseImageBtn = new JButton(getIcon("picture.png"));
        browseImageBtn.setToolTipText("Browse texture in assets folder");
        browseImageBtn.setPreferredSize(new Dimension(28, 22));
        browseImageBtn.setBackground(DarkThemeColors.BG_PANEL);
        browseImageBtn.addActionListener(e -> browseAssetTexture());
        imgP.add(imagePathField, BorderLayout.CENTER);
        imgP.add(browseImageBtn, BorderLayout.EAST);
        propsPanel.add(imgP, gbc);
        row++;

        // Count Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel countLbl = new JLabel("Max Count:");
        countLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(countLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        countSpinner = new JSpinner(new SpinnerNumberModel(200, 1, 10000, 10));
        propsPanel.add(countSpinner, gbc);
        row++;

        // Duration
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel durLbl = new JLabel("Duration (ms):");
        durLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(durLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        durationSpinner = new JSpinner(new SpinnerNumberModel(1000f, 50f, 60000f, 100f));
        propsPanel.add(durationSpinner, gbc);
        row++;

        // Emission Rate
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel emLbl = new JLabel("Emission Rate:");
        emLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(emLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        emissionSpinner = new JSpinner(new SpinnerNumberModel(50f, 1f, 1000f, 5f));
        propsPanel.add(emissionSpinner, gbc);
        row++;

        // Life Min/Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lifeLbl = new JLabel("Life Min/Max:");
        lifeLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(lifeLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel lifeP = new JPanel(new GridLayout(1, 2, 4, 0));
        lifeP.setOpaque(false);
        lifeMinSpinner = new JSpinner(new SpinnerNumberModel(500f, 10f, 20000f, 50f));
        lifeMaxSpinner = new JSpinner(new SpinnerNumberModel(1000f, 10f, 20000f, 50f));
        lifeP.add(lifeMinSpinner);
        lifeP.add(lifeMaxSpinner);
        propsPanel.add(lifeP, gbc);
        row++;

        // Scale Min/Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel scaleLbl = new JLabel("Scale Min/Max:");
        scaleLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(scaleLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel scaleP = new JPanel(new GridLayout(1, 2, 4, 0));
        scaleP.setOpaque(false);
        scaleMinSpinner = new JSpinner(new SpinnerNumberModel(10f, 1f, 500f, 2f));
        scaleMaxSpinner = new JSpinner(new SpinnerNumberModel(30f, 1f, 500f, 2f));
        scaleP.add(scaleMinSpinner);
        scaleP.add(scaleMaxSpinner);
        propsPanel.add(scaleP, gbc);
        row++;

        // Velocity Min/Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel velLbl = new JLabel("Velocity Min/Max:");
        velLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(velLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel velP = new JPanel(new GridLayout(1, 2, 4, 0));
        velP.setOpaque(false);
        velMinSpinner = new JSpinner(new SpinnerNumberModel(50f, 0f, 2000f, 10f));
        velMaxSpinner = new JSpinner(new SpinnerNumberModel(150f, 0f, 2000f, 10f));
        velP.add(velMinSpinner);
        velP.add(velMaxSpinner);
        propsPanel.add(velP, gbc);
        row++;

        // Angle Min/Max
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel angLbl = new JLabel("Angle Min/Max:");
        angLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(angLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel angP = new JPanel(new GridLayout(1, 2, 4, 0));
        angP.setOpaque(false);
        angleMinSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 360f, 15f));
        angleMaxSpinner = new JSpinner(new SpinnerNumberModel(360f, 0f, 360f, 15f));
        angP.add(angleMinSpinner);
        angP.add(angleMaxSpinner);
        propsPanel.add(angP, gbc);
        row++;

        // Wind / Gravity
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel wgLbl = new JLabel("Wind / Gravity:");
        wgLbl.setForeground(DarkThemeColors.TEXT_PRIMARY);
        propsPanel.add(wgLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel wgP = new JPanel(new GridLayout(1, 2, 4, 0));
        wgP.setOpaque(false);
        windSpinner = new JSpinner(new SpinnerNumberModel(0f, -500f, 500f, 10f));
        gravitySpinner = new JSpinner(new SpinnerNumberModel(0f, -500f, 500f, 10f));
        wgP.add(windSpinner);
        wgP.add(gravitySpinner);
        propsPanel.add(wgP, gbc);
        row++;

        // Checkboxes
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel checkP = new JPanel(new GridLayout(2, 2, 4, 4));
        checkP.setOpaque(false);
        additiveBox = new JCheckBox("Additive", true);
        continuousBox = new JCheckBox("Continuous", true);
        attachedBox = new JCheckBox("Attached", false);
        behindBox = new JCheckBox("Behind", false);
        for (JCheckBox cb : new JCheckBox[]{additiveBox, continuousBox, attachedBox, behindBox}) {
            cb.setOpaque(false);
            cb.setForeground(DarkThemeColors.TEXT_PRIMARY);
        }
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
        panel.setBackground(DarkThemeColors.BG_PANEL);
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Filters
        JPanel topFilters = new JPanel(new GridLayout(2, 1, 4, 4));
        topFilters.setOpaque(false);

        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.addAll(ParticlePresetsLibrary.getCategories());
        categoryCombo = new JComboBox<>(categories.toArray(new String[0]));
        categoryCombo.setBackground(DarkThemeColors.BG_DARK);
        categoryCombo.setForeground(DarkThemeColors.TEXT_PRIMARY);
        categoryCombo.setRenderer(new CategoryComboRenderer());
        categoryCombo.addActionListener(e -> filterPresets());

        presetSearchField = new JTextField();
        presetSearchField.setBackground(DarkThemeColors.BG_DARK);
        presetSearchField.setForeground(DarkThemeColors.TEXT_PRIMARY);
        presetSearchField.setCaretColor(DarkThemeColors.TEXT_PRIMARY);
        presetSearchField.putClientProperty("JTextField.placeholderText", "Search 100 presets (#fire, #magic, #scifi)...");
        presetSearchField.addActionListener(e -> filterPresets());

        JPanel searchP = new JPanel(new BorderLayout(4, 0));
        searchP.setOpaque(false);
        JLabel searchIconLabel = new JLabel(getIcon("magnifier.png"));
        searchP.add(searchIconLabel, BorderLayout.WEST);
        searchP.add(presetSearchField, BorderLayout.CENTER);

        topFilters.add(categoryCombo);
        topFilters.add(searchP);
        panel.add(topFilters, BorderLayout.NORTH);

        // Preset List
        presetJList = new JList<>(presetListModel);
        presetJList.setBackground(DarkThemeColors.BG_DARK);
        presetJList.setForeground(DarkThemeColors.TEXT_PRIMARY);
        presetJList.setCellRenderer(new PresetListRenderer());
        presetJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ParticlePreset preset = presetJList.getSelectedValue();
                    if (preset != null) {
                        applyPreset(preset);
                    }
                }
            }
        });
        panel.add(new JScrollPane(presetJList), BorderLayout.CENTER);

        // Bottom Apply Button
        JButton applyPresetBtn = new JButton("Apply Preset to Effect", getIcon("wand.png"));
        applyPresetBtn.setBackground(DarkThemeColors.ACCENT_PRIMARY);
        applyPresetBtn.setForeground(java.awt.Color.WHITE);
        applyPresetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyPresetBtn.setFocusPainted(false);
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

    public void applyPreset(ParticlePreset preset) {
        if (preset == null) return;
        Particle2DEffectModel newEffect = preset.createEffect();
        effectModel.setEmitters(newEffect.getEmitters());
        refreshEmitterList();
        particleListener.restart();
    }

    private void browseAssetTexture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Particle Texture from Assets Folder");
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

    private static Icon getCategoryIcon(String category) {
        if (category == null) return getIcon("star.png");
        switch (category) {
            case "Sci-Fi & Energy": return getIcon("lightning.png");
            case "Combat & Weapons": return getIcon("bomb.png");
            case "Magic & Fantasy": return getIcon("wand.png");
            case "Nature & Weather": return getIcon("weather_rain.png");
            case "Explosions & Impacts": return getIcon("fire.png");
            case "Atmospheric & Ambient": return getIcon("weather_sun.png");
            default: return getIcon("star.png");
        }
    }

    /**
     * Category dropdown combo renderer with icons.
     */
    private static class CategoryComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                String cat = value.toString();
                label.setText(cat);
                label.setIcon(getCategoryIcon(cat));
            }
            if (isSelected) {
                label.setBackground(DarkThemeColors.ACCENT_PRIMARY);
                label.setForeground(java.awt.Color.WHITE);
            } else {
                label.setBackground(DarkThemeColors.BG_DARK);
                label.setForeground(DarkThemeColors.TEXT_PRIMARY);
            }
            return label;
        }
    }

    /**
     * Custom List Cell Renderer for Particle Presets with Tags, Category, and Icons in Dark Theme.
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
            tagsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            add(nameLabel);
            add(categoryLabel);
            add(tagsLabel);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ParticlePreset> list, ParticlePreset value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                nameLabel.setText(value.getName());
                nameLabel.setIcon(getCategoryIcon(value.getCategory()));
                categoryLabel.setText("Category: " + value.getCategory());
                tagsLabel.setText(String.join(" ", value.getTags()));
            }

            if (isSelected) {
                setBackground(DarkThemeColors.ACCENT_PRIMARY);
                nameLabel.setForeground(java.awt.Color.WHITE);
                categoryLabel.setForeground(new java.awt.Color(210, 230, 255));
                tagsLabel.setForeground(new java.awt.Color(200, 220, 245));
            } else {
                setBackground(index % 2 == 0 ? DarkThemeColors.BG_DARK : DarkThemeColors.BG_ITEM_ALT);
                nameLabel.setForeground(DarkThemeColors.TEXT_PRIMARY);
                categoryLabel.setForeground(DarkThemeColors.TEXT_SECONDARY);
                tagsLabel.setForeground(DarkThemeColors.TEXT_MUTED);
            }
            return this;
        }
    }

    /**
     * Real-time hardware-accelerated LibGDX ApplicationListener for particle rendering.
     */
    private static class ParticleApplicationListener implements ApplicationListener {
        private final Particle2DEffectModel effectModel;
        private final List<GdxParticle> particles = new ArrayList<>();
        private final Random random = new Random();

        private SpriteBatch batch;
        private OrthographicCamera camera;
        private Texture particleTexture;
        private Texture customTexture;
        private File pendingTextureFile;

        private float emitterX = 0f;
        private float emitterY = 0f;
        private boolean running = true;

        public ParticleApplicationListener(Particle2DEffectModel effectModel) {
            this.effectModel = effectModel;
        }

        public void start() { running = true; }
        public void pauseSimulation() { running = false; }
        public void restart() {
            particles.clear();
            emitterX = 0f;
            emitterY = 0f;
            spawnBurst(50);
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
            for (Particle2DEmitterModel emitter : effectModel.getEmitters()) {
                if (!emitter.isEnabled()) continue;
                for (int i = 0; i < count; i++) {
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

            // Dark neutral background
            ScreenUtils.clear(0.12f, 0.12f, 0.13f, 1f);

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
                batch.draw(
                        activeTex,
                        p.x - currentScale / 2f,
                        p.y - currentScale / 2f,
                        currentScale,
                        currentScale
                );
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
                    byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, w, h, false);
                    java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < h; y++) {
                        int srcY = h - 1 - y;
                        for (int x = 0; x < w; x++) {
                            int idx = (srcY * w + x) * 4;
                            int r = pixels[idx] & 0xFF;
                            int g = pixels[idx + 1] & 0xFF;
                            int b = pixels[idx + 2] & 0xFF;
                            int a = pixels[idx + 3] & 0xFF;
                            img.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                        }
                    }
                    javax.imageio.ImageIO.write(img, "png", new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/particle_editor_opengl_gpu_proof.png"));
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
