package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.editor.scene2d.data.vo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Modern Dark Theme property inspector for HyperLap2D Scene Items.
 */
public class SceneItemInspectorPanel extends JPanel {

    private static class DarkColors {
        public static final Color BG_PANEL = new Color(43, 45, 48);
        public static final Color BG_DARK = new Color(30, 31, 34);
        public static final Color TEXT_PRIMARY = new Color(223, 225, 229);
        public static final Color TEXT_MUTED = new Color(139, 148, 158);
        public static final Color BORDER = new Color(60, 63, 65);
    }

    private final SceneVO scene;
    private MainItemVO currentItem;
    private boolean updating = false;
    private Runnable changeListener;

    // Transform fields
    private JTextField nameField;
    private JComboBox<String> layerCombo;
    private JSpinner posXSpinner;
    private JSpinner posYSpinner;
    private JSpinner scaleXSpinner;
    private JSpinner scaleYSpinner;
    private JSpinner rotationSpinner;
    private JSpinner originXSpinner;
    private JSpinner originYSpinner;
    private JSpinner zIndexSpinner;
    private JCheckBox visibleBox;
    private JCheckBox lockedBox;

    // Physics fields
    private JCheckBox physicsEnabledBox;
    private JComboBox<String> bodyTypeCombo;
    private JSpinner densitySpinner;
    private JSpinner frictionSpinner;
    private JSpinner restitutionSpinner;
    private JCheckBox sensorBox;
    private JCheckBox bulletBox;

    public SceneItemInspectorPanel(SceneVO scene) {
        this.scene = scene;

        setLayout(new BorderLayout(4, 4));
        setBackground(DarkColors.BG_PANEL);
        setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(DarkColors.BG_PANEL);

        content.add(createTransformSection());
        content.add(Box.createVerticalStrut(6));
        content.add(createPhysicsSection());

        add(new JScrollPane(content), BorderLayout.CENTER);
        setItem(null);
    }

    public void setChangeListener(Runnable listener) {
        this.changeListener = listener;
    }

    public void setItem(MainItemVO item) {
        this.currentItem = item;
        updating = true;

        if (item == null) {
            nameField.setText("");
            nameField.setEnabled(false);
            layerCombo.setEnabled(false);
            posXSpinner.setEnabled(false);
            posYSpinner.setEnabled(false);
            scaleXSpinner.setEnabled(false);
            scaleYSpinner.setEnabled(false);
            rotationSpinner.setEnabled(false);
            originXSpinner.setEnabled(false);
            originYSpinner.setEnabled(false);
            zIndexSpinner.setEnabled(false);
            visibleBox.setEnabled(false);
            lockedBox.setEnabled(false);
            physicsEnabledBox.setEnabled(false);
            setPhysicsControlsEnabled(false);
        } else {
            nameField.setEnabled(true);
            layerCombo.setEnabled(true);
            posXSpinner.setEnabled(true);
            posYSpinner.setEnabled(true);
            scaleXSpinner.setEnabled(true);
            scaleYSpinner.setEnabled(true);
            rotationSpinner.setEnabled(true);
            originXSpinner.setEnabled(true);
            originYSpinner.setEnabled(true);
            zIndexSpinner.setEnabled(true);
            visibleBox.setEnabled(true);
            lockedBox.setEnabled(true);
            physicsEnabledBox.setEnabled(true);

            nameField.setText(item.itemName != null ? item.itemName : "");
            updateLayerCombo();
            layerCombo.setSelectedItem(item.layerName);

            posXSpinner.setValue(item.x);
            posYSpinner.setValue(item.y);
            scaleXSpinner.setValue(item.scaleX);
            scaleYSpinner.setValue(item.scaleY);
            rotationSpinner.setValue(item.rotation);
            originXSpinner.setValue(item.originX);
            originYSpinner.setValue(item.originY);
            zIndexSpinner.setValue(item.zIndex);
            visibleBox.setSelected(item.isVisible);
            lockedBox.setSelected(item.isLocked);

            if (item.physics != null) {
                physicsEnabledBox.setSelected(true);
                setPhysicsControlsEnabled(true);
                bodyTypeCombo.setSelectedIndex(Math.max(0, Math.min(2, item.physics.bodyType)));
                densitySpinner.setValue(item.physics.density);
                frictionSpinner.setValue(item.physics.friction);
                restitutionSpinner.setValue(item.physics.restitution);
                sensorBox.setSelected(item.physics.sensor);
                bulletBox.setSelected(item.physics.bullet);
            } else {
                physicsEnabledBox.setSelected(false);
                setPhysicsControlsEnabled(false);
            }
        }

        updating = false;
    }

