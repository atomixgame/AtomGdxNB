package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.ShaderGraphPanel;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;

public class ShaderGraphTopComponent extends TopComponent {
    public ShaderGraphTopComponent() {
        setName("Visual ShaderGraph Studio");
        setToolTipText("Node-based Visual Shader & PBR Material Editor (Unity / Blender style)");
        setLayout(new BorderLayout());
        add(new ShaderGraphPanel(), BorderLayout.CENTER);
    }
    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "ShaderGraphTopComponent"; }
}
