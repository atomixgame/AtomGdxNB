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
 * Docks on the Right side ("properties") hosting the full interactive SceneItemInspectorPanel.
 */
public class InspectorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result<Object> lookupResult;
    private final JPanel wrapperPanel = new JPanel(new BorderLayout());
    private SceneItemInspectorPanel itemInspectorPanel;
    private SceneVO activeScene;

    public InspectorTopComponent() {
        setName("Inspector");
        setToolTipText("LibGDX Property & Component Inspector");
        setLayout(new BorderLayout());
        setBackground(new Color(30, 31, 34));

        this.activeScene = new SceneVO("MainScene");
        itemInspectorPanel = new SceneItemInspectorPanel(this.activeScene);

        add(wrapperPanel, BorderLayout.CENTER);
        showEmptySelection();
    }

    public void setScene(SceneVO scene) {
        this.activeScene = scene != null ? scene : new SceneVO("MainScene");
        if (itemInspectorPanel != null) {
            itemInspectorPanel = new SceneItemInspectorPanel(this.activeScene);
        }
    }

    public void inspectItem(MainItemVO item) {
        if (item == null) {
            showEmptySelection();
            return;
        }
        wrapperPanel.removeAll();
        if (itemInspectorPanel == null) {
            itemInspectorPanel = new SceneItemInspectorPanel(activeScene);
        }
        itemInspectorPanel.setItem(item);
        wrapperPanel.add(itemInspectorPanel, BorderLayout.CENTER);
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
            if (obj instanceof MainItemVO) {
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

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 31, 34));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel p = createSection("File Details");
        p.add(createPropRow("File Name:", file.getName()));
        p.add(createPropRow("Size:", (file.length() / 1024) + " KB"));
        p.add(createPropRow("Location:", file.getParent()));
        p.add(createPropRow("Writable:", file.canWrite() ? "Yes" : "No"));
        panel.add(p);
        panel.add(Box.createVerticalGlue());

        wrapperPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    private void inspectProject(LibGdxProject project) {
        wrapperPanel.removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 31, 34));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel p = createSection("Project Overview");
        p.add(createPropRow("Game Name:", project.getName()));
        p.add(createPropRow("LibGDX Version:", project.getGdxVersion()));
        p.add(createPropRow("Package:", project.getPackageName()));
        p.add(createPropRow("Root Path:", project.getRootDirectory().getAbsolutePath()));
        panel.add(p);
        panel.add(Box.createVerticalGlue());

        wrapperPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
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
        wrapperPanel.removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 31, 34));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel info = new JLabel("<html><center style='color:#9aa0a6;'>Select an item in <b>Projects & Assets</b> or <b>Scene Structure</b> to inspect and edit its properties.</center></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(info);
        panel.add(Box.createVerticalGlue());

        wrapperPanel.add(panel, BorderLayout.CENTER);
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
