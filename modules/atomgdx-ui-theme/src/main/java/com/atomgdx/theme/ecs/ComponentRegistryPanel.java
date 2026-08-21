package com.atomgdx.theme.ecs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Visual Ashley ECS Component Registry & System Introspection Tool.
 */
public class ComponentRegistryPanel extends JPanel {

    public static class ComponentInfo implements Serializable {
        public String name;
        public String packageName;
        public String description;
        public List<String> fieldSignatures = new ArrayList<>();
        public boolean isCustom;

        public ComponentInfo(String name, String packageName, String description, boolean isCustom, String... fields) {
            this.name = name;
            this.packageName = packageName;
            this.description = description;
            this.isCustom = isCustom;
            for (String f : fields) fieldSignatures.add(f);
        }

        @Override
        public String toString() {
            return name + (isCustom ? " (Custom)" : " [Built-in]");
        }
    }

    private final List<ComponentInfo> components = new ArrayList<>();
    private final DefaultListModel<ComponentInfo> listModel = new DefaultListModel<>();
    private final JList<ComponentInfo> componentList = new JList<>(listModel);
    private final JTextField searchField = new JTextField();

    private final JLabel compNameLabel = new JLabel("Component Details");
    private final JLabel compPackageLabel = new JLabel("Package: com.atomgdx.ecs");
    private final JTextArea compDescArea = new JTextArea();
    private final DefaultListModel<String> fieldsListModel = new DefaultListModel<>();
    private final JList<String> fieldsList = new JList<>(fieldsListModel);

