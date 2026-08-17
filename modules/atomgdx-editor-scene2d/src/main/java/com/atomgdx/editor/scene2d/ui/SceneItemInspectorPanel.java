package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.ui.DarkThemeUtils.CollapsibleSection;
import com.atomgdx.editor.scene2d.data.vo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Unity-style Collapsible Component Inspector for HyperLap2D Scene Items.
 * Features compact gap-free layout, foldout component headers, compact colored Vector2 inputs,
 * and a "+ Add Component" menu.
 */
public class SceneItemInspectorPanel extends JPanel {

    private final SceneVO scene;
    private MainItemVO currentItem;
    private boolean updating = false;
    private Runnable changeListener;

    private final JPanel componentsContainer = new JPanel();

    // Transform Inputs
    private JTextField nameField;
    private JComboBox<String> layerCombo;
    private JSpinner posXSpinner, posYSpinner;
    private JSpinner scaleXSpinner, scaleYSpinner;
    private JSpinner rotationSpinner;
    private JSpinner originXSpinner, originYSpinner;
    private JSpinner zIndexSpinner;
    private JCheckBox visibleBox, lockedBox;

    // Physics Inputs
    private JCheckBox physicsEnabledBox;
    private JComboBox<String> bodyTypeCombo;
    private JSpinner densitySpinner, frictionSpinner, restitutionSpinner;
    private JCheckBox sensorBox, bulletBox;
    private JPanel physicsBodyContent;
    private CollapsibleSection physicsSection;

    // Light Inputs
    private JComboBox<String> lightTypeCombo;
    private JSpinner raysSpinner, distanceSpinner;
    private JCheckBox softLightBox, xrayBox;
    private JPanel lightBodyContent;
    private CollapsibleSection lightSection;

    public SceneItemInspectorPanel(SceneVO scene) {
        this.scene = scene;

        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        componentsContainer.setLayout(new BoxLayout(componentsContainer, BoxLayout.Y_AXIS));
        componentsContainer.setBackground(DarkThemeUtils.BG_DARK);
        componentsContainer.setBorder(new EmptyBorder(2, 2, 2, 2));

        // 1. Transform Component (Always present)
        componentsContainer.add(createTransformSection());
        componentsContainer.add(Box.createVerticalStrut(2));

        // 2. Physics Component
        physicsSection = createPhysicsSection();
        componentsContainer.add(physicsSection);
        componentsContainer.add(Box.createVerticalStrut(2));

        // 3. Lighting Component
        lightSection = createLightSection();
        componentsContainer.add(lightSection);
        componentsContainer.add(Box.createVerticalStrut(4));

        // 4. "+ Add Component" Button
        JButton addComponentBtn = new JButton("+ Add Component");
        addComponentBtn.setBackground(DarkThemeUtils.BG_HEADER);
        addComponentBtn.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        addComponentBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addComponentBtn.setFocusPainted(false);
        addComponentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addComponentBtn.setMaximumSize(new Dimension(280, 24));
        addComponentBtn.addActionListener(e -> showAddComponentPopup(addComponentBtn));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(500, 28));
        btnPanel.add(addComponentBtn);
        componentsContainer.add(btnPanel);

        componentsContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(componentsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        setItem(null);
    }

    public void setChangeListener(Runnable listener) {
        this.changeListener = listener;
    }

    public void setItem(MainItemVO item) {
        this.currentItem = item;
        updating = true;

        if (item == null) {
            nameField.setText("None Selected");
            nameField.setEnabled(false);
            layerCombo.setEnabled(false);
            setControlsEnabled(false);
            physicsSection.setVisible(false);
            lightSection.setVisible(false);
        } else {
            nameField.setEnabled(true);
            layerCombo.setEnabled(true);
            setControlsEnabled(true);

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
                physicsSection.setVisible(true);
                physicsEnabledBox.setSelected(true);
                setPhysicsControlsEnabled(true);
                bodyTypeCombo.setSelectedIndex(Math.max(0, Math.min(2, item.physics.bodyType)));
                densitySpinner.setValue(item.physics.density);
                frictionSpinner.setValue(item.physics.friction);
                restitutionSpinner.setValue(item.physics.restitution);
                sensorBox.setSelected(item.physics.sensor);
                bulletBox.setSelected(item.physics.bullet);
            } else {
                physicsSection.setVisible(false);
                physicsEnabledBox.setSelected(false);
            }

            if (item instanceof LightVO) {
                LightVO lt = (LightVO) item;
                lightSection.setVisible(true);
                lightTypeCombo.setSelectedItem(lt.type != null ? lt.type.name() : "POINT");
                raysSpinner.setValue(lt.rays);
                distanceSpinner.setValue(lt.distance);
                softLightBox.setSelected(lt.soft);
                xrayBox.setSelected(lt.isXRay);
            } else {
                lightSection.setVisible(false);
            }
        }

