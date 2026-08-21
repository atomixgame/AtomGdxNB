package com.atomgdx.theme.windows;

import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.ui.Particle2DEditorPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class Particle2DTopComponent extends TopComponent {
    private static Particle2DTopComponent instance;

    public Particle2DTopComponent() {
        setName("2D Particle Studio");
        setToolTipText("LibGDX 2D Particle Effect Studio");
        setLayout(new BorderLayout());
        add(new Particle2DEditorPanel(new Particle2DEffectModel("FlameEffect")), BorderLayout.CENTER);
    }

    public static synchronized Particle2DTopComponent getDefault() {
        if (instance == null) instance = new Particle2DTopComponent();
        return instance;
    }

    public static synchronized Particle2DTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("Particle2DTopComponent");
        if (tc instanceof Particle2DTopComponent) return (Particle2DTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "Particle2DTopComponent"; }
}
