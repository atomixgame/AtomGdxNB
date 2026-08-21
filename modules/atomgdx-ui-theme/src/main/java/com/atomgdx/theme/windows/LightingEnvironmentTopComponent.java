package com.atomgdx.theme.windows;

import com.atomgdx.viewer3d.ui.EnvironmentLightingPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for Environment Lighting & HDRI Skybox Studio.
 */
public class LightingEnvironmentTopComponent extends TopComponent {

    public LightingEnvironmentTopComponent() {
        setName("Environment & Lighting");
        setToolTipText("LibGDX 3D HDRI Skybox IBL, Directional Sun, and Shadows Studio");
        setLayout(new BorderLayout());
        add(new EnvironmentLightingPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "LightingEnvironmentTopComponent";
    }
}
