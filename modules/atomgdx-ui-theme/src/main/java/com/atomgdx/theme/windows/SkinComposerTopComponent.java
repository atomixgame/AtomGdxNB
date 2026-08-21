package com.atomgdx.theme.windows;

import com.atomgdx.editor.skin.SkinModel;
import com.atomgdx.editor.skin.ui.SkinComposerPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class SkinComposerTopComponent extends TopComponent {
    private static SkinComposerTopComponent instance;

    public SkinComposerTopComponent() {
        setName("VisUI Skin Composer");
        setToolTipText("LibGDX Scene2D / VisUI Skin Composer & Style Editor");
        setLayout(new BorderLayout());
        add(new SkinComposerPanel(new SkinModel()), BorderLayout.CENTER);
    }

    public static synchronized SkinComposerTopComponent getDefault() {
        if (instance == null) instance = new SkinComposerTopComponent();
        return instance;
    }

    public static synchronized SkinComposerTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("SkinComposerTopComponent");
        if (tc instanceof SkinComposerTopComponent) return (SkinComposerTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "SkinComposerTopComponent"; }
}