    private void updateLayerCombo() {
        layerCombo.removeAllItems();
        if (scene != null) {
            for (LayerItemVO l : scene.composite.layers) {
                layerCombo.addItem(l.layerName);
            }
        }
    }

    private JPanel createTransformSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(DarkColors.BG_PANEL);
        TitledBorder tb = BorderFactory.createTitledBorder(new LineBorder(DarkColors.BORDER, 1), "Transform & Identity");
        tb.setTitleColor(DarkColors.TEXT_PRIMARY);
        p.setBorder(tb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 4, 2, 4);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        nameField = new JTextField();
        nameField.setBackground(DarkColors.BG_DARK);
        nameField.setForeground(DarkColors.TEXT_PRIMARY);
        nameField.addActionListener(e -> syncToItem());
        p.add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Layer:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        layerCombo = new JComboBox<>();
        layerCombo.setBackground(DarkColors.BG_DARK);
        layerCombo.setForeground(DarkColors.TEXT_PRIMARY);
        layerCombo.addActionListener(e -> syncToItem());
        p.add(layerCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Position X/Y:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel posP = new JPanel(new GridLayout(1, 2, 4, 0));
        posP.setOpaque(false);
        posXSpinner = new JSpinner(new SpinnerNumberModel(0f, -100000f, 100000f, 10f));
        posYSpinner = new JSpinner(new SpinnerNumberModel(0f, -100000f, 100000f, 10f));
        posXSpinner.addChangeListener(e -> syncToItem());
        posYSpinner.addChangeListener(e -> syncToItem());
        posP.add(posXSpinner);
        posP.add(posYSpinner);
        p.add(posP, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Scale X/Y:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel scaleP = new JPanel(new GridLayout(1, 2, 4, 0));
        scaleP.setOpaque(false);
        scaleXSpinner = new JSpinner(new SpinnerNumberModel(1f, 0.01f, 100f, 0.1f));
        scaleYSpinner = new JSpinner(new SpinnerNumberModel(1f, 0.01f, 100f, 0.1f));
        scaleXSpinner.addChangeListener(e -> syncToItem());
        scaleYSpinner.addChangeListener(e -> syncToItem());
        scaleP.add(scaleXSpinner);
        scaleP.add(scaleYSpinner);
        p.add(scaleP, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Rotation:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        rotationSpinner = new JSpinner(new SpinnerNumberModel(0f, -3600f, 3600f, 15f));
        rotationSpinner.addChangeListener(e -> syncToItem());
        p.add(rotationSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Origin X/Y:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel originP = new JPanel(new GridLayout(1, 2, 4, 0));
        originP.setOpaque(false);
        originXSpinner = new JSpinner(new SpinnerNumberModel(0f, -1000f, 1000f, 5f));
        originYSpinner = new JSpinner(new SpinnerNumberModel(0f, -1000f, 1000f, 5f));
        originXSpinner.addChangeListener(e -> syncToItem());
        originYSpinner.addChangeListener(e -> syncToItem());
        originP.add(originXSpinner);
        originP.add(originYSpinner);
        p.add(originP, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Z-Index:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        zIndexSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        zIndexSpinner.addChangeListener(e -> syncToItem());
        p.add(zIndexSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel checkP = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        checkP.setOpaque(false);
        visibleBox = new JCheckBox("Visible", true);
        lockedBox = new JCheckBox("Locked", false);
        for (JCheckBox cb : new JCheckBox[]{visibleBox, lockedBox}) {
            cb.setOpaque(false);
            cb.setForeground(DarkColors.TEXT_PRIMARY);
            cb.addActionListener(e -> syncToItem());
            checkP.add(cb);
        }
        p.add(checkP, gbc);

        return p;
    }

    private JPanel createPhysicsSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(DarkColors.BG_PANEL);
        TitledBorder tb = BorderFactory.createTitledBorder(new LineBorder(DarkColors.BORDER, 1), "Box2D Physics");
        tb.setTitleColor(DarkColors.TEXT_PRIMARY);
        p.setBorder(tb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 4, 2, 4);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        physicsEnabledBox = new JCheckBox("Enable Physics Body");
        physicsEnabledBox.setOpaque(false);
        physicsEnabledBox.setForeground(DarkColors.TEXT_PRIMARY);
        physicsEnabledBox.addActionListener(e -> {
            boolean en = physicsEnabledBox.isSelected();
            setPhysicsControlsEnabled(en);
            if (currentItem != null) {
                if (en && currentItem.physics == null) {
                    currentItem.physics = new PhysicsBodyDataVO();
                } else if (!en) {
                    currentItem.physics = null;
                }
            }
            syncToItem();
        });
        p.add(physicsEnabledBox, gbc);
        row++;

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Body Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        bodyTypeCombo = new JComboBox<>(new String[]{"Static", "Kinematic", "Dynamic"});
        bodyTypeCombo.setBackground(DarkColors.BG_DARK);
        bodyTypeCombo.setForeground(DarkColors.TEXT_PRIMARY);
        bodyTypeCombo.addActionListener(e -> syncToItem());
        p.add(bodyTypeCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Density:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        densitySpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 1000f, 0.1f));
        densitySpinner.addChangeListener(e -> syncToItem());
        p.add(densitySpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel("Friction / Restitution:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel frP = new JPanel(new GridLayout(1, 2, 4, 0));
        frP.setOpaque(false);
        frictionSpinner = new JSpinner(new SpinnerNumberModel(0.2f, 0f, 10f, 0.05f));
        restitutionSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f, 0.05f));
        frictionSpinner.addChangeListener(e -> syncToItem());
        restitutionSpinner.addChangeListener(e -> syncToItem());
        frP.add(frictionSpinner);
        frP.add(restitutionSpinner);
        p.add(frP, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel checkP = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        checkP.setOpaque(false);
        sensorBox = new JCheckBox("Sensor", false);
        bulletBox = new JCheckBox("Bullet", false);
        for (JCheckBox cb : new JCheckBox[]{sensorBox, bulletBox}) {
            cb.setOpaque(false);
            cb.setForeground(DarkColors.TEXT_PRIMARY);
            cb.addActionListener(e -> syncToItem());
            checkP.add(cb);
        }
        p.add(checkP, gbc);

        return p;
    }

    private void setPhysicsControlsEnabled(boolean enabled) {
        bodyTypeCombo.setEnabled(enabled);
        densitySpinner.setEnabled(enabled);
        frictionSpinner.setEnabled(enabled);
        restitutionSpinner.setEnabled(enabled);
        sensorBox.setEnabled(enabled);
        bulletBox.setEnabled(enabled);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(DarkColors.TEXT_PRIMARY);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }

    private void syncToItem() {
        if (updating || currentItem == null) return;

        currentItem.itemName = nameField.getText().trim();
        if (layerCombo.getSelectedItem() != null) {
            currentItem.layerName = (String) layerCombo.getSelectedItem();
        }
        currentItem.x = ((Number) posXSpinner.getValue()).floatValue();
        currentItem.y = ((Number) posYSpinner.getValue()).floatValue();
        currentItem.scaleX = ((Number) scaleXSpinner.getValue()).floatValue();
        currentItem.scaleY = ((Number) scaleYSpinner.getValue()).floatValue();
        currentItem.rotation = ((Number) rotationSpinner.getValue()).floatValue();
        currentItem.originX = ((Number) originXSpinner.getValue()).floatValue();
        currentItem.originY = ((Number) originYSpinner.getValue()).floatValue();
        currentItem.isVisible = visibleBox.isSelected();
        currentItem.isLocked = lockedBox.isSelected();

        if (physicsEnabledBox.isSelected()) {
            if (currentItem.physics == null) {
                currentItem.physics = new PhysicsBodyDataVO();
            }
            currentItem.physics.bodyType = bodyTypeCombo.getSelectedIndex();
            currentItem.physics.density = ((Number) densitySpinner.getValue()).floatValue();
            currentItem.physics.friction = ((Number) frictionSpinner.getValue()).floatValue();
            currentItem.physics.restitution = ((Number) restitutionSpinner.getValue()).floatValue();
            currentItem.physics.sensor = sensorBox.isSelected();
            currentItem.physics.bullet = bulletBox.isSelected();
        } else {
            currentItem.physics = null;
        }

        if (changeListener != null) {
            changeListener.run();
        }
    }
}
