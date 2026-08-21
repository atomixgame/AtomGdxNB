package com.atomgdx.theme.windows;

import com.atomgdx.gradle.ui.BuildConfigPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class BuildConfigTopComponent extends TopComponent {
    private static BuildConfigTopComponent instance;

    public BuildConfigTopComponent() {
        setName("Build Configurations");
        setToolTipText("Multi-Platform LibGDX Build Profiles & Gradle Runner");
        setLayout(new BorderLayout());
        add(new BuildConfigPanel(), BorderLayout.CENTER);
    }

    public static synchronized BuildConfigTopComponent getDefault() {
        if (instance == null) instance = new BuildConfigTopComponent();
        return instance;
    }

    public static synchronized BuildConfigTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("BuildConfigTopComponent");
        if (tc instanceof BuildConfigTopComponent) return (BuildConfigTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "BuildConfigTopComponent"; }
}
