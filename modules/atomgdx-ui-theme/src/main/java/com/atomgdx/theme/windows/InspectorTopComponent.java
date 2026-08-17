package com.atomgdx.theme.windows;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.ui.DarkThemeUtils.CollapsibleSection;
import com.atomgdx.viewer3d.Model3DDescriptor;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.ui.Model3DInspectorPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

/**
 * Universal Inspector TopComponent that reacts to global Lookup selections across
 * 3D Scenes, 3D GLTF/GLB Models, and Project Assets.
 */
@TopComponent.Description(preferredID = "InspectorTopComponent", iconBase = "com/atomgdx/theme/icons/inspector.png", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "com.atomgdx.theme.windows.InspectorTopComponent")
@ActionReference(path = "Menu/Window", position = 300)
@TopComponent.OpenActionRegistration(displayName = "#CTL_InspectorAction", preferredID = "InspectorTopComponent")
@Messages({
        "CTL_InspectorAction=Inspector",
        "CTL_InspectorTopComponent=Inspector",
        "HINT_InspectorTopComponent=Universal Object, Asset & Property Inspector"
})
public final class InspectorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel wrapperPanel = new JPanel(new BorderLayout());
    private Model3DInspectorPanel inspector3D;

    public InspectorTopComponent() {
        setName(Bundle.CTL_InspectorTopComponent());
        setToolTipText(Bundle.HINT_InspectorTopComponent());
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        wrapperPanel.setBackground(DarkThemeUtils.BG_DARK);
        wrapperPanel.setBorder(null);

        add(wrapperPanel, BorderLayout.CENTER);
        showEmptySelection();
    }

    public void inspectNode3D(Node3DVO node) {
        if (node == null) {
            showEmptySelection();
            return;
        }
        setName("Inspector - " + node.nodeName);
        wrapperPanel.removeAll();
        if (inspector3D == null) {
            inspector3D = new Model3DInspectorPanel();
        }
        inspector3D.setNode(node);
        wrapperPanel.add(inspector3D, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    public void inspectModelDescriptor(Model3DDescriptor desc) {
        if (desc == null) {
            showEmptySelection();
            return;
        }
        setName("Inspector - " + desc.getName());
        wrapperPanel.removeAll();

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(DarkThemeUtils.BG_DARK);
        container.setBorder(new EmptyBorder(4, 4, 4, 4));

        // 0. Read-Only Model Viewer Banner (Full-Width, Left-Aligned)
        JPanel bannerPanel = new JPanel(new BorderLayout(8, 0));
        bannerPanel.setBackground(new Color(45, 38, 20));
        bannerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(234, 179, 8), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        bannerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noteIcon = new JLabel(DarkThemeUtils.getFatcowIcon("information.png"));
        JLabel noteText = new JLabel("<html><b>Read-Only Asset Preview</b><br/><font color='#d1d5db' size='2'>Inspecting raw 3D mesh asset. Use 3D Scene Editor to edit hierarchy, materials & physics.</font></html>");
        noteText.setForeground(new Color(253, 224, 71));
        noteText.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        bannerPanel.add(noteIcon, BorderLayout.WEST);
        bannerPanel.add(noteText, BorderLayout.CENTER);

        JPanel bannerWrap = new JPanel(new BorderLayout());
        bannerWrap.setOpaque(false);
        bannerWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        bannerWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        bannerWrap.add(bannerPanel, BorderLayout.CENTER);
        bannerWrap.add(Box.createVerticalStrut(4), BorderLayout.SOUTH);
        container.add(bannerWrap);

        // 1. File Metadata Section
        JPanel fileMeta = new JPanel();
        fileMeta.setLayout(new BoxLayout(fileMeta, BoxLayout.Y_AXIS));
        fileMeta.setOpaque(false);
        fileMeta.setBorder(new EmptyBorder(4, 6, 4, 6));
        fileMeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        File file = desc.getModelFile();
        String path = file != null ? file.getAbsolutePath() : "assets/models/" + desc.getName();
        long sizeKb = file != null && file.exists() ? (file.length() / 1024) : 142;

        fileMeta.add(createPropRow("File Name:", desc.getName()));
        fileMeta.add(createPropRow("Format:", desc.getFormat().name()));
        fileMeta.add(createPropRow("File Size:", sizeKb + " KB"));
        fileMeta.add(createPropRow("Path:", path));

        CollapsibleSection metaSection = new CollapsibleSection("File Metadata", DarkThemeUtils.getFatcowIcon("page_white_magnify.png"), fileMeta, null);
        metaSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        container.add(metaSection);
        container.add(Box.createVerticalStrut(3));

        // 2. Model & Mesh Statistics Section
        JPanel meshStats = new JPanel();
        meshStats.setLayout(new BoxLayout(meshStats, BoxLayout.Y_AXIS));
        meshStats.setOpaque(false);
        meshStats.setBorder(new EmptyBorder(4, 6, 4, 6));
        meshStats.setAlignmentX(Component.LEFT_ALIGNMENT);

        meshStats.add(createPropRow("Total Meshes:", String.valueOf(Math.max(1, desc.getMeshCount()))));
        meshStats.add(createPropRow("Nodes:", String.valueOf(Math.max(1, desc.getNodeCount()))));
        meshStats.add(createPropRow("Materials:", String.valueOf(Math.max(1, desc.getMaterialCount())) + " (PBR)"));
        meshStats.add(createPropRow("Animations:", String.valueOf(desc.getAnimationCount())));
        meshStats.add(createPropRow("Shading Model:", "PBR Metallic-Roughness"));

        CollapsibleSection statsSection = new CollapsibleSection("Model Statistics", DarkThemeUtils.getFatcowIcon("box.png"), meshStats, null);
        statsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        container.add(statsSection);
        container.add(Box.createVerticalStrut(3));

        // 3. Viewport Display Options Section
        JPanel displayOpts = new JPanel();
        displayOpts.setLayout(new BoxLayout(displayOpts, BoxLayout.Y_AXIS));
        displayOpts.setOpaque(false);
        displayOpts.setBorder(new EmptyBorder(4, 6, 4, 6));
        displayOpts.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox pbrBox = new JCheckBox("Enable PBR Lighting", desc.isEnablePbr());
        JCheckBox gridBox = new JCheckBox("Show Ground Grid", desc.isShowGrid());
        JCheckBox wireBox = new JCheckBox("Wireframe Overlay", desc.isWireframe());
        JCheckBox bonesBox = new JCheckBox("Show Skeleton Bones", desc.isShowBones());

        for (JCheckBox cb : new JCheckBox[]{pbrBox, gridBox, wireBox, bonesBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            displayOpts.add(cb);
        }

        CollapsibleSection optsSection = new CollapsibleSection("Viewport Display Options", DarkThemeUtils.getFatcowIcon("cog.png"), displayOpts, null);
        optsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        optsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        container.add(optsSection);
        container.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(container);
        sp.setBorder(null);
        sp.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        wrapperPanel.add(sp, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    public void inspectMaterial(Material3DVO material) {
        if (material == null) {
            showEmptySelection();
            return;
        }
        setName("Inspector - Material: " + material.materialName);
        wrapperPanel.removeAll();

        Node3DVO previewNode = new Node3DVO(material.materialName, Node3DVO.MeshShape.SPHERE, 0, 0, 0);
        previewNode.material = material;
        if (inspector3D == null) {
            inspector3D = new Model3DInspectorPanel();
        }
        inspector3D.setNode(previewNode);
        wrapperPanel.add(inspector3D, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    public void inspectProject(LibGdxProject project) {
        if (project == null) {
            showEmptySelection();
            return;
        }
        setName("Inspector - Project: " + project.getName());
        wrapperPanel.removeAll();

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(DarkThemeUtils.BG_DARK);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel(project.getName(), DarkThemeUtils.getFatcowIcon("application_view_tile.png"), JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        JPanel details = new JPanel(new GridLayout(4, 1, 4, 4));
        details.setOpaque(false);
        details.setBorder(new EmptyBorder(8, 0, 0, 0));
        details.add(new JLabel("Root: " + (project.getRootDirectory() != null ? project.getRootDirectory().getPath() : "Workspace")));
        details.add(new JLabel("Build System: Gradle"));
        details.add(new JLabel("LibGDX Version: 1.13.1"));
        details.add(new JLabel("Target Platforms: Desktop (LWJGL3), Android, Web"));

        for (Component c : details.getComponents()) {
            c.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            c.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        }

        p.add(title, BorderLayout.NORTH);
        p.add(details, BorderLayout.CENTER);

        wrapperPanel.add(p, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    private void showEmptySelection() {
        setName("Inspector");
        wrapperPanel.removeAll();
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setBackground(DarkThemeUtils.BG_DARK);

        JLabel lbl = new JLabel("Select an asset, node, or file to inspect properties", DarkThemeUtils.getFatcowIcon("magnifier.png"), JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setForeground(DarkThemeUtils.TEXT_MUTED);

        empty.add(lbl);
        wrapperPanel.add(empty, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (lookupResult == null) return;
        for (Object obj : lookupResult.allInstances()) {
            if (obj instanceof Model3DDescriptor) {
                inspectModelDescriptor((Model3DDescriptor) obj);
                return;
            } else if (obj instanceof Node3DVO) {
                inspectNode3D((Node3DVO) obj);
                return;
            } else if (obj instanceof Material3DVO) {
                inspectMaterial((Material3DVO) obj);
                return;
            } else if (obj instanceof File) {
                File file = (File) obj;
                String name = file.getName().toLowerCase();
                if (name.endsWith(".gltf") || name.endsWith(".glb") || name.endsWith(".obj") || name.endsWith(".g3db") || name.endsWith(".g3dj")) {
                    inspectModelDescriptor(new Model3DDescriptor(file));
                } else {
                    inspectFileAsset(file);
                }
                return;
            } else if (obj instanceof LibGdxProject) {
                inspectProject((LibGdxProject) obj);
                return;
            }
        }
        showEmptySelection();
    }

    private JPanel createPropRow(String label, String value) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(95, 20));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        val.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        r.add(lbl, BorderLayout.WEST);
        r.add(val, BorderLayout.CENTER);
        return r;
    }

    private void inspectFileAsset(File file) {
        wrapperPanel.removeAll();
        setName("Inspector - " + file.getName());

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(DarkThemeUtils.BG_DARK);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel(file.getName(), DarkThemeUtils.getFatcowIcon("page_white.png"), JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        JPanel details = new JPanel(new GridLayout(3, 1, 4, 4));
        details.setOpaque(false);
        details.setBorder(new EmptyBorder(8, 0, 0, 0));
        details.add(new JLabel("Size: " + (file.length() / 1024) + " KB"));
        details.add(new JLabel("Path: " + file.getAbsolutePath()));
        details.add(new JLabel("Type: " + (file.isDirectory() ? "Directory" : "File")));

        for (Component c : details.getComponents()) {
            c.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            c.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        }

        p.add(title, BorderLayout.NORTH);
        p.add(details, BorderLayout.CENTER);

        wrapperPanel.add(p, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
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
}
