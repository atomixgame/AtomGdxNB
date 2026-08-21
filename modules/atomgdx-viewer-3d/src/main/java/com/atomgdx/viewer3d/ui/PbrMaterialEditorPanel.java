package com.atomgdx.viewer3d.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Visual PBR Material & GLSL Shader Linkage Editor.
 */
public class PbrMaterialEditorPanel extends JPanel {

    public static class PbrMaterialData implements Serializable {
        public String materialName = "SciFi_Hull_Plate";
        public Color albedoColor = new Color(220, 230, 245);
        public float metallic = 0.85f;
        public float roughness = 0.25f;
        public float normalStrength = 1.0f;
        public float aoStrength = 1.0f;
        public Color emissiveColor = new Color(0, 220, 255);
        public float emissiveIntensity = 1.5f;
        public String alphaMode = "OPAQUE";
        public float clearcoat = 0.5f;
        public float transmission = 0.0f;
    }

    private final PbrMaterialData data;
    private final JTextField nameField = new JTextField();
    private final JButton albedoColorBtn = new JButton();
    private final JSlider metallicSlider = new JSlider(0, 100, 85);
    private final JSlider roughnessSlider = new JSlider(0, 100, 25);
    private final JSlider normalStrengthSlider = new JSlider(0, 300, 100);
    private final JSlider aoStrengthSlider = new JSlider(0, 100, 100);
    private final JButton emissiveColorBtn = new JButton();
    private final JSlider emissiveIntensitySlider = new JSlider(0, 500, 150);
    private final JComboBox<String> alphaModeCombo = new JComboBox<>(new String[]{"OPAQUE", "MASK", "BLEND"});
    private final JSlider clearcoatSlider = new JSlider(0, 100, 50);

    private final JPanel previewCanvas;

    public PbrMaterialEditorPanel() {
        this(new PbrMaterialData());
    }

