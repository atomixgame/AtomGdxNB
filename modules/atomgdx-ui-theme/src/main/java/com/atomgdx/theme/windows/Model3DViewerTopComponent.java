package com.atomgdx.theme.windows;

import com.atomgdx.viewer3d.Model3DDescriptor;
import com.atomgdx.viewer3d.ui.Model3DViewerPanel;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;

public class Model3DViewerTopComponent extends TopComponent {

    public Model3DViewerTopComponent() {
        setName("3D GLTF Viewer");
        setToolTipText("LibGDX 3D Model & GLTF/GLB Mesh Viewer");
        setLayout(new BorderLayout());
        Model3DDescriptor descriptor = new Model3DDescriptor(
                new File("G:\\GameDev\\LibGDX\\AtomGdx\\AtomGdxNB\\Workspace\\NeonCosmos\\assets\\models\\spacefighter.gltf")
        );
        add(new Model3DViewerPanel(descriptor), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Model3DViewerTopComponent";
    }
}
