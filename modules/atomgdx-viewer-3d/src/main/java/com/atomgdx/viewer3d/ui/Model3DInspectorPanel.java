package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.ui.DarkThemeUtils.CollapsibleSection;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Unity-style Collapsible Inspector for 3D GameObjects, Meshes, PBR Materials, and Physics.
 */
public class Model3DInspectorPanel extends JPanel {

    private Node3DVO currentNode;
    private boolean updating = false;
    private Runnable changeListener;

    private final JPanel componentsContainer = new JPanel();

    // Node Header
    private JTextField nameField;
    private JComboBox<String> typeCombo;

    // Transform 3D
    private JSpinner posXSpinner, posYSpinner, posZSpinner;
    private JSpinner rotXSpinner, rotYSpinner, rotZSpinner;
    private JSpinner scaleXSpinner, scaleYSpinner, scaleZSpinner;

    // Mesh & Stats
    private JComboBox<String> shapeCombo;
    private JLabel vertexCountLbl;
    private JLabel triangleCountLbl;
    private CollapsibleSection meshSection;

    // Material & PBR
    private JComboBox<String> materialPresetCombo;
    private JSlider metallicSlider;
    private JSlider roughnessSlider;
    private JSlider opacitySlider;
    private JCheckBox twoSidedBox, wireframeBox;
    private CollapsibleSection materialSection;

    // Lighting (if Light)
    private JSpinner lightIntensitySpinner, lightDistSpinner;
    private CollapsibleSection lightSection;

    // Physics 3D
    private JCheckBox physicsEnabledBox;
    private JComboBox<String> bodyTypeCombo;
    private JSpinner massSpinner, frictionSpinner, restitutionSpinner;
    private JComboBox<String> colShapeCombo;
    private CollapsibleSection physicsSection;

    public Model3DInspectorPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        componentsContainer.setLayout(new BoxLayout(componentsContainer, BoxLayout.Y_AXIS));
        componentsContainer.setBackground(DarkThemeUtils.BG_DARK);
        componentsContainer.setBorder(null);

        // 1. Transform Section (Always present)
        CollapsibleSection transformSection = createTransformSection();
        transformSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        componentsContainer.add(transformSection);

        // 2. Mesh Section
        meshSection = createMeshSection();
        meshSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        componentsContainer.add(meshSection);

        // 3. Material Section
        materialSection = createMaterialSection();
        materialSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        componentsContainer.add(materialSection);

        // 4. Lighting Section
        lightSection = createLightSection();
        lightSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        componentsContainer.add(lightSection);

        // 5. Physics Section
        physicsSection = createPhysicsSection();
        physicsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        componentsContainer.add(physicsSection);

        componentsContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(componentsContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        setNode(null);
    }

    public void setChangeListener(Runnable listener) {
        this.changeListener = listener;
    }

