package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.editor.scene2d.data.vo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Unity-style Collapsible Component Inspector for HyperLap2D Scene Items.
 * Features foldout component headers, compact colored Vector2 inputs (X/Y),
 * toggles, and a "+ Add Component" menu.
 */
public class SceneItemInspectorPanel extends JPanel {

    public static class UnityColors {
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
    }

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
        setBackground(UnityColors.BG_DARK);

        componentsContainer.setLayout(new BoxLayout(componentsContainer, BoxLayout.Y_AXIS));
        componentsContainer.setBackground(UnityColors.BG_DARK);
        componentsContainer.setBorder(new EmptyBorder(4, 4, 4, 4));

        // 1. Transform Component (Always present)
        componentsContainer.add(createTransformSection());
        componentsContainer.add(Box.createVerticalStrut(4));

        // 2. Physics Component
        physicsSection = createPhysicsSection();
        componentsContainer.add(physicsSection);
        componentsContainer.add(Box.createVerticalStrut(4));

        // 3. Lighting Component
        lightSection = createLightSection();
        componentsContainer.add(lightSection);
        componentsContainer.add(Box.createVerticalStrut(8));

        // 4. "+ Add Component" Button
        JButton addComponentBtn = new JButton("+ Add Component");
        addComponentBtn.setBackground(UnityColors.BG_HEADER);
        addComponentBtn.setForeground(UnityColors.TEXT_PRIMARY);
        addComponentBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addComponentBtn.setFocusPainted(false);
        addComponentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addComponentBtn.setMaximumSize(new Dimension(280, 26));
        addComponentBtn.addActionListener(e -> showAddComponentPopup(addComponentBtn));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        btnPanel.setOpaque(false);
        btnPanel.add(addComponentBtn);
        componentsContainer.add(btnPanel);

        componentsContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(componentsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UnityColors.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
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

            // Physics Section Visibility & Values
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

            // Light Section Visibility & Values
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
        content.setBorder(new EmptyBorder(4, 6, 6, 6));

        // Name & Active Toggles
        JPanel topRow = new JPanel(new BorderLayout(4, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(500, 24));
        nameField = createCompactTextField();
        nameField.addActionListener(e -> syncToItem());

        JPanel checks = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        checks.setOpaque(false);
        visibleBox = new JCheckBox("Visible", true);
        lockedBox = new JCheckBox("Locked", false);
        for (JCheckBox cb : new JCheckBox[]{visibleBox, lockedBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            cb.setForeground(UnityColors.TEXT_SECONDARY);
            cb.addActionListener(e -> syncToItem());
            checks.add(cb);
        }
        topRow.add(nameField, BorderLayout.CENTER);
        topRow.add(checks, BorderLayout.EAST);
        content.add(topRow);
        content.add(Box.createVerticalStrut(4));

        // Layer Row
        JPanel layerRow = createPropContainer("Layer");
        layerCombo = new JComboBox<>();
        layerCombo.setBackground(UnityColors.BG_INPUT);
        layerCombo.setForeground(UnityColors.TEXT_PRIMARY);
        layerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        layerCombo.addActionListener(e -> syncToItem());
        layerRow.add(layerCombo, BorderLayout.CENTER);
        content.add(layerRow);
        content.add(Box.createVerticalStrut(3));

        // Position (X / Y)
        posXSpinner = createCompactSpinner(0f, -100000f, 100000f, 10f);
        posYSpinner = createCompactSpinner(0f, -100000f, 100000f, 10f);
        content.add(createVector2Row("Position", "X", posXSpinner, "Y", posYSpinner));
        content.add(Box.createVerticalStrut(3));

        // Scale (X / Y)
        scaleXSpinner = createCompactSpinner(1f, 0.01f, 100f, 0.1f);
        scaleYSpinner = createCompactSpinner(1f, 0.01f, 100f, 0.1f);
        content.add(createVector2Row("Scale", "X", scaleXSpinner, "Y", scaleYSpinner));
        content.add(Box.createVerticalStrut(3));

        // Rotation (Single Float)
        rotationSpinner = createCompactSpinner(0f, -3600f, 3600f, 15f);
        JPanel rotRow = createPropContainer("Rotation");
        JPanel rotInner = new JPanel(new BorderLayout(4, 0));
        rotInner.setOpaque(false);
        JLabel degLbl = new JLabel("°");
        degLbl.setForeground(UnityColors.TEXT_MUTED);
        rotInner.add(rotationSpinner, BorderLayout.CENTER);
        rotInner.add(degLbl, BorderLayout.EAST);
        rotRow.add(rotInner, BorderLayout.CENTER);
        content.add(rotRow);
        content.add(Box.createVerticalStrut(3));

        // Origin (X / Y)
        originXSpinner = createCompactSpinner(0f, -1000f, 1000f, 5f);
        originYSpinner = createCompactSpinner(0f, -1000f, 1000f, 5f);
        content.add(createVector2Row("Origin", "X", originXSpinner, "Y", originYSpinner));
        content.add(Box.createVerticalStrut(3));

        // Z-Index
        zIndexSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        zIndexSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        zIndexSpinner.addChangeListener(e -> syncToItem());
        JPanel zRow = createPropContainer("Z-Index");
        zRow.add(zIndexSpinner, BorderLayout.CENTER);
        content.add(zRow);

        return new CollapsibleSection("Transform", "cog.png", content, null);
    }

    private CollapsibleSection createPhysicsSection() {
        physicsBodyContent = new JPanel();
        physicsBodyContent.setLayout(new BoxLayout(physicsBodyContent, BoxLayout.Y_AXIS));
        physicsBodyContent.setOpaque(false);
        physicsBodyContent.setBorder(new EmptyBorder(4, 6, 6, 6));

        // Body Type
        JPanel btRow = createPropContainer("Body Type");
        bodyTypeCombo = new JComboBox<>(new String[]{"Static", "Kinematic", "Dynamic"});
        bodyTypeCombo.setBackground(UnityColors.BG_INPUT);
        bodyTypeCombo.setForeground(UnityColors.TEXT_PRIMARY);
        bodyTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bodyTypeCombo.addActionListener(e -> syncToItem());
        btRow.add(bodyTypeCombo, BorderLayout.CENTER);
        physicsBodyContent.add(btRow);
        physicsBodyContent.add(Box.createVerticalStrut(3));

        // Density / Friction / Restitution
        densitySpinner = createCompactSpinner(1f, 0f, 1000f, 0.1f);
        frictionSpinner = createCompactSpinner(0.2f, 0f, 10f, 0.05f);
        restitutionSpinner = createCompactSpinner(0f, 0f, 1f, 0.05f);

        physicsBodyContent.add(createSinglePropRow("Density", densitySpinner));
        physicsBodyContent.add(Box.createVerticalStrut(3));
        physicsBodyContent.add(createSinglePropRow("Friction", frictionSpinner));
        physicsBodyContent.add(Box.createVerticalStrut(3));
        physicsBodyContent.add(createSinglePropRow("Restitution", restitutionSpinner));
        physicsBodyContent.add(Box.createVerticalStrut(3));

        // Sensor / Bullet
        JPanel checkP = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkP.setOpaque(false);
        sensorBox = new JCheckBox("Is Sensor", false);
        bulletBox = new JCheckBox("Bullet (CCD)", false);
        for (JCheckBox cb : new JCheckBox[]{sensorBox, bulletBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(UnityColors.TEXT_PRIMARY);
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

        return new CollapsibleSection("Box2D Rigidbody", "bomb.png", physicsBodyContent, physicsEnabledBox);
    }

    private CollapsibleSection createLightSection() {
        lightBodyContent = new JPanel();
        lightBodyContent.setLayout(new BoxLayout(lightBodyContent, BoxLayout.Y_AXIS));
        lightBodyContent.setOpaque(false);
        lightBodyContent.setBorder(new EmptyBorder(4, 6, 6, 6));

        // Light Type
        JPanel ltRow = createPropContainer("Light Type");
        lightTypeCombo = new JComboBox<>(new String[]{"POINT", "CONE", "DIRECTIONAL"});
        lightTypeCombo.setBackground(UnityColors.BG_INPUT);
        lightTypeCombo.setForeground(UnityColors.TEXT_PRIMARY);
        lightTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lightTypeCombo.addActionListener(e -> syncToItem());
        ltRow.add(lightTypeCombo, BorderLayout.CENTER);
        lightBodyContent.add(ltRow);
        lightBodyContent.add(Box.createVerticalStrut(3));

        // Rays / Distance
        raysSpinner = new JSpinner(new SpinnerNumberModel(128, 16, 1024, 16));
        distanceSpinner = createCompactSpinner(300f, 10f, 5000f, 25f);
        lightBodyContent.add(createSinglePropRow("Rays", raysSpinner));
        lightBodyContent.add(Box.createVerticalStrut(3));
        lightBodyContent.add(createSinglePropRow("Distance", distanceSpinner));
        lightBodyContent.add(Box.createVerticalStrut(3));

        // Soft / X-Ray
        JPanel checkP = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkP.setOpaque(false);
        softLightBox = new JCheckBox("Soft Shadows", true);
        xrayBox = new JCheckBox("X-Ray", false);
        for (JCheckBox cb : new JCheckBox[]{softLightBox, xrayBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(UnityColors.TEXT_PRIMARY);
            cb.addActionListener(e -> syncToItem());
            checkP.add(cb);
        }
        lightBodyContent.add(checkP);

        return new CollapsibleSection("Dynamic Light 2D", "lightning.png", lightBodyContent, null);
    }

    private void showAddComponentPopup(Component invoker) {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "Please select an entity first.");
            return;
        }

        JPopupMenu menu = new JPopupMenu();
        JMenuItem addPhysics = new JMenuItem("Box2D Rigidbody", Scene2DEditorPanel.getIcon("bomb.png"));
        JMenuItem addLight = new JMenuItem("Point Light 2D", Scene2DEditorPanel.getIcon("lightning.png"));
        JMenuItem addParticle = new JMenuItem("Particle Emitter", Scene2DEditorPanel.getIcon("fire.png"));

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

    // Helper UI Factories
    private static JPanel createPropContainer(String labelText) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(500, 22));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(UnityColors.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(75, 20));
        r.add(lbl, BorderLayout.WEST);
        return r;
    }

    private static JPanel createSinglePropRow(String label, JComponent comp) {
        JPanel r = createPropContainer(label);
        r.add(comp, BorderLayout.CENTER);
        return r;
    }

    private JPanel createVector2Row(String labelText, String axis1, JSpinner s1, String axis2, JSpinner s2) {
        JPanel r = createPropContainer(labelText);
        JPanel inner = new JPanel(new GridLayout(1, 2, 4, 0));
        inner.setOpaque(false);

        inner.add(createAxisField(axis1, UnityColors.AXIS_X, s1));
        inner.add(createAxisField(axis2, UnityColors.AXIS_Y, s2));

        r.add(inner, BorderLayout.CENTER);
        return r;
    }

    private JPanel createAxisField(String axis, Color axisColor, JSpinner spinner) {
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

    private JTextField createCompactTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(UnityColors.BG_INPUT);
        tf.setForeground(UnityColors.TEXT_PRIMARY);
        tf.setCaretColor(UnityColors.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UnityColors.BORDER, 1),
                new EmptyBorder(1, 4, 1, 4)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return tf;
    }

    private JSpinner createCompactSpinner(float val, float min, float max, float step) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, step));
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sp.addChangeListener(e -> syncToItem());
        return sp;
    }

    /**
     * Unity-style Collapsible Section Component with foldout arrow, icon, and optional enable toggle.
     */
    public static class CollapsibleSection extends JPanel {
        private final JPanel contentPanel;
        private final JLabel toggleArrow = new JLabel("▼");
        private boolean isExpanded = true;

        public CollapsibleSection(String title, String iconName, JPanel content, JCheckBox enableCheckbox) {
            setLayout(new BorderLayout());
            setBackground(UnityColors.BG_PANEL);
            setBorder(new LineBorder(UnityColors.BORDER, 1));
            this.contentPanel = content;

            // Header Bar
            JPanel header = new JPanel(new BorderLayout(4, 0));
            header.setBackground(UnityColors.BG_HEADER);
            header.setBorder(new EmptyBorder(3, 6, 3, 6));
            header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            left.setOpaque(false);

            toggleArrow.setFont(new Font("Segoe UI", Font.BOLD, 10));
            toggleArrow.setForeground(UnityColors.TEXT_SECONDARY);
            left.add(toggleArrow);

            if (enableCheckbox != null) {
                left.add(enableCheckbox);
            }

            ImageIcon icon = Scene2DEditorPanel.getIcon(iconName);
            if (icon != null) {
                left.add(new JLabel(icon));
            }

            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            titleLbl.setForeground(UnityColors.TEXT_PRIMARY);
            left.add(titleLbl);

            header.add(left, BorderLayout.WEST);

            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setExpanded(!isExpanded);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    header.setBackground(UnityColors.BG_HEADER_HOVER);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    header.setBackground(UnityColors.BG_HEADER);
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
