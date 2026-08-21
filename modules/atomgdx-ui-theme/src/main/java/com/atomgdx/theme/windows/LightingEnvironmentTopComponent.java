package com.atomgdx.theme.windows;

import com.atomgdx.viewer3d.ui.EnvironmentLightingPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class LightingEnvironmentTopComponent extends TopComponent {
    private static LightingEnvironmentTopComponent instance;

    public LightingEnvironmentTopComponent() {
        setName("Environment & Lighting");
        setToolTipText("3D Environment, HDRI Skybox & Directional Sun Lighting Studio");
        setLayout(new BorderLayout());
        add(new EnvironmentLightingPanel(), BorderLayout.CENTER);
    }

    public static synchronized LightingEnvironmentTopComponent getDefault() {
        if (instance == null) instance = new LightingEnvironmentTopComponent();
        return instance;
    }

    public static synchronized LightingEnvironmentTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("LightingEnvironmentTopComponent");
        if (tc instanceof LightingEnvironmentTopComponent) return (LightingEnvironmentTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "LightingEnvironmentTopComponent"; }
}
