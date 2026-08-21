package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.VisualScriptingPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class VisualScriptingTopComponent extends TopComponent {
    private static VisualScriptingTopComponent instance;

    public VisualScriptingTopComponent() {
        setName("Visual Scripting & FSM Graph");
        setToolTipText("Finite State Machine (FSM) & Visual Logic Scripting Graph (Unreal Blueprint style)");
        setLayout(new BorderLayout());
        add(new VisualScriptingPanel(), BorderLayout.CENTER);
    }

    public static synchronized VisualScriptingTopComponent getDefault() {
        if (instance == null) instance = new VisualScriptingTopComponent();
        return instance;
    }

    public static synchronized VisualScriptingTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("VisualScriptingTopComponent");
        if (tc instanceof VisualScriptingTopComponent) return (VisualScriptingTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "VisualScriptingTopComponent"; }
}
