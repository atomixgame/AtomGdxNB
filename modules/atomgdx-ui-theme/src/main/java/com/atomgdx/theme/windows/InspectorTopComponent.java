package com.atomgdx.theme.windows;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.editor.scene2d.SceneItem;
import com.atomgdx.theme.SciFiColors;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * NetBeans TopComponent for the Property & Component Inspector.
 * Docks on the right side ("properties") and listens to NetBeans Global Lookup.
 */
public class InspectorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel contentPanel = new JPanel();
    private final JLabel headerTitle = new JLabel("Nothing Selected");

    public InspectorTopComponent() {
        setName("Inspector");
        setToolTipText("LibGDX Property & Component Inspector");
        setLayout(new BorderLayout());
        setBackground(SciFiColors.BG_DARKEST);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SciFiColors.BG_PANEL);
        header.setBorder(new EmptyBorder(8, 12, 8, 12));
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerTitle.setForeground(SciFiColors.ACCENT_CYAN);
        header.add(headerTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Content
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(SciFiColors.BG_DARKEST);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SciFiColors.BG_DARKEST);
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
            if (obj instanceof SceneItem) {
                inspectSceneItem((SceneItem) obj);
                return;
            } else if (obj instanceof LibGdxProject) {
                inspectProject((LibGdxProject) obj);
                return;
            }
        }
        showEmptySelection();
    }

    private void showEmptySelection() {
        headerTitle.setText("No Component Selected");
        contentPanel.removeAll();

        JLabel info = new JLabel("<html><center style='color:#64748b;'>Select an item in <b>Projects</b> or <b>Scene Structure</b> to inspect its properties.</center></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(info);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void inspectSceneItem(SceneItem item) {
        headerTitle.setText("Scene Item: " + item.getName());
        contentPanel.removeAll();

        // Transform Section
        JPanel transformBox = createSection("Transform (2D)");
        addField(transformBox, "Item ID", item.getId());
        addField(transformBox, "Name", item.getName());
        addField(transformBox, "Position X", String.valueOf(item.getX()));
        addField(transformBox, "Position Y", String.valueOf(item.getY()));
        addField(transformBox, "Width", String.valueOf(item.getWidth()));
        addField(transformBox, "Height", String.valueOf(item.getHeight()));
        addField(transformBox, "Layer", item.getLayerName());
        contentPanel.add(transformBox);

        // Physics Section
        if (item.getPhysicsData() != null) {
            JPanel physicsBox = createSection("Box2D Physics");
            addField(physicsBox, "Body Type", String.valueOf(item.getPhysicsData().getBodyType()));
            addField(physicsBox, "Density", String.valueOf(item.getPhysicsData().getDensity()));
            addField(physicsBox, "Friction", String.valueOf(item.getPhysicsData().getFriction()));
            addField(physicsBox, "Restitution", String.valueOf(item.getPhysicsData().getRestitution()));
            contentPanel.add(Box.createVerticalStrut(10));
            contentPanel.add(physicsBox);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void inspectProject(LibGdxProject project) {
        headerTitle.setText("Project: " + project.getName());
        contentPanel.removeAll();

        JPanel generalBox = createSection("Project Metadata");
        addField(generalBox, "Project Name", project.getName());
        addField(generalBox, "Package Name", project.getPackageName());
        addField(generalBox, "Main Class", project.getMainClass());
        addField(generalBox, "LibGDX Version", project.getGdxVersion());
        addField(generalBox, "Root Directory", project.getRootDirectory().getName());
        contentPanel.add(generalBox);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.setBackground(SciFiColors.BG_CARD);
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
        txt.setBackground(SciFiColors.BG_DARK);
        txt.setForeground(SciFiColors.TEXT_PRIMARY);
        txt.setCaretColor(SciFiColors.ACCENT_CYAN);
        txt.setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));
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
        return "InspectorTopComponent";
    }
}