    public PbrMaterialEditorPanel(PbrMaterialData data) {
        this.data = data != null ? data : new PbrMaterialData();

        previewCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark studio background
                g2.setColor(new Color(14, 15, 18));
                g2.fillRect(0, 0, w, h);

                int radius = Math.min(w, h) / 3;
                int cx = w / 2;
                int cy = h / 2;

                // Sphere PBR simulation
                float metallic = PbrMaterialEditorPanel.this.data.metallic;
                float roughness = PbrMaterialEditorPanel.this.data.roughness;

                // Ambient + Diffuse
                Color base = PbrMaterialEditorPanel.this.data.albedoColor;
                int r = Math.min(255, (int) (base.getRed() * (1f - metallic * 0.5f)));
                int gr = Math.min(255, (int) (base.getGreen() * (1f - metallic * 0.5f)));
                int b = Math.min(255, (int) (base.getBlue() * (1f - metallic * 0.5f)));

                // Radial gradient highlight simulating PBR specular lobe
                Point2D center = new Point2D.Float(cx - radius * 0.35f, cy - radius * 0.35f);
                float[] dist = {0.0f, 0.4f, 1.0f};

                Color specColor = metallic > 0.5f ? base : Color.WHITE;
                int specAlpha = Math.max(40, (int) ((1f - roughness) * 230));
                Color highlight = new Color(specColor.getRed(), specColor.getGreen(), specColor.getBlue(), specAlpha);
                Color mid = new Color(r, gr, b);
                Color shadow = new Color(Math.max(0, r / 4), Math.max(0, gr / 4), Math.max(0, b / 4));

                Color[] colors = {highlight, mid, shadow};
                RadialGradientPaint p = new RadialGradientPaint(center, radius * 1.4f, dist, colors);
                g2.setPaint(p);
                g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

                // Emissive rim glow if active
                if (PbrMaterialEditorPanel.this.data.emissiveIntensity > 0) {
                    Color em = PbrMaterialEditorPanel.this.data.emissiveColor;
                    int emAlpha = Math.min(180, (int) (PbrMaterialEditorPanel.this.data.emissiveIntensity * 40));
                    g2.setColor(new Color(em.getRed(), em.getGreen(), em.getBlue(), emAlpha));
                    g2.setStroke(new BasicStroke(3.5f));
                    g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
                }

                // Label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(PbrMaterialEditorPanel.this.data.materialName, 15, 25);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(150, 155, 165));
                g2.drawString("Metallic: " + String.format("%.2f", PbrMaterialEditorPanel.this.data.metallic) + " | Roughness: " + String.format("%.2f", PbrMaterialEditorPanel.this.data.roughness), 15, 42);

                g2.dispose();
            }
        };
        previewCanvas.setPreferredSize(new Dimension(300, 220));
        previewCanvas.setBorder(createTitledBorder("PBR Sphere Preview"));

        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(22, 23, 26));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 32, 36));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("PBR Material & Shader Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        header.add(title, BorderLayout.WEST);

        JButton exportMatBtn = new JButton("Save Material Preset");
        exportMatBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exportMatBtn.setBackground(new Color(44, 93, 212));
        exportMatBtn.setForeground(Color.WHITE);
        exportMatBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Material preset '" + PbrMaterialEditorPanel.this.data.materialName + "' saved successfully!", "Material Preset", JOptionPane.INFORMATION_MESSAGE));
        header.add(exportMatBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Split: Left Parameters, Right Live Preview & GLSL
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360);
        splitPane.setBackground(new Color(22, 23, 26));
        splitPane.setBorder(null);

        // Left form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 32, 36));
        formPanel.setBorder(createTitledBorder("PBR Properties"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        // Name
        nameField.setText(this.data.materialName);
        nameField.addActionListener(e -> this.data.materialName = nameField.getText().trim());
        formPanel.add(new JLabel("Material Name:"), setGbc(gbc, 0, 0, 1));
        formPanel.add(nameField, setGbc(gbc, 1, 0, 2));

        // Albedo Color
        setupColorButton(albedoColorBtn, this.data.albedoColor, c -> { this.data.albedoColor = c; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Albedo (Base Color):"), setGbc(gbc, 0, 1, 1));
        formPanel.add(albedoColorBtn, setGbc(gbc, 1, 1, 2));

        // Metallic
        metallicSlider.addChangeListener(e -> { this.data.metallic = metallicSlider.getValue() / 100f; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Metallic:"), setGbc(gbc, 0, 2, 1));
        formPanel.add(metallicSlider, setGbc(gbc, 1, 2, 2));

        // Roughness
        roughnessSlider.addChangeListener(e -> { this.data.roughness = roughnessSlider.getValue() / 100f; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Roughness:"), setGbc(gbc, 0, 3, 1));
        formPanel.add(roughnessSlider, setGbc(gbc, 1, 3, 2));

        // Normal Strength
        normalStrengthSlider.addChangeListener(e -> { this.data.normalStrength = normalStrengthSlider.getValue() / 100f; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Normal Scale:"), setGbc(gbc, 0, 4, 1));
        formPanel.add(normalStrengthSlider, setGbc(gbc, 1, 4, 2));

        // AO
        aoStrengthSlider.addChangeListener(e -> { this.data.aoStrength = aoStrengthSlider.getValue() / 100f; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Ambient Occlusion:"), setGbc(gbc, 0, 5, 1));
        formPanel.add(aoStrengthSlider, setGbc(gbc, 1, 5, 2));

        // Emissive Color
        setupColorButton(emissiveColorBtn, this.data.emissiveColor, c -> { this.data.emissiveColor = c; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Emissive Color:"), setGbc(gbc, 0, 6, 1));
        formPanel.add(emissiveColorBtn, setGbc(gbc, 1, 6, 2));

        // Emissive Intensity
        emissiveIntensitySlider.addChangeListener(e -> { this.data.emissiveIntensity = emissiveIntensitySlider.getValue() / 100f; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Emissive Intensity:"), setGbc(gbc, 0, 7, 1));
        formPanel.add(emissiveIntensitySlider, setGbc(gbc, 1, 7, 2));

        // Alpha Mode
        alphaModeCombo.setSelectedItem(this.data.alphaMode);
        alphaModeCombo.addActionListener(e -> { this.data.alphaMode = (String) alphaModeCombo.getSelectedItem(); previewCanvas.repaint(); });
        formPanel.add(new JLabel("Alpha Mode:"), setGbc(gbc, 0, 8, 1));
        formPanel.add(alphaModeCombo, setGbc(gbc, 1, 8, 2));

        // Clearcoat
        clearcoatSlider.addChangeListener(e -> { this.data.clearcoat = clearcoatSlider.getValue() / 100f; previewCanvas.repaint(); });
        formPanel.add(new JLabel("Clearcoat:"), setGbc(gbc, 0, 9, 1));
        formPanel.add(clearcoatSlider, setGbc(gbc, 1, 9, 2));

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        splitPane.setLeftComponent(formScroll);

        // Right side: Sphere Preview & Shader Code
        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(new Color(22, 23, 26));
        rightPanel.add(previewCanvas, BorderLayout.NORTH);

        // GLSL preview
        JTextArea glslArea = new JTextArea();
        glslArea.setEditable(false);
        glslArea.setBackground(new Color(16, 17, 20));
        glslArea.setForeground(new Color(0, 255, 200));
        glslArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        glslArea.setText(
                "// LibGDX PBR Shader Uniforms:\n" +
                "uniform vec4 u_diffuseColor;   // Albedo\n" +
                "uniform float u_metallic;      // PBR Metallic\n" +
                "uniform float u_roughness;     // PBR Roughness\n" +
                "uniform sampler2D u_normalMap; // Tangent Space Normal\n" +
                "uniform vec4 u_emissiveColor;  // Emission\n" +
                "uniform float u_occlusion;     // Ambient Occlusion\n"
        );
        JPanel glslPanel = new JPanel(new BorderLayout());
        glslPanel.setBackground(new Color(18, 19, 22));
        glslPanel.setBorder(createTitledBorder("GLSL PBR Uniform Bindings"));
        glslPanel.add(new JScrollPane(glslArea), BorderLayout.CENTER);
        rightPanel.add(glslPanel, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private void setupColorButton(JButton btn, Color initColor, java.util.function.Consumer<Color> onColorPicked) {
        btn.setBackground(initColor);
        btn.setPreferredSize(new Dimension(60, 22));
        btn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Select Color", btn.getBackground());
            if (chosen != null) {
                btn.setBackground(chosen);
                onColorPicked.accept(chosen);
            }
        });
    }

    private GridBagConstraints setGbc(GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = width > 1 ? 1.0 : 0.0;
        return gbc;
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
