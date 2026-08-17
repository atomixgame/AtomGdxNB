package com.atomgdx.theme.windows;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.SceneItemInspectorPanel;
import com.atomgdx.theme.project.LibGdxProjectNode;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.ui.Model3DInspectorPanel;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for the Property & Component Inspector.
 * Docks on the Right side ("properties"), dynamically switching between 2D HyperLap Items,
 * 3D GameObjects / SceneGraph Nodes, PBR Materials, and Project Settings.
 */
public class InspectorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel wrapperPanel = new JPanel(new BorderLayout());

    private SceneItemInspectorPanel itemInspector2D;
    private Model3DInspectorPanel inspector3D;
    private SceneVO activeScene2D;

    public InspectorTopComponent() {
        setName("Inspector");
        setToolTipText("LibGDX Property, 3D GameObject & Component Inspector");
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
            if (obj instanceof Node3DVO) {
                inspectNode3D((Node3DVO) obj);
                return;
            } else if (obj instanceof Material3DVO) {
                inspectMaterial((Material3DVO) obj);
                return;
            } else if (obj instanceof MainItemVO) {
                inspectItem((MainItemVO) obj);
                return;
            } else if (obj instanceof File) {
                inspectFileAsset((File) obj);
                return;
            } else if (obj instanceof LibGdxProject) {
                inspectProject((LibGdxProject) obj);
                return;
            }
        }
        showEmptySelection();
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