    public void setNode(Node3DVO node) {
        this.currentNode = node;
        updating = true;

        if (node == null) {
            nameField.setText("None Selected");
            nameField.setEnabled(false);
            typeCombo.setEnabled(false);
            setControlsEnabled(false);
            meshSection.setVisible(false);
            materialSection.setVisible(false);
            lightSection.setVisible(false);
            physicsSection.setVisible(false);
        } else {
            nameField.setEnabled(true);
            typeCombo.setEnabled(true);
            setControlsEnabled(true);

            nameField.setText(node.nodeName);
            typeCombo.setSelectedItem(node.nodeType.name());

            posXSpinner.setValue(node.posX);
            posYSpinner.setValue(node.posY);
            posZSpinner.setValue(node.posZ);

            rotXSpinner.setValue(node.rotX);
            rotYSpinner.setValue(node.rotY);
            rotZSpinner.setValue(node.rotZ);

            scaleXSpinner.setValue(node.scaleX);
            scaleYSpinner.setValue(node.scaleY);
            scaleZSpinner.setValue(node.scaleZ);

            // Mesh Section
            if (node.nodeType == Node3DVO.NodeType.MESH || node.nodeType == Node3DVO.NodeType.PREFAB) {
                meshSection.setVisible(true);
                shapeCombo.setSelectedItem(node.meshShape.name());
                vertexCountLbl.setText(String.valueOf(node.vertexCount));
                triangleCountLbl.setText(String.valueOf(node.triangleCount));
            } else {
                meshSection.setVisible(false);
            }

            // Material Section
            if (node.material != null && (node.nodeType == Node3DVO.NodeType.MESH || node.nodeType == Node3DVO.NodeType.PREFAB)) {
                materialSection.setVisible(true);
                materialPresetCombo.setSelectedItem(node.material.materialName);
                metallicSlider.setValue((int) (node.material.metallic * 100));
                roughnessSlider.setValue((int) (node.material.roughness * 100));
                opacitySlider.setValue((int) (node.material.opacity * 100));
                twoSidedBox.setSelected(node.material.isTwoSided);
                wireframeBox.setSelected(node.material.isWireframe);
            } else {
                materialSection.setVisible(false);
            }

            // Lighting Section
            if (node.nodeType == Node3DVO.NodeType.LIGHT_POINT || node.nodeType == Node3DVO.NodeType.LIGHT_DIRECTIONAL) {
                lightSection.setVisible(true);
                lightIntensitySpinner.setValue(node.lightIntensity);
                lightDistSpinner.setValue(node.lightDistance);
            } else {
                lightSection.setVisible(false);
            }

            // Physics Section
            physicsSection.setVisible(true);
            physicsEnabledBox.setSelected(node.hasPhysics);
            setPhysicsControlsEnabled(node.hasPhysics);
            bodyTypeCombo.setSelectedIndex(Math.max(0, Math.min(2, node.physicsBodyType)));
            massSpinner.setValue(node.mass);
            frictionSpinner.setValue(node.friction);
            restitutionSpinner.setValue(node.restitution);
            colShapeCombo.setSelectedItem(node.collisionShape);
        }

        updating = false;
        revalidate();
        repaint();
    }

    private CollapsibleSection createTransformSection() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel nameRow = DarkThemeUtils.createPropContainer("Name");
        nameField = DarkThemeUtils.createCompactTextField();
        nameField.addActionListener(e -> syncToNode());
        nameRow.add(nameField, BorderLayout.CENTER);
        content.add(nameRow);
        content.add(Box.createVerticalStrut(2));

        JPanel typeRow = DarkThemeUtils.createPropContainer("Type");
        typeCombo = new JComboBox<>(new String[]{"EMPTY", "MESH", "LIGHT_POINT", "LIGHT_DIRECTIONAL", "CAMERA", "PREFAB", "PARTICLE_EMITTER"});
        typeCombo.setBackground(DarkThemeUtils.BG_INPUT);
        typeCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        typeCombo.addActionListener(e -> syncToNode());
        typeRow.add(typeCombo, BorderLayout.CENTER);
        content.add(typeRow);
        content.add(Box.createVerticalStrut(2));

        // Position 3D (X, Y, Z)
        posXSpinner = DarkThemeUtils.createCompactSpinner(0f, -10000f, 10000f, 0.5f);
        posYSpinner = DarkThemeUtils.createCompactSpinner(0f, -10000f, 10000f, 0.5f);
        posZSpinner = DarkThemeUtils.createCompactSpinner(0f, -10000f, 10000f, 0.5f);
        posXSpinner.addChangeListener(e -> syncToNode());
        posYSpinner.addChangeListener(e -> syncToNode());
        posZSpinner.addChangeListener(e -> syncToNode());
        content.add(createVector3Row("Position", posXSpinner, posYSpinner, posZSpinner));
        content.add(Box.createVerticalStrut(2));

        // Rotation 3D (X, Y, Z)
        rotXSpinner = DarkThemeUtils.createCompactSpinner(0f, -3600f, 3600f, 15f);
        rotYSpinner = DarkThemeUtils.createCompactSpinner(0f, -3600f, 3600f, 15f);
        rotZSpinner = DarkThemeUtils.createCompactSpinner(0f, -3600f, 3600f, 15f);
        rotXSpinner.addChangeListener(e -> syncToNode());
        rotYSpinner.addChangeListener(e -> syncToNode());
        rotZSpinner.addChangeListener(e -> syncToNode());
        content.add(createVector3Row("Rotation", rotXSpinner, rotYSpinner, rotZSpinner));
        content.add(Box.createVerticalStrut(2));

