package com.atomgdx.theme.windows;

import com.atomgdx.editor.font.ui.FontGeneratorPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class FontGeneratorTopComponent extends TopComponent {
    private static FontGeneratorTopComponent instance;

    public FontGeneratorTopComponent() {
        setName("Bitmap & MSDF Font Studio");
        setToolTipText("Bitmap Font & Multi-channel Signed Distance Field (MSDF) Generator");
        setLayout(new BorderLayout());
        add(new FontGeneratorPanel(), BorderLayout.CENTER);
    }

    public static synchronized FontGeneratorTopComponent getDefault() {
        if (instance == null) instance = new FontGeneratorTopComponent();
        return instance;
    }

    public static synchronized FontGeneratorTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("FontGeneratorTopComponent");
        if (tc instanceof FontGeneratorTopComponent) return (FontGeneratorTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "FontGeneratorTopComponent"; }
}
