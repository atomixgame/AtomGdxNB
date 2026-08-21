package com.atomgdx.viewer3d.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.Serializable;

/**
 * Visual Editor for LibGDX 3D Environment, HDRI Skybox IBL, Directional Sun, and Shadows.
 */
public class EnvironmentLightingPanel extends JPanel {

    public static class EnvironmentData implements Serializable {
        public String hdriPreset = "SciFi_Nebula_Cosmos";
        public float iblIntensity = 1.2f;
        public Color ambientColor = new Color(35, 40, 50);
        public Color sunColor = new Color(255, 245, 230);
        public float sunIntensity = 2.0f;
        public int sunPitch = 45;
        public int sunYaw = 135;
        public boolean enableShadows = true;
        public int shadowMapSize = 2048;
        public float shadowBias = 0.005f;
        public boolean enableFog = false;
        public Color fogColor = new Color(20, 22, 28);
        public float fogEnd = 150f;
    }

    private final EnvironmentData data;
    private final JComboBox<String> hdriCombo = new JComboBox<>(new String[]{
            "SciFi Nebula Cosmos",
            "Space Station Orbit",
            "Cyberpunk Night City",
            "Sunny Studio Warm",
            "Sunset Horizon Deep",
            "Deep Void Dark"
    });
    private final JSlider iblSlider = new JSlider(0, 500, 120);
    private final JButton ambientColorBtn = new JButton();
    private final JButton sunColorBtn = new JButton();
    private final JSlider sunIntensitySlider = new JSlider(0, 500, 200);
    private final JSlider sunPitchSlider = new JSlider(0, 90, 45);
    private final JSlider sunYawSlider = new JSlider(0, 360, 135);
    private final JCheckBox shadowCheckBox = new JCheckBox("Cast Dynamic Shadows", true);
    private final JComboBox<Integer> shadowResCombo = new JComboBox<>(new Integer[]{512, 1024, 2048, 4096});
    private final JCheckBox fogCheckBox = new JCheckBox("Enable Distance Fog", false);
    private final JButton fogColorBtn = new JButton();

    private final JPanel sunCompassCanvas;

    public EnvironmentLightingPanel() {
        this(new EnvironmentData());
    }

    public EnvironmentLightingPanel(EnvironmentData data) {
        this.data = data != null ? data : new EnvironmentData();

        sunCompassCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(14, 15, 18));
                g2.fillRect(0, 0, w, h);

                int cx = w / 2;
                int cy = h / 2;
                int r = Math.min(w, h) / 3;

                // Compass circle
                g2.setColor(new Color(40, 43, 50));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(new Color(60, 65, 75));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.drawLine(cx - r, cy, cx + r, cy);
                g2.drawLine(cx, cy - r, cx, cy + r);

                // Compass directions
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(new Color(0, 220, 255));
                g2.drawString("N (Z+)", cx - 12, cy - r + 14);
                g2.drawString("S (Z-)", cx - 12, cy + r - 6);
                g2.drawString("E (X+)", cx + r - 30, cy + 4);
                g2.drawString("W (X-)", cx - r + 6, cy + 4);

                // Sun Direction Vector
                double radYaw = Math.toRadians(EnvironmentLightingPanel.this.data.sunYaw);
                double radPitch = Math.toRadians(EnvironmentLightingPanel.this.data.sunPitch);
                int sunX = cx + (int) (Math.sin(radYaw) * Math.cos(radPitch) * r * 0.85);
                int sunY = cy - (int) (Math.cos(radYaw) * Math.cos(radPitch) * r * 0.85);

                // Sun ray
                g2.setColor(new Color(255, 200, 50, 180));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(cx, cy, sunX, sunY);

                // Sun disc
                g2.setColor(EnvironmentLightingPanel.this.data.sunColor);
                g2.fillOval(sunX - 10, sunY - 10, 20, 20);
                g2.setColor(new Color(255, 180, 0));
                g2.drawOval(sunX - 10, sunY - 10, 20, 20);

                // Labels
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString("Sun Position: Yaw " + EnvironmentLightingPanel.this.data.sunYaw + "° | Pitch " + EnvironmentLightingPanel.this.data.sunPitch + "°", 12, 22);

