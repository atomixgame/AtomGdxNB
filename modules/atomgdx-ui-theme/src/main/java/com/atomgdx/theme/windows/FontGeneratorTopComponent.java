package com.atomgdx.theme.windows;

import com.atomgdx.editor.font.ui.FontGeneratorPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for Bitmap Font & MSDF Generator.
 */
public class FontGeneratorTopComponent extends TopComponent {

    public FontGeneratorTopComponent() {
        setName("Bitmap & MSDF Font Studio");
        setToolTipText("LibGDX FreeType / Hiero Bitmap Font Generator");
        setLayout(new BorderLayout());
        add(new FontGeneratorPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "FontGeneratorTopComponent";
    }
}
