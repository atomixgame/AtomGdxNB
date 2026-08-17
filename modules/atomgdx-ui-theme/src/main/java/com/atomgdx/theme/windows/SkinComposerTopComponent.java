package com.atomgdx.theme.windows;

import com.atomgdx.editor.skin.SkinModel;
import com.atomgdx.editor.skin.ui.SkinComposerPanel;
import org.openide.windows.TopComponent;

import java.awt.*;

public class SkinComposerTopComponent extends TopComponent {

    public SkinComposerTopComponent() {
        setName("VisUI Skin Composer");
        setToolTipText("LibGDX Scene2D / VisUI Skin Composer & Style Editor");
        setLayout(new BorderLayout());
        add(new SkinComposerPanel(new SkinModel()), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "SkinComposerTopComponent";
    }
}
