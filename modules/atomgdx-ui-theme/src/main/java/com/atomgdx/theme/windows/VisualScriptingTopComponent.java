package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.VisualScriptingPanel;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;

public class VisualScriptingTopComponent extends TopComponent {
    public VisualScriptingTopComponent() {
        setName("Visual Scripting & FSM Graph");
        setToolTipText("Finite State Machine (FSM) & Visual Logic Scripting Graph (Unreal Blueprint style)");
        setLayout(new BorderLayout());
        add(new VisualScriptingPanel(), BorderLayout.CENTER);
    }
    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "VisualScriptingTopComponent"; }
}
