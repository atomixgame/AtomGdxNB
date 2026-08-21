package com.atomgdx.theme.windows;

import com.atomgdx.theme.ecs.ComponentRegistryPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class ComponentRegistryTopComponent extends TopComponent {
    private static ComponentRegistryTopComponent instance;

    public ComponentRegistryTopComponent() {
        setName("Ashley ECS Components");
        setToolTipText("LibGDX Ashley ECS Component Registry & Prefab Inspector");
        setLayout(new BorderLayout());
        add(new ComponentRegistryPanel(), BorderLayout.CENTER);
    }

    public static synchronized ComponentRegistryTopComponent getDefault() {
        if (instance == null) instance = new ComponentRegistryTopComponent();
        return instance;
    }

    public static synchronized ComponentRegistryTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("ComponentRegistryTopComponent");
        if (tc instanceof ComponentRegistryTopComponent) return (ComponentRegistryTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "ComponentRegistryTopComponent"; }
}