        // Scale 3D (X, Y, Z)
        scaleXSpinner = DarkThemeUtils.createCompactSpinner(1f, 0.01f, 1000f, 0.1f);
        scaleYSpinner = DarkThemeUtils.createCompactSpinner(1f, 0.01f, 1000f, 0.1f);
        scaleZSpinner = DarkThemeUtils.createCompactSpinner(1f, 0.01f, 1000f, 0.1f);
        scaleXSpinner.addChangeListener(e -> syncToNode());
        scaleYSpinner.addChangeListener(e -> syncToNode());
        scaleZSpinner.addChangeListener(e -> syncToNode());
        content.add(createVector3Row("Scale", scaleXSpinner, scaleYSpinner, scaleZSpinner));

        return new CollapsibleSection("Transform 3D", DarkThemeUtils.getFatcowIcon("cog.png"), content, null);
    }

    private JPanel createVector3Row(String label, JSpinner sx, JSpinner sy, JSpinner sz) {
        JPanel r = DarkThemeUtils.createPropContainer(label);
        JPanel inner = new JPanel(new GridLayout(1, 3, 3, 0));
        inner.setOpaque(false);

        inner.add(DarkThemeUtils.createAxisField("X", DarkThemeUtils.AXIS_X, sx));
        inner.add(DarkThemeUtils.createAxisField("Y", DarkThemeUtils.AXIS_Y, sy));
        inner.add(DarkThemeUtils.createAxisField("Z", DarkThemeUtils.AXIS_Z, sz));

        r.add(inner, BorderLayout.CENTER);
        return r;
    }

    private CollapsibleSection createMeshSection() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel shapeRow = DarkThemeUtils.createPropContainer("Geometry");
        shapeCombo = new JComboBox<>(new String[]{"BOX", "SPHERE", "CYLINDER", "CONE", "CAPSULE", "PLANE", "TORUS", "CUSTOM_MODEL"});
        shapeCombo.setBackground(DarkThemeUtils.BG_INPUT);
        shapeCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        shapeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        shapeCombo.addActionListener(e -> syncToNode());
        shapeRow.add(shapeCombo, BorderLayout.CENTER);
        content.add(shapeRow);
        content.add(Box.createVerticalStrut(2));

        vertexCountLbl = new JLabel("24");
        vertexCountLbl.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        vertexCountLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        content.add(DarkThemeUtils.createSinglePropRow("Vertices", vertexCountLbl));
        content.add(Box.createVerticalStrut(2));

        triangleCountLbl = new JLabel("12");
        triangleCountLbl.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        triangleCountLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        content.add(DarkThemeUtils.createSinglePropRow("Triangles", triangleCountLbl));

        return new CollapsibleSection("3D Mesh & Stats", DarkThemeUtils.getFatcowIcon("box.png"), content, null);
    }

    private CollapsibleSection createMaterialSection() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel presetRow = DarkThemeUtils.createPropContainer("Material");
        materialPresetCombo = new JComboBox<>(new String[]{
                "Default Material", "Metallic Gold", "Brushed Steel", "Neon Glow Cyan", "SciFi Hull Paint", "Matte Plastic", "Transparent Glass"
        });
        materialPresetCombo.setBackground(DarkThemeUtils.BG_INPUT);
        materialPresetCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        materialPresetCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        materialPresetCombo.addActionListener(e -> {
            if (!updating && currentNode != null) {
                String preset = (String) materialPresetCombo.getSelectedItem();
                currentNode.material = Material3DVO.createPreset(preset);
                setNode(currentNode);
                if (changeListener != null) changeListener.run();
            }
        });
        presetRow.add(materialPresetCombo, BorderLayout.CENTER);
        content.add(presetRow);
        content.add(Box.createVerticalStrut(2));

        metallicSlider = new JSlider(0, 100, 20);
        metallicSlider.setOpaque(false);
        metallicSlider.addChangeListener(e -> syncToNode());
        content.add(DarkThemeUtils.createSinglePropRow("Metallic", metallicSlider));
        content.add(Box.createVerticalStrut(2));

        roughnessSlider = new JSlider(0, 100, 50);
        roughnessSlider.setOpaque(false);
        roughnessSlider.addChangeListener(e -> syncToNode());
        content.add(DarkThemeUtils.createSinglePropRow("Roughness", roughnessSlider));
        content.add(Box.createVerticalStrut(2));

        opacitySlider = new JSlider(0, 100, 100);
        opacitySlider.setOpaque(false);
        opacitySlider.addChangeListener(e -> syncToNode());
        content.add(DarkThemeUtils.createSinglePropRow("Opacity", opacitySlider));
        content.add(Box.createVerticalStrut(2));

        JPanel flagsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        flagsRow.setOpaque(false);
        twoSidedBox = new JCheckBox("Two-Sided", false);
        wireframeBox = new JCheckBox("Wireframe", false);
        for (JCheckBox cb : new JCheckBox[]{twoSidedBox, wireframeBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            cb.addActionListener(e -> syncToNode());
            flagsRow.add(cb);
        }
        content.add(flagsRow);

        return new CollapsibleSection("PBR Material", DarkThemeUtils.getFatcowIcon("color_wheel.png"), content, null);
    }

    private CollapsibleSection createLightSection() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 4, 4, 4));

        lightIntensitySpinner = DarkThemeUtils.createCompactSpinner(1f, 0f, 100f, 0.2f);
        lightDistSpinner = DarkThemeUtils.createCompactSpinner(25f, 1f, 500f, 5f);
        lightIntensitySpinner.addChangeListener(e -> syncToNode());
        lightDistSpinner.addChangeListener(e -> syncToNode());

        content.add(DarkThemeUtils.createSinglePropRow("Intensity", lightIntensitySpinner));
        content.add(Box.createVerticalStrut(2));
        content.add(DarkThemeUtils.createSinglePropRow("Range", lightDistSpinner));

        return new CollapsibleSection("Light 3D", DarkThemeUtils.getFatcowIcon("lightning.png"), content, null);
    }

    private CollapsibleSection createPhysicsSection() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(2, 4, 4, 4));

        JPanel btRow = DarkThemeUtils.createPropContainer("Body Type");
        bodyTypeCombo = new JComboBox<>(new String[]{"Static", "Kinematic", "Dynamic"});
        bodyTypeCombo.setBackground(DarkThemeUtils.BG_INPUT);
        bodyTypeCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        bodyTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bodyTypeCombo.addActionListener(e -> syncToNode());
        btRow.add(bodyTypeCombo, BorderLayout.CENTER);
        content.add(btRow);
        content.add(Box.createVerticalStrut(2));

        massSpinner = DarkThemeUtils.createCompactSpinner(1f, 0f, 10000f, 0.5f);
        frictionSpinner = DarkThemeUtils.createCompactSpinner(0.5f, 0f, 10f, 0.05f);
        restitutionSpinner = DarkThemeUtils.createCompactSpinner(0.1f, 0f, 1f, 0.05f);
        massSpinner.addChangeListener(e -> syncToNode());
        frictionSpinner.addChangeListener(e -> syncToNode());
        restitutionSpinner.addChangeListener(e -> syncToNode());

        content.add(DarkThemeUtils.createSinglePropRow("Mass (kg)", massSpinner));
        content.add(Box.createVerticalStrut(2));
        content.add(DarkThemeUtils.createSinglePropRow("Friction", frictionSpinner));
        content.add(Box.createVerticalStrut(2));
        content.add(DarkThemeUtils.createSinglePropRow("Restitution", restitutionSpinner));
        content.add(Box.createVerticalStrut(2));

        JPanel shapeRow = DarkThemeUtils.createPropContainer("Collider");
        colShapeCombo = new JComboBox<>(new String[]{"BOX", "SPHERE", "CAPSULE", "CONVEX_HULL"});
        colShapeCombo.setBackground(DarkThemeUtils.BG_INPUT);
        colShapeCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        colShapeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        colShapeCombo.addActionListener(e -> syncToNode());
        shapeRow.add(colShapeCombo, BorderLayout.CENTER);
        content.add(shapeRow);

        physicsEnabledBox = new JCheckBox("", false);
        physicsEnabledBox.setOpaque(false);
        physicsEnabledBox.addActionListener(e -> {
            boolean en = physicsEnabledBox.isSelected();
            setPhysicsControlsEnabled(en);
            if (currentNode != null) {
                currentNode.hasPhysics = en;
            }
            syncToNode();
        });

        return new CollapsibleSection("Bullet Physics 3D", DarkThemeUtils.getFatcowIcon("bomb.png"), content, physicsEnabledBox);
    }

    private void setControlsEnabled(boolean enabled) {
        posXSpinner.setEnabled(enabled);
        posYSpinner.setEnabled(enabled);
        posZSpinner.setEnabled(enabled);
        rotXSpinner.setEnabled(enabled);
        rotYSpinner.setEnabled(enabled);
        rotZSpinner.setEnabled(enabled);
        scaleXSpinner.setEnabled(enabled);
        scaleYSpinner.setEnabled(enabled);
        scaleZSpinner.setEnabled(enabled);
    }

    private void setPhysicsControlsEnabled(boolean enabled) {
        bodyTypeCombo.setEnabled(enabled);
        massSpinner.setEnabled(enabled);
        frictionSpinner.setEnabled(enabled);
        restitutionSpinner.setEnabled(enabled);
        colShapeCombo.setEnabled(enabled);
    }

    private void syncToNode() {
        if (updating || currentNode == null) return;

        currentNode.nodeName = nameField.getText().trim();
        currentNode.nodeType = Node3DVO.NodeType.valueOf((String) typeCombo.getSelectedItem());

        currentNode.posX = ((Number) posXSpinner.getValue()).floatValue();
        currentNode.posY = ((Number) posYSpinner.getValue()).floatValue();
        currentNode.posZ = ((Number) posZSpinner.getValue()).floatValue();

        currentNode.rotX = ((Number) rotXSpinner.getValue()).floatValue();
        currentNode.rotY = ((Number) rotYSpinner.getValue()).floatValue();
        currentNode.rotZ = ((Number) rotZSpinner.getValue()).floatValue();

        currentNode.scaleX = ((Number) scaleXSpinner.getValue()).floatValue();
        currentNode.scaleY = ((Number) scaleYSpinner.getValue()).floatValue();
        currentNode.scaleZ = ((Number) scaleZSpinner.getValue()).floatValue();

        if (shapeCombo.getSelectedItem() != null) {
            currentNode.meshShape = Node3DVO.MeshShape.valueOf((String) shapeCombo.getSelectedItem());
        }

        if (currentNode.material != null) {
            currentNode.material.metallic = metallicSlider.getValue() / 100.0f;
            currentNode.material.roughness = roughnessSlider.getValue() / 100.0f;
            currentNode.material.opacity = opacitySlider.getValue() / 100.0f;
            currentNode.material.isTwoSided = twoSidedBox.isSelected();
            currentNode.material.isWireframe = wireframeBox.isSelected();
        }

        currentNode.lightIntensity = ((Number) lightIntensitySpinner.getValue()).floatValue();
        currentNode.lightDistance = ((Number) lightDistSpinner.getValue()).floatValue();

        currentNode.hasPhysics = physicsEnabledBox.isSelected();
        currentNode.physicsBodyType = bodyTypeCombo.getSelectedIndex();
        currentNode.mass = ((Number) massSpinner.getValue()).floatValue();
        currentNode.friction = ((Number) frictionSpinner.getValue()).floatValue();
        currentNode.restitution = ((Number) restitutionSpinner.getValue()).floatValue();
        if (colShapeCombo.getSelectedItem() != null) {
            currentNode.collisionShape = (String) colShapeCombo.getSelectedItem();
        }

        if (changeListener != null) {
            changeListener.run();
        }
    }
}
