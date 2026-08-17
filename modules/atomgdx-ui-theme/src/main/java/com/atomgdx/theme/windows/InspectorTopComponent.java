package com.atomgdx.theme.windows;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.SceneItemInspectorPanel;
import com.atomgdx.theme.project.LibGdxProjectNode;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for the Property & Component Inspector.
 * Docks on the Right side ("properties") inspecting any active LibGdxProject, File, or HyperLap2D Scene Item.
 */
public class InspectorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel contentPanel = new JPanel();
    private final JLabel headerTitle = new JLabel("Nothing Selected");
    private final JLabel headerIcon = new JLabel();

    public InspectorTopComponent() {
        setName("Inspector");
        setToolTipText("LibGDX Property & Component Inspector");
        setLayout(new BorderLayout());
        setBackground(new Color(30, 31, 34));

        // Header
        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setBackground(new Color(43, 45, 48));
        header.setBorder(new EmptyBorder(6, 8, 6, 8));
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerTitle.setForeground(new Color(223, 225, 229));
        header.add(headerIcon, BorderLayout.WEST);
        header.add(headerTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Content
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(30, 31, 34));
        contentPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 31, 34));
        add(scrollPane, BorderLayout.CENTER);

        showEmptySelection();
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
            if (obj instanceof MainItemVO) {
                inspectSceneItem((MainItemVO) obj);
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

    private void inspectSceneItem(MainItemVO item) {
        headerTitle.setText("Item: " + (item.itemName != null && !item.itemName.isEmpty() ? item.itemName : "Item #" + item.uniqueId));
        headerIcon.setIcon(new ImageIcon(LibGdxProjectNode.getCustomIcon("star.png")));
        contentPanel.removeAll();

        // Transform Section
        JPanel transformP = createSection("Transform & Identity");
        transformP.add(createPropRow("Name:", item.itemName));
        transformP.add(createPropRow("Layer:", item.layerName));
        transformP.add(createPropRow("Position:", "(" + item.x + ", " + item.y + ")"));
        transformP.add(createPropRow("Scale:", "(" + item.scaleX + ", " + item.scaleY + ")"));
        transformP.add(createPropRow("Rotation:", item.rotation + "°"));
        transformP.add(createPropRow("Origin:", "(" + item.originX + ", " + item.originY + ")"));
        transformP.add(createPropRow("Z-Index:", String.valueOf(item.zIndex)));
        transformP.add(createPropRow("Visible:", item.isVisible ? "Yes" : "No"));
        contentPanel.add(transformP);

        // Box2D Physics Section
        if (item.physics != null) {
            contentPanel.add(Box.createVerticalStrut(6));
            JPanel physP = createSection("Box2D Physics Body");
            String[] types = {"Static", "Kinematic", "Dynamic"};
            physP.add(createPropRow("Body Type:", types[Math.max(0, Math.min(2, item.physics.bodyType))]));
            physP.add(createPropRow("Density:", String.valueOf(item.physics.density)));
            physP.add(createPropRow("Friction:", String.valueOf(item.physics.friction)));
            physP.add(createPropRow("Restitution:", String.valueOf(item.physics.restitution)));
            physP.add(createPropRow("Sensor:", item.physics.sensor ? "True" : "False"));
            physP.add(createPropRow("Bullet:", item.physics.bullet ? "True" : "False"));
            contentPanel.add(physP);
        }

        // Lighting Section
        if (item instanceof LightVO) {
            LightVO lt = (LightVO) item;
            contentPanel.add(Box.createVerticalStrut(6));
            JPanel ltP = createSection("Light Properties");
            ltP.add(createPropRow("Light Type:", lt.type.name()));
            ltP.add(createPropRow("Rays:", String.valueOf(lt.rays)));
            ltP.add(createPropRow("Distance:", lt.distance + " px"));
            contentPanel.add(ltP);
        }

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void inspectFileAsset(File file) {
        headerTitle.setText("Asset: " + file.getName());
        headerIcon.setIcon(new ImageIcon(LibGdxProjectNode.getCustomIcon("picture.png")));
        contentPanel.removeAll();

        JPanel p = createSection("File Details");
        p.add(createPropRow("File Name:", file.getName()));
        p.add(createPropRow("Size:", (file.length() / 1024) + " KB"));
        p.add(createPropRow("Location:", file.getParent()));
        p.add(createPropRow("Writable:", file.canWrite() ? "Yes" : "No"));
        contentPanel.add(p);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void inspectProject(LibGdxProject project) {
        headerTitle.setText("Project: " + project.getName());
        headerIcon.setIcon(new ImageIcon(LibGdxProjectNode.getCustomIcon("folder.png")));
        contentPanel.removeAll();

        JPanel p = createSection("Project Overview");
        p.add(createPropRow("Game Name:", project.getName()));
        p.add(createPropRow("LibGDX Version:", project.getGdxVersion()));
        p.add(createPropRow("Package:", project.getPackageName()));
        p.add(createPropRow("Root Path:", project.getRootDirectory().getAbsolutePath()));
        contentPanel.add(p);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSection(String title) {
        JPanel p = new JPanel(new GridLayout(0, 1, 2, 2));
        p.setBackground(new Color(43, 45, 48));
        TitledBorder tb = BorderFactory.createTitledBorder(new LineBorder(new Color(60, 63, 65), 1), title);
        tb.setTitleColor(new Color(223, 225, 229));
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        p.setBorder(BorderFactory.createCompoundBorder(tb, new EmptyBorder(4, 6, 4, 6)));
        return p;
    }

    private JPanel createPropRow(String label, String value) {
        JPanel r = new JPanel(new BorderLayout(4, 0));
        r.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(154, 160, 166));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setPreferredSize(new Dimension(80, 20));

        JLabel val = new JLabel(value != null ? value : "");
        val.setForeground(new Color(223, 225, 229));
        val.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        r.add(lbl, BorderLayout.WEST);
        r.add(val, BorderLayout.CENTER);
        return r;
    }

    private void showEmptySelection() {
        headerTitle.setText("No Component Selected");
        headerIcon.setIcon(null);
        contentPanel.removeAll();

        JLabel info = new JLabel("<html><center style='color:#9aa0a6;'>Select an item in <b>Projects</b> or <b>Scene Structure</b> to inspect its properties.</center></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(info);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
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
