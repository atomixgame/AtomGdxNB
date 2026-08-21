package com.atomgdx.theme.windows;

import com.atomgdx.theme.ecs.ComponentRegistryPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for Ashley ECS Component Registry.
 */
public class ComponentRegistryTopComponent extends TopComponent {

    public ComponentRegistryTopComponent() {
        setName("ECS Components");
        setToolTipText("Ashley ECS Component Registry & System Introspection");
        setLayout(new BorderLayout());
        add(new ComponentRegistryPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "ComponentRegistryTopComponent";
    }
}
