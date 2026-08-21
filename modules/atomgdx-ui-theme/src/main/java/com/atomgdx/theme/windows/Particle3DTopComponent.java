package com.atomgdx.theme.windows;

import com.atomgdx.editor.particle3d.ui.Particle3DEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for 3D Flame Particle System Editor.
 */
public class Particle3DTopComponent extends TopComponent {

    public Particle3DTopComponent() {
        setName("3D Particle Flame Studio");
        setToolTipText("LibGDX 3D Flame Particle System & Physics Influencers");
        setLayout(new BorderLayout());
        add(new Particle3DEditorPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Particle3DTopComponent";
    }
}
