package com.atomgdx.theme.windows;

import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.ui.Particle2DEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.*;

public class Particle2DTopComponent extends TopComponent {

    public Particle2DTopComponent() {
        setName("2D Particle Studio");
        setToolTipText("LibGDX 2D Particle Effect Studio");
        setLayout(new BorderLayout());
        add(new Particle2DEditorPanel(new Particle2DEffectModel("FlameEffect")), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Particle2DTopComponent";
    }
}
