package com.atomgdx.theme.windows;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.theme.project.LibGdxProjectNode;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent providing the LibGDX Project Explorer and Asset Tree window.
 */
public class LibGdxProjectExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager explorerManager = new ExplorerManager();

    public LibGdxProjectExplorerTopComponent() {
        setName("Projects & Assets");
        setToolTipText("LibGDX Game Projects and Assets Tree");
        setLayout(new BorderLayout());
        setBackground(new Color(30, 31, 34));

        BeanTreeView treeView = new BeanTreeView();
        treeView.setBorder(new LineBorder(new Color(60, 63, 65), 1));
        treeView.setBackground(new Color(30, 31, 34));
        treeView.setRootVisible(true);

        // Customize viewport background
        if (treeView.getViewport() != null) {
            treeView.getViewport().setBackground(new Color(30, 31, 34));
        }

        add(treeView, BorderLayout.CENTER);

        // Associate NetBeans Lookup with ExplorerManager selection
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));

        // Initialize with workspace project NeonCosmos
        File workspaceDir = new File("G:\\GameDev\\LibGDX\\AtomGdx\\AtomGdxNB\\Workspace\\NeonCosmos");
        LibGdxProject demoProject = new LibGdxProject("NeonCosmos", "com.atomgdx.demo", "NeonCosmosGame", workspaceDir);
        explorerManager.setRootContext(new LibGdxProjectNode(demoProject));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "LibGdxProjectExplorerTopComponent";
    }
}