                g2.dispose();
            }
        };
        sunCompassCanvas.setBorder(createTitledBorder("Directional Sun Orbit Compass"));

        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(22, 23, 26));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 32, 36));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Environment & HDRI Lighting Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        header.add(title, BorderLayout.WEST);

        JButton applyBtn = new JButton("Apply Lighting Setup");
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        applyBtn.setBackground(new Color(44, 93, 212));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Environment lighting parameters applied to 3D Viewport!", "Environment Studio", JOptionPane.INFORMATION_MESSAGE));
        header.add(applyBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Split: Left Parameters, Right Sun Compass & Skybox View
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setBackground(new Color(22, 23, 26));
        splitPane.setBorder(null);

        // Left Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 32, 36));
        formPanel.setBorder(createTitledBorder("Lighting & Skybox Settings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 6, 3, 6);

        // HDRI Preset
        formPanel.add(new JLabel("HDRI Skybox:"), setGbc(gbc, 0, 0, 1));
        hdriCombo.addActionListener(e -> { this.data.hdriPreset = (String) hdriCombo.getSelectedItem(); sunCompassCanvas.repaint(); });
        formPanel.add(hdriCombo, setGbc(gbc, 1, 0, 2));

        // IBL Intensity
        iblSlider.addChangeListener(e -> this.data.iblIntensity = iblSlider.getValue() / 100f);
        formPanel.add(new JLabel("IBL Specular/Diffuse:"), setGbc(gbc, 0, 1, 1));
        formPanel.add(iblSlider, setGbc(gbc, 1, 1, 2));

        // Ambient Color
        setupColorButton(ambientColorBtn, this.data.ambientColor, c -> this.data.ambientColor = c);
        formPanel.add(new JLabel("Ambient Base:"), setGbc(gbc, 0, 2, 1));
        formPanel.add(ambientColorBtn, setGbc(gbc, 1, 2, 2));

        // Sun Color
        setupColorButton(sunColorBtn, this.data.sunColor, c -> { this.data.sunColor = c; sunCompassCanvas.repaint(); });
        formPanel.add(new JLabel("Sun Directional Color:"), setGbc(gbc, 0, 3, 1));
        formPanel.add(sunColorBtn, setGbc(gbc, 1, 3, 2));

        // Sun Intensity
        sunIntensitySlider.addChangeListener(e -> this.data.sunIntensity = sunIntensitySlider.getValue() / 100f);
        formPanel.add(new JLabel("Sun Intensity:"), setGbc(gbc, 0, 4, 1));
        formPanel.add(sunIntensitySlider, setGbc(gbc, 1, 4, 2));

        // Sun Pitch
        sunPitchSlider.addChangeListener(e -> { this.data.sunPitch = sunPitchSlider.getValue(); sunCompassCanvas.repaint(); });
        formPanel.add(new JLabel("Sun Pitch Angle:"), setGbc(gbc, 0, 5, 1));
        formPanel.add(sunPitchSlider, setGbc(gbc, 1, 5, 2));

        // Sun Yaw
        sunYawSlider.addChangeListener(e -> { this.data.sunYaw = sunYawSlider.getValue(); sunCompassCanvas.repaint(); });
        formPanel.add(new JLabel("Sun Yaw Angle:"), setGbc(gbc, 0, 6, 1));
        formPanel.add(sunYawSlider, setGbc(gbc, 1, 6, 2));

        // Shadows
        shadowCheckBox.setOpaque(false);
        shadowCheckBox.setForeground(Color.WHITE);
        shadowCheckBox.addActionListener(e -> this.data.enableShadows = shadowCheckBox.isSelected());
        formPanel.add(shadowCheckBox, setGbc(gbc, 0, 7, 1));

        shadowResCombo.setSelectedItem(this.data.shadowMapSize);
        shadowResCombo.addActionListener(e -> this.data.shadowMapSize = (Integer) shadowResCombo.getSelectedItem());
        formPanel.add(shadowResCombo, setGbc(gbc, 1, 7, 2));

        // Fog
        fogCheckBox.setOpaque(false);
        fogCheckBox.setForeground(Color.WHITE);
        fogCheckBox.addActionListener(e -> this.data.enableFog = fogCheckBox.isSelected());
        formPanel.add(fogCheckBox, setGbc(gbc, 0, 8, 1));

        setupColorButton(fogColorBtn, this.data.fogColor, c -> this.data.fogColor = c);
        formPanel.add(fogColorBtn, setGbc(gbc, 1, 8, 2));

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        splitPane.setLeftComponent(formScroll);

        // Right side
        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(new Color(22, 23, 26));
        rightPanel.add(sunCompassCanvas, BorderLayout.CENTER);

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
                sunCompassCanvas.repaint();
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
