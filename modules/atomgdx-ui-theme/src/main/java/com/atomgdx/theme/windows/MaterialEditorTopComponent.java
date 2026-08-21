package com.atomgdx.theme.windows;

import com.atomgdx.viewer3d.ui.PbrMaterialEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for PBR Material Editor & Shader Studio.
 */
public class MaterialEditorTopComponent extends TopComponent {

    public MaterialEditorTopComponent() {
        setName("PBR Material Studio");
        setToolTipText("LibGDX 3D PBR Material Editor & GLSL Shader Bindings");
        setLayout(new BorderLayout());
        add(new PbrMaterialEditorPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "MaterialEditorTopComponent";
    }
}
