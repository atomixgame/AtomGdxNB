package com.atomgdx.theme.windows;

import com.atomgdx.editor.particle3d.ui.Particle3DEditorPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class Particle3DTopComponent extends TopComponent {
    private static Particle3DTopComponent instance;

    public Particle3DTopComponent() {
        setName("3D Particle Flame Studio");
        setToolTipText("LibGDX 3D Flame Particle System & Physics Influencers");
        setLayout(new BorderLayout());
        add(new Particle3DEditorPanel(), BorderLayout.CENTER);
    }

    public static synchronized Particle3DTopComponent getDefault() {
        if (instance == null) instance = new Particle3DTopComponent();
        return instance;
    }

    public static synchronized Particle3DTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("Particle3DTopComponent");
        if (tc instanceof Particle3DTopComponent) return (Particle3DTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "Particle3DTopComponent"; }
}
