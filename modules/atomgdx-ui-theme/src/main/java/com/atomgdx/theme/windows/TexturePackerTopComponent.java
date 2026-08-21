package com.atomgdx.theme.windows;

import com.atomgdx.tools.texturepacker.ui.TexturePackerPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class TexturePackerTopComponent extends TopComponent {
    private static TexturePackerTopComponent instance;

    public TexturePackerTopComponent() {
        setName("TexturePacker Studio");
        setToolTipText("LibGDX Batch Texture Atlas Packer");
        setLayout(new BorderLayout());
        add(new TexturePackerPanel(), BorderLayout.CENTER);
    }

    public static synchronized TexturePackerTopComponent getDefault() {
        if (instance == null) instance = new TexturePackerTopComponent();
        return instance;
    }

    public static synchronized TexturePackerTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("TexturePackerTopComponent");
        if (tc instanceof TexturePackerTopComponent) return (TexturePackerTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "TexturePackerTopComponent"; }
}
