package com.atomgdx.theme.windows;

import com.atomgdx.viewer3d.ui.PbrMaterialEditorPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class MaterialEditorTopComponent extends TopComponent {
    private static MaterialEditorTopComponent instance;

    public MaterialEditorTopComponent() {
        setName("PBR Material Studio");
        setToolTipText("LibGDX 3D PBR Material Editor & GLSL Shader Bindings");
        setLayout(new BorderLayout());
        add(new PbrMaterialEditorPanel(), BorderLayout.CENTER);
    }

    public static synchronized MaterialEditorTopComponent getDefault() {
        if (instance == null) instance = new MaterialEditorTopComponent();
        return instance;
    }

    public static synchronized MaterialEditorTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("MaterialEditorTopComponent");
        if (tc instanceof MaterialEditorTopComponent) return (MaterialEditorTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "MaterialEditorTopComponent"; }
}