    public ComponentRegistryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(22, 23, 26));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        populateDefaultComponents();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 32, 36));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Ashley ECS Component Registry");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        header.add(title, BorderLayout.WEST);

        JButton createCompBtn = new JButton("+ Create Component");
        createCompBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        createCompBtn.setBackground(new Color(44, 93, 212));
        createCompBtn.setForeground(Color.WHITE);
        createCompBtn.addActionListener(e -> createNewComponent());
        header.add(createCompBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Split: Left list with search, Right fields and descriptions
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setBackground(new Color(22, 23, 26));
        splitPane.setBorder(null);

        // Left List & Search
        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.setBackground(new Color(30, 32, 36));
        leftPanel.setBorder(createTitledBorder("Registered Components"));

        JPanel searchBar = new JPanel(new BorderLayout(4, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Search:"), BorderLayout.WEST);
        searchField.addCaretListener(e -> filterComponents(searchField.getText().trim()));
        searchBar.add(searchField, BorderLayout.CENTER);
        leftPanel.add(searchBar, BorderLayout.NORTH);

        filterComponents("");
        componentList.setBackground(new Color(18, 19, 22));
        componentList.setForeground(Color.WHITE);
        componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        componentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedComponent();
        });
        leftPanel.add(new JScrollPane(componentList), BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // Right details
        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(new Color(30, 32, 36));
        rightPanel.setBorder(createTitledBorder("Component Specification"));

        JPanel detailHeader = new JPanel(new GridLayout(2, 1, 2, 2));
        detailHeader.setOpaque(false);
        compNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        compNameLabel.setForeground(new Color(0, 220, 255));
        compPackageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        compPackageLabel.setForeground(new Color(160, 165, 175));
        detailHeader.add(compNameLabel);
        detailHeader.add(compPackageLabel);
        rightPanel.add(detailHeader, BorderLayout.NORTH);

        JPanel detailBody = new JPanel(new GridLayout(2, 1, 4, 4));
        detailBody.setOpaque(false);

        compDescArea.setEditable(false);
        compDescArea.setLineWrap(true);
        compDescArea.setBackground(new Color(16, 17, 20));
        compDescArea.setForeground(Color.WHITE);
        compDescArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane descScroll = new JScrollPane(compDescArea);
        descScroll.setBorder(createTitledBorder("Description"));
        detailBody.add(descScroll);

        fieldsList.setBackground(new Color(16, 17, 20));
        fieldsList.setForeground(new Color(0, 255, 200));
        fieldsList.setFont(new Font("Consolas", Font.PLAIN, 11));
        JScrollPane fieldsScroll = new JScrollPane(fieldsList);
        fieldsScroll.setBorder(createTitledBorder("Fields & Types"));
        detailBody.add(fieldsScroll);

        rightPanel.add(detailBody, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        if (!listModel.isEmpty()) {
            componentList.setSelectedIndex(0);
        }
    }

    private void populateDefaultComponents() {
        components.add(new ComponentInfo("TransformComponent", "com.atomgdx.ecs.components", "Position (X, Y, Z), Scale, Rotation quaternion, and origin anchors.", false, "Vector3 position = new Vector3()", "Vector3 scale = new Vector3(1, 1, 1)", "Quaternion rotation = new Quaternion()", "Vector2 origin = new Vector2()"));
        components.add(new ComponentInfo("TextureComponent", "com.atomgdx.ecs.components", "TextureRegion / AtlasRegion reference for 2D SpriteBatch rendering.", false, "TextureRegion region", "Color tint = new Color(Color.WHITE)", "boolean flipX", "boolean flipY"));
        components.add(new ComponentInfo("AnimationComponent", "com.atomgdx.ecs.components", "Keyframe Animation state with frame duration and playmode.", false, "Animation<TextureRegion> animation", "float stateTime = 0f", "boolean looping = true"));
        components.add(new ComponentInfo("RigidBody2DComponent", "com.atomgdx.ecs.components", "Box2D Body, fixtures, mass, velocity, and collision filters.", false, "Body body", "BodyType type = BodyType.DynamicBody", "float friction = 0.2f", "float restitution = 0.0f"));
        components.add(new ComponentInfo("Mesh3DComponent", "com.atomgdx.ecs.components", "LibGDX 3D ModelInstance reference with materials and bounding box.", false, "ModelInstance modelInstance", "BoundingBox bounds", "boolean castShadow = true"));
        components.add(new ComponentInfo("Light2DComponent", "com.atomgdx.ecs.components", "Box2DLight PointLight, ConeLight, or DirectionalLight.", false, "Light light", "Color color = Color.WHITE", "float distance = 100f", "int rays = 128"));
        components.add(new ComponentInfo("ScriptComponent", "com.atomgdx.ecs.components", "Visual / Groovy / Java script logic controller hook.", false, "String scriptPath", "boolean isEnabled = true"));
        components.add(new ComponentInfo("HealthComponent", "com.neon.cosmos.components", "Custom game health and shield points.", true, "float currentHealth = 100f", "float maxHealth = 100f", "float shield = 50f", "boolean isInvulnerable = false"));
    }

    private void filterComponents(String query) {
        listModel.clear();
        for (ComponentInfo c : components) {
            if (query.isBlank() || c.name.toLowerCase().contains(query.toLowerCase()) || c.description.toLowerCase().contains(query.toLowerCase())) {
                listModel.addElement(c);
            }
        }
    }

    private void showSelectedComponent() {
        ComponentInfo c = componentList.getSelectedValue();
        if (c == null) return;

        compNameLabel.setText(c.name);
        compPackageLabel.setText("Package: " + c.packageName + (c.isCustom ? " [Custom Component]" : " [Engine Core]"));
        compDescArea.setText(c.description);

        fieldsListModel.clear();
        for (String f : c.fieldSignatures) {
            fieldsListModel.addElement(f);
        }
    }

    private void createNewComponent() {
        String name = JOptionPane.showInputDialog(this, "Enter Component Class Name (e.g. ShieldComponent):", "Create ECS Component", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            ComponentInfo custom = new ComponentInfo(name.trim(), "com.neon.cosmos.components", "User-defined Ashley ECS component.", true, "public boolean isEnabled = true;");
            components.add(custom);
            filterComponents(searchField.getText().trim());
            componentList.setSelectedValue(custom, true);
        }
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 52, 58), 1),
                title
        );
        border.setTitleColor(new Color(220, 224, 230));
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        return border;
    }
}
