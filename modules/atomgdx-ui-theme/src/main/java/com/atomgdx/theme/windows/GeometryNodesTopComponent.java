package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.GeometryNodesPanel;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;

public class GeometryNodesTopComponent extends TopComponent {
    public GeometryNodesTopComponent() {
        setName("Procedural Geometry Nodes");
        setToolTipText("Procedural 3D Mesh Generation & Geometry Modifiers Graph (Blender style)");
        setLayout(new BorderLayout());
        add(new GeometryNodesPanel(), BorderLayout.CENTER);
    }
    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "GeometryNodesTopComponent"; }
}
