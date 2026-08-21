package com.atomgdx.theme.windows;

import com.atomgdx.theme.SciFiColors;
import com.atomgdx.viewer3d.Model3DDescriptor;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for inspecting 3D Mesh Geometry, PBR Materials, Bones, and Animations.
 * Docks on the right side ("properties").
 */
public class ModelPropertiesTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel contentPanel = new JPanel();
    private final JLabel headerTitle = new JLabel("3D Model Properties");

    public ModelPropertiesTopComponent() {
        setName("3D Model Properties");
        setToolTipText("LibGDX 3D GLTF / Mesh & Material Properties");
        setLayout(new BorderLayout());
        setBackground(SciFiColors.BG_DARKEST);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SciFiColors.BG_PANEL);
        header.setBorder(new EmptyBorder(6, 10, 6, 10));
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerTitle.setForeground(SciFiColors.ACCENT_CYAN);
        header.add(headerTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Content
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(SciFiColors.BG_DARKEST);
        contentPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SciFiColors.BG_DARKEST);
        add(scrollPane, BorderLayout.CENTER);

        // Default demo inspection
        inspectModel(new Model3DDescriptor(new File("G:\\GameDev\\LibGDX\\AtomGdx\\AtomGdxNB\\Workspace\\NeonCosmos\\assets\\models\\spacefighter.gltf")));
    }

    @Override
    protected void componentOpened() {
        super.componentOpened();
        lookupResult = Utilities.actionsGlobalContext().lookupResult(Object.class);
        lookupResult.addLookupListener(this);
        resultChanged(new LookupEvent(lookupResult));
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();
        if (lookupResult != null) {
            lookupResult.removeLookupListener(this);
            lookupResult = null;
        }
    }

    private Object currentlyInspected = null;

    @Override
    public void resultChanged(LookupEvent ev) {
        Lookup.Result<?> res = (ev != null && ev.getSource() instanceof Lookup.Result) ? (Lookup.Result<?>) ev.getSource() : lookupResult;
        if (res == null) return;
        java.util.Collection<?> instances = res.allInstances();
        if (instances.isEmpty()) {
            return;
        }
        for (Object obj : instances) {
            if (obj == currentlyInspected) return;
            if (obj instanceof Model3DDescriptor) {
                currentlyInspected = obj;
                inspectModel((Model3DDescriptor) obj);
                return;
            }
        }
    }

    private void inspectModel(Model3DDescriptor model) {
        headerTitle.setText("3D Model: " + model.getName());
        contentPanel.removeAll();

        // Mesh Geometry Section
        JPanel meshBox = createSection("Mesh Geometry");
        addField(meshBox, "Mesh Name", model.getName());
        addField(meshBox, "Vertex Count", "14,820");
        addField(meshBox, "Index Count", "28,640");
        addField(meshBox, "Submeshes", "3 (Hull, Cockpit, Thrusters)");
        addField(meshBox, "Bounding Box", "4.2m x 1.8m x 6.5m");
        contentPanel.add(meshBox);
        contentPanel.add(Box.createVerticalStrut(8));

        // PBR Material Section
        JPanel matBox = createSection("PBR Material (Physically Based)");
        addField(matBox, "Shader Type", "PBRMetallicRoughness");
        addField(matBox, "Albedo Map", "textures/ship_albedo.png");
        addField(matBox, "Normal Map", "textures/ship_normal.png");
        addField(matBox, "Roughness", "0.35");
        addField(matBox, "Metallic", "0.85");
        addField(matBox, "Emissive Map", "textures/ship_glow.png");
        contentPanel.add(matBox);
        contentPanel.add(Box.createVerticalStrut(8));

        // Skeleton & Animations Section
        JPanel animBox = createSection("Bones & Skeletal Animations");
        addField(animBox, "Bone Nodes", "12 Bones (Wings, LandingGear)");
        addField(animBox, "Animations", "Fly_Idle, Thruster_Boost, Wings_Fold");
        addField(animBox, "Active Clip", "Fly_Idle (60 FPS, Loop)");
        contentPanel.add(animBox);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 4));
        panel.setBackground(SciFiColors.BG_CARD);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, panel.getPreferredSize().height));
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                title
        );
        border.setTitleColor(SciFiColors.TEXT_PRIMARY);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        panel.setBorder(border);
        return panel;
    }

    private void addField(JPanel panel, String label, String val) {
        JLabel lbl = new JLabel(" " + label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(SciFiColors.TEXT_SECONDARY);

        JTextField txt = new JTextField(val);
        txt.setPreferredSize(new Dimension(80, 24));
        txt.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
        txt.setBackground(SciFiColors.BG_DARK);
        txt.setForeground(SciFiColors.TEXT_PRIMARY);
        txt.setCaretColor(SciFiColors.ACCENT_CYAN);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                new EmptyBorder(2, 4, 2, 4)
        ));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        panel.add(lbl);
        panel.add(txt);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "ModelPropertiesTopComponent";
    }
}
