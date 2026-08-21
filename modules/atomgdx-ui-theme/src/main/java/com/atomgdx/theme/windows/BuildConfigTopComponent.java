package com.atomgdx.theme.windows;

import com.atomgdx.gradle.ui.BuildConfigPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for Multi-Platform Build Configurations & Gradle Target Matrix.
 */
public class BuildConfigTopComponent extends TopComponent {

    public BuildConfigTopComponent() {
        setName("Build Configurations");
        setToolTipText("Multi-Platform LibGDX Build Profiles & Gradle Runner");
        setLayout(new BorderLayout());
        add(new BuildConfigPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "BuildConfigTopComponent";
    }
}
