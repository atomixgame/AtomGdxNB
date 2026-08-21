package com.atomgdx.theme.windows;

import com.atomgdx.tools.texturepacker.ui.TexturePackerPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for TexturePacker Studio.
 */
public class TexturePackerTopComponent extends TopComponent {

    public TexturePackerTopComponent() {
        setName("TexturePacker Studio");
        setToolTipText("LibGDX Batch Texture Atlas Packer");
        setLayout(new BorderLayout());
        add(new TexturePackerPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "TexturePackerTopComponent";
    }
}