        updating = false;
        revalidate();
        repaint();
    }

    private void updateLayerCombo() {
        layerCombo.removeAllItems();
        if (scene != null) {
            for (LayerItemVO l : scene.composite.layers) {
                layerCombo.addItem(l.layerName);
            }
        }
    }

    private CollapsibleSection createTransformSection() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel topRow = new JPanel(new BorderLayout(4, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(500, 22));
        nameField = DarkThemeUtils.createCompactTextField();
        nameField.addActionListener(e -> syncToItem());

        JPanel checks = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        checks.setOpaque(false);
        visibleBox = new JCheckBox("Visible", true);
        lockedBox = new JCheckBox("Locked", false);
        for (JCheckBox cb : new JCheckBox[]{visibleBox, lockedBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            cb.setForeground(DarkThemeUtils.TEXT_SECONDARY);
            cb.addActionListener(e -> syncToItem());
            checks.add(cb);
        }
        topRow.add(nameField, BorderLayout.CENTER);
        topRow.add(checks, BorderLayout.EAST);
        content.add(topRow);
        content.add(Box.createVerticalStrut(2));

        JPanel layerRow = DarkThemeUtils.createPropContainer("Layer");
        layerCombo = new JComboBox<>();
        layerCombo.setBackground(DarkThemeUtils.BG_INPUT);
        layerCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        layerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        layerCombo.addActionListener(e -> syncToItem());
        layerRow.add(layerCombo, BorderLayout.CENTER);
        content.add(layerRow);
        content.add(Box.createVerticalStrut(2));

        posXSpinner = DarkThemeUtils.createCompactSpinner(0f, -100000f, 100000f, 10f);
        posYSpinner = DarkThemeUtils.createCompactSpinner(0f, -100000f, 100000f, 10f);
        posXSpinner.addChangeListener(e -> syncToItem());
        posYSpinner.addChangeListener(e -> syncToItem());
        content.add(DarkThemeUtils.createVector2Row("Position", "X", posXSpinner, "Y", posYSpinner));
        content.add(Box.createVerticalStrut(2));

        scaleXSpinner = DarkThemeUtils.createCompactSpinner(1f, 0.01f, 100f, 0.1f);
        scaleYSpinner = DarkThemeUtils.createCompactSpinner(1f, 0.01f, 100f, 0.1f);
        scaleXSpinner.addChangeListener(e -> syncToItem());
        scaleYSpinner.addChangeListener(e -> syncToItem());
        content.add(DarkThemeUtils.createVector2Row("Scale", "X", scaleXSpinner, "Y", scaleYSpinner));
        content.add(Box.createVerticalStrut(2));

        rotationSpinner = DarkThemeUtils.createCompactSpinner(0f, -3600f, 3600f, 15f);
        rotationSpinner.addChangeListener(e -> syncToItem());
        JPanel rotRow = DarkThemeUtils.createPropContainer("Rotation");
        JPanel rotInner = new JPanel(new BorderLayout(4, 0));
        rotInner.setOpaque(false);
        JLabel degLbl = new JLabel("°");
        degLbl.setForeground(DarkThemeUtils.TEXT_MUTED);
        rotInner.add(rotationSpinner, BorderLayout.CENTER);
        rotInner.add(degLbl, BorderLayout.EAST);
        rotRow.add(rotInner, BorderLayout.CENTER);
        content.add(rotRow);
        content.add(Box.createVerticalStrut(2));

        originXSpinner = DarkThemeUtils.createCompactSpinner(0f, -1000f, 1000f, 5f);
        originYSpinner = DarkThemeUtils.createCompactSpinner(0f, -1000f, 1000f, 5f);
        originXSpinner.addChangeListener(e -> syncToItem());
        originYSpinner.addChangeListener(e -> syncToItem());
        content.add(DarkThemeUtils.createVector2Row("Origin", "X", originXSpinner, "Y", originYSpinner));
        content.add(Box.createVerticalStrut(2));

        zIndexSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        zIndexSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        zIndexSpinner.addChangeListener(e -> syncToItem());
        JPanel zRow = DarkThemeUtils.createPropContainer("Z-Index");
        zRow.add(zIndexSpinner, BorderLayout.CENTER);
        content.add(zRow);

        return new CollapsibleSection("Transform", DarkThemeUtils.getFatcowIcon("cog.png"), content, null);
    }

    private CollapsibleSection createPhysicsSection() {
        physicsBodyContent = new JPanel();
        physicsBodyContent.setLayout(new BoxLayout(physicsBodyContent, BoxLayout.Y_AXIS));
        physicsBodyContent.setOpaque(false);
        physicsBodyContent.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel btRow = DarkThemeUtils.createPropContainer("Body Type");
        bodyTypeCombo = new JComboBox<>(new String[]{"Static", "Kinematic", "Dynamic"});
        bodyTypeCombo.setBackground(DarkThemeUtils.BG_INPUT);
        bodyTypeCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        bodyTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bodyTypeCombo.addActionListener(e -> syncToItem());
        btRow.add(bodyTypeCombo, BorderLayout.CENTER);
        physicsBodyContent.add(btRow);
        physicsBodyContent.add(Box.createVerticalStrut(2));

        densitySpinner = DarkThemeUtils.createCompactSpinner(1f, 0f, 1000f, 0.1f);
        frictionSpinner = DarkThemeUtils.createCompactSpinner(0.2f, 0f, 10f, 0.05f);
        restitutionSpinner = DarkThemeUtils.createCompactSpinner(0f, 0f, 1f, 0.05f);
        densitySpinner.addChangeListener(e -> syncToItem());
        frictionSpinner.addChangeListener(e -> syncToItem());
        restitutionSpinner.addChangeListener(e -> syncToItem());

        physicsBodyContent.add(DarkThemeUtils.createSinglePropRow("Density", densitySpinner));
        physicsBodyContent.add(Box.createVerticalStrut(2));
        physicsBodyContent.add(DarkThemeUtils.createSinglePropRow("Friction", frictionSpinner));
        physicsBodyContent.add(Box.createVerticalStrut(2));
        physicsBodyContent.add(DarkThemeUtils.createSinglePropRow("Restitution", restitutionSpinner));
        physicsBodyContent.add(Box.createVerticalStrut(2));

        JPanel checkP = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkP.setOpaque(false);
        checkP.setMaximumSize(new Dimension(500, 22));
        sensorBox = new JCheckBox("Is Sensor", false);
        bulletBox = new JCheckBox("Bullet (CCD)", false);
        for (JCheckBox cb : new JCheckBox[]{sensorBox, bulletBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            cb.addActionListener(e -> syncToItem());
            checkP.add(cb);
        }
        physicsBodyContent.add(checkP);

        physicsEnabledBox = new JCheckBox("", true);
        physicsEnabledBox.setOpaque(false);
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

        return new CollapsibleSection("Box2D Rigidbody", DarkThemeUtils.getFatcowIcon("bomb.png"), physicsBodyContent, physicsEnabledBox);
    }

    private CollapsibleSection createLightSection() {
        lightBodyContent = new JPanel();
        lightBodyContent.setLayout(new BoxLayout(lightBodyContent, BoxLayout.Y_AXIS));
        lightBodyContent.setOpaque(false);
        lightBodyContent.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel ltRow = DarkThemeUtils.createPropContainer("Light Type");
        lightTypeCombo = new JComboBox<>(new String[]{"POINT", "CONE", "DIRECTIONAL"});
        lightTypeCombo.setBackground(DarkThemeUtils.BG_INPUT);
        lightTypeCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        lightTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lightTypeCombo.addActionListener(e -> syncToItem());
        ltRow.add(lightTypeCombo, BorderLayout.CENTER);
        lightBodyContent.add(ltRow);
        lightBodyContent.add(Box.createVerticalStrut(2));

        raysSpinner = new JSpinner(new SpinnerNumberModel(128, 16, 1024, 16));
        distanceSpinner = DarkThemeUtils.createCompactSpinner(300f, 10f, 5000f, 25f);
        raysSpinner.addChangeListener(e -> syncToItem());
        distanceSpinner.addChangeListener(e -> syncToItem());

        lightBodyContent.add(DarkThemeUtils.createSinglePropRow("Rays", raysSpinner));
        lightBodyContent.add(Box.createVerticalStrut(2));
        lightBodyContent.add(DarkThemeUtils.createSinglePropRow("Distance", distanceSpinner));
        lightBodyContent.add(Box.createVerticalStrut(2));

        JPanel checkP = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkP.setOpaque(false);
        checkP.setMaximumSize(new Dimension(500, 22));
        softLightBox = new JCheckBox("Soft Shadows", true);
        xrayBox = new JCheckBox("X-Ray", false);
        for (JCheckBox cb : new JCheckBox[]{softLightBox, xrayBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            cb.addActionListener(e -> syncToItem());
            checkP.add(cb);
        }
        lightBodyContent.add(checkP);

        return new CollapsibleSection("Dynamic Light 2D", DarkThemeUtils.getFatcowIcon("lightning.png"), lightBodyContent, null);
    }

    private void showAddComponentPopup(Component invoker) {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "Please select an entity first.");
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        JMenuItem addPhysics = new JMenuItem("Box2D Rigidbody", DarkThemeUtils.getFatcowIcon("bomb.png"));
        JMenuItem addLight = new JMenuItem("Point Light 2D", DarkThemeUtils.getFatcowIcon("lightning.png"));
        JMenuItem addParticle = new JMenuItem("Particle Emitter", DarkThemeUtils.getFatcowIcon("fire.png"));

        addPhysics.addActionListener(e -> {
            if (currentItem.physics == null) {
                currentItem.physics = new PhysicsBodyDataVO();
                setItem(currentItem);
                if (changeListener != null) changeListener.run();
            }
        });

        addLight.addActionListener(e -> {
            if (!(currentItem instanceof LightVO)) {
                LightVO lt = new LightVO(currentItem.itemName + "_Light", LightVO.LightType.POINT, currentItem.x, currentItem.y);
                scene.composite.sLights.add(lt);
                setItem(lt);
                if (changeListener != null) changeListener.run();
            }
        });

        menu.add(addPhysics);
        menu.add(addLight);
        menu.add(addParticle);
        menu.show(invoker, 0, invoker.getHeight());
    }

    private void setControlsEnabled(boolean enabled) {
        posXSpinner.setEnabled(enabled);
        posYSpinner.setEnabled(enabled);
        scaleXSpinner.setEnabled(enabled);
        scaleYSpinner.setEnabled(enabled);
        rotationSpinner.setEnabled(enabled);
        originXSpinner.setEnabled(enabled);
        originYSpinner.setEnabled(enabled);
        zIndexSpinner.setEnabled(enabled);
        visibleBox.setEnabled(enabled);
        lockedBox.setEnabled(enabled);
    }

    private void setPhysicsControlsEnabled(boolean enabled) {
        bodyTypeCombo.setEnabled(enabled);
        densitySpinner.setEnabled(enabled);
        frictionSpinner.setEnabled(enabled);
        restitutionSpinner.setEnabled(enabled);
        sensorBox.setEnabled(enabled);
        bulletBox.setEnabled(enabled);
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
        currentItem.zIndex = ((Number) zIndexSpinner.getValue()).intValue();
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

        if (currentItem instanceof LightVO) {
            LightVO lt = (LightVO) currentItem;
            lt.type = LightVO.LightType.valueOf((String) lightTypeCombo.getSelectedItem());
            lt.rays = ((Number) raysSpinner.getValue()).intValue();
            lt.distance = ((Number) distanceSpinner.getValue()).floatValue();
            lt.soft = softLightBox.isSelected();
            lt.isXRay = xrayBox.isSelected();
        }

        if (changeListener != null) {
            changeListener.run();
        }
    }
}
