package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.ShaderGraphPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class ShaderGraphTopComponent extends TopComponent {
    private static ShaderGraphTopComponent instance;

    public ShaderGraphTopComponent() {
        setName("Visual ShaderGraph Studio");
        setToolTipText("Node-based Visual Shader & PBR Material Editor (Unity / Blender style)");
        setLayout(new BorderLayout());
        add(new ShaderGraphPanel(), BorderLayout.CENTER);
    }

    public static synchronized ShaderGraphTopComponent getDefault() {
        if (instance == null) instance = new ShaderGraphTopComponent();
        return instance;
    }

    public static synchronized ShaderGraphTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("ShaderGraphTopComponent");
        if (tc instanceof ShaderGraphTopComponent) return (ShaderGraphTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "ShaderGraphTopComponent"; }
}
