package com.atomgdx.theme.windows;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.ui.DarkThemeUtils.CollapsibleSection;
import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.SceneItemInspectorPanel;
import com.atomgdx.theme.project.LibGdxProjectNode;
import com.atomgdx.viewer3d.Model3DDescriptor;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.ui.Model3DInspectorPanel;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for the Property & Component Inspector.
 * Docks on the Right side ("properties"), dynamically inspecting 3D Models & File Metadata (Read-Only banner),
 * 3D GameObjects (Editable), PBR Materials, 2D HyperLap Items, and Project Settings.
 */
public class InspectorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel wrapperPanel = new JPanel(new BorderLayout());

    private SceneItemInspectorPanel itemInspector2D;
    private Model3DInspectorPanel inspector3D;
    private SceneVO activeScene2D;

    public InspectorTopComponent() {
        setName("Inspector");
        setToolTipText("LibGDX Property, 3D GameObject, Model Stats & Component Inspector");
        setLayout(new BorderLayout());
        setBackground(DarkThemeUtils.BG_DARK);

        this.activeScene2D = new SceneVO("MainScene");
        itemInspector2D = new SceneItemInspectorPanel(this.activeScene2D);
        inspector3D = new Model3DInspectorPanel();

        add(wrapperPanel, BorderLayout.CENTER);
        showEmptySelection();
    }

    public void setScene2D(SceneVO scene) {
        this.activeScene2D = scene != null ? scene : new SceneVO("MainScene");
        if (itemInspector2D != null) {
            itemInspector2D = new SceneItemInspectorPanel(this.activeScene2D);
        }
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

        // 0. Read-Only Model Viewer Banner
        JPanel bannerPanel = new JPanel(new BorderLayout(8, 0));
        bannerPanel.setBackground(new Color(45, 38, 20));
        bannerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(234, 179, 8), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        bannerPanel.setMaximumSize(new Dimension(500, 52));

        JLabel noteIcon = new JLabel(DarkThemeUtils.getFatcowIcon("information.png"));
        JLabel noteText = new JLabel("<html><b>Read-Only Asset Preview</b><br/><font color='#d1d5db' size='2'>Inspecting raw 3D mesh asset. Use 3D Scene Editor to edit hierarchy, materials & physics.</font></html>");
        noteText.setForeground(new Color(253, 224, 71));
        noteText.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        bannerPanel.add(noteIcon, BorderLayout.WEST);
        bannerPanel.add(noteText, BorderLayout.CENTER);

        JPanel bannerWrap = new JPanel(new BorderLayout());
        bannerWrap.setOpaque(false);
        bannerWrap.setMaximumSize(new Dimension(500, 56));
        bannerWrap.add(bannerPanel, BorderLayout.CENTER);
        bannerWrap.add(Box.createVerticalStrut(4), BorderLayout.SOUTH);
        container.add(bannerWrap);

        // 1. File Metadata Section
        JPanel fileMeta = new JPanel();
        fileMeta.setLayout(new BoxLayout(fileMeta, BoxLayout.Y_AXIS));
        fileMeta.setOpaque(false);
        fileMeta.setBorder(new EmptyBorder(4, 6, 4, 6));

        File file = desc.getModelFile();
        String path = file != null ? file.getAbsolutePath() : "assets/models/" + desc.getName();
        long sizeKb = file != null && file.exists() ? (file.length() / 1024) : 142;

        fileMeta.add(createPropRow("File Name:", desc.getName()));
        fileMeta.add(createPropRow("Format:", desc.getFormat().name()));
        fileMeta.add(createPropRow("File Size:", sizeKb + " KB"));
        fileMeta.add(createPropRow("Path:", path));

        container.add(new CollapsibleSection("File Metadata", DarkThemeUtils.getFatcowIcon("page_white_magnify.png"), fileMeta, null));
        container.add(Box.createVerticalStrut(3));

        // 2. Model & Mesh Statistics Section
        JPanel meshStats = new JPanel();
        meshStats.setLayout(new BoxLayout(meshStats, BoxLayout.Y_AXIS));
        meshStats.setOpaque(false);
        meshStats.setBorder(new EmptyBorder(4, 6, 4, 6));

        meshStats.add(createPropRow("Total Meshes:", String.valueOf(Math.max(1, desc.getMeshCount()))));
        meshStats.add(createPropRow("Nodes:", String.valueOf(Math.max(1, desc.getNodeCount()))));
        meshStats.add(createPropRow("Materials:", String.valueOf(Math.max(1, desc.getMaterialCount())) + " (PBR)"));
        meshStats.add(createPropRow("Animations:", String.valueOf(desc.getAnimationCount())));
        meshStats.add(createPropRow("Shading Model:", "PBR Metallic-Roughness"));

        container.add(new CollapsibleSection("Model Statistics", DarkThemeUtils.getFatcowIcon("box.png"), meshStats, null));
        container.add(Box.createVerticalStrut(3));

        // 3. Viewport Display Options Section
        JPanel displayOpts = new JPanel();
        displayOpts.setLayout(new BoxLayout(displayOpts, BoxLayout.Y_AXIS));
        displayOpts.setOpaque(false);
        displayOpts.setBorder(new EmptyBorder(4, 6, 4, 6));

        JCheckBox pbrBox = new JCheckBox("Enable PBR Lighting", desc.isEnablePbr());
        JCheckBox gridBox = new JCheckBox("Show Ground Grid", desc.isShowGrid());
        JCheckBox wireBox = new JCheckBox("Wireframe Overlay", desc.isWireframe());
        JCheckBox bonesBox = new JCheckBox("Show Skeleton Bones", desc.isShowBones());

        for (JCheckBox cb : new JCheckBox[]{pbrBox, gridBox, wireBox, bonesBox}) {
            cb.setOpaque(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cb.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            displayOpts.add(cb);
        }

        container.add(new CollapsibleSection("Viewport Display Options", DarkThemeUtils.getFatcowIcon("cog.png"), displayOpts, null));
        container.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(container);
        sp.setBorder(null);
        sp.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        wrapperPanel.add(sp, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    public void inspectItem(MainItemVO item) {
        if (item == null) {
            showEmptySelection();
            return;
        }
        setName("Inspector - " + (item.itemName != null ? item.itemName : "2D Item"));
        wrapperPanel.removeAll();
        if (itemInspector2D == null) {
            itemInspector2D = new SceneItemInspectorPanel(activeScene2D);
        }
        itemInspector2D.setItem(item);
        wrapperPanel.add(itemInspector2D, BorderLayout.CENTER);
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

    @Override
    public void resultChanged(LookupEvent ev) {
        if (lookupResult == null) return;
        java.util.Collection<?> selected = lookupResult.allInstances();
        for (Object obj : selected) {
            if (obj instanceof Model3DDescriptor) {
                inspectModelDescriptor((Model3DDescriptor) obj);
                return;
            } else if (obj instanceof Node3DVO) {
                inspectNode3D((Node3DVO) obj);
                return;
            } else if (obj instanceof Material3DVO) {
                inspectMaterial((Material3DVO) obj);
                return;
            } else if (obj instanceof MainItemVO) {
                inspectItem((MainItemVO) obj);
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
        r.setMaximumSize(new Dimension(500, 20));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(85, 20));

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

        JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
        p.setBackground(DarkThemeUtils.BG_PANEL);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("File: " + file.getName()));
        p.add(new JLabel("Path: " + file.getAbsolutePath()));
        p.add(new JLabel("Size: " + (file.length() / 1024) + " KB"));
        wrapperPanel.add(p, BorderLayout.NORTH);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    private void inspectProject(LibGdxProject project) {
        wrapperPanel.removeAll();
        setName("Inspector - " + project.getName());

        JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
        p.setBackground(DarkThemeUtils.BG_PANEL);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("Project: " + project.getName()));
        p.add(new JLabel("Java: " + project.getJavaVersion()));
        p.add(new JLabel("LibGDX: " + project.getGdxVersion()));
        wrapperPanel.add(p, BorderLayout.NORTH);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    private void showEmptySelection() {
        setName("Inspector");
        wrapperPanel.removeAll();
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setBackground(DarkThemeUtils.BG_DARK);

        JLabel msg = new JLabel("No item or 3D node selected", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(DarkThemeUtils.TEXT_MUTED);
        empty.add(msg);

        wrapperPanel.add(empty, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "InspectorTopComponent";
    }
}
