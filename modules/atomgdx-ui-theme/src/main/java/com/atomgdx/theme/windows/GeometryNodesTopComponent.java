package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.GeometryNodesPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class GeometryNodesTopComponent extends TopComponent {
    private static GeometryNodesTopComponent instance;

    public GeometryNodesTopComponent() {
        setName("Procedural Geometry Nodes");
        setToolTipText("Procedural 3D Mesh Generation & Geometry Modifiers Graph (Blender style)");
        setLayout(new BorderLayout());
        add(new GeometryNodesPanel(), BorderLayout.CENTER);
    }

    public static synchronized GeometryNodesTopComponent getDefault() {
        if (instance == null) instance = new GeometryNodesTopComponent();
        return instance;
    }

    public static synchronized GeometryNodesTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("GeometryNodesTopComponent");
        if (tc instanceof GeometryNodesTopComponent) return (GeometryNodesTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "GeometryNodesTopComponent"; }
}
