package com.atomgdx.theme.windows;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.Model3DDescriptor;
import com.atomgdx.viewer3d.ui.Model3DViewerPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;
import java.util.Collections;

/**
 * NetBeans TopComponent for 3D Model & GLTF/GLB Viewer (Read-Only Preview).
 * Supports opening any .glb, .gltf, .obj, .g3db model directly.
 */
@TopComponent.Description(
        preferredID = "Model3DViewerTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.atomgdx.theme.windows.Model3DViewerTopComponent")
@ActionReference(path = "Menu/Window/3D", position = 200)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_Model3DViewerAction",
        preferredID = "Model3DViewerTopComponent"
)
@Messages({
        "CTL_Model3DViewerAction=3D Model Viewer",
        "CTL_Model3DViewerTopComponent=3D Model Viewer",
        "HINT_Model3DViewerTopComponent=Read-Only 3D GLTF/GLB Model & Mesh Viewer"
})
public class Model3DViewerTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private Model3DDescriptor descriptor;
    private final Model3DViewerPanel viewerPanel;

    public Model3DViewerTopComponent() {
        this(new File("G:\\GameDev\\LibGDX\\AtomGdx\\AtomGdxNB\\Workspace\\NeonCosmos\\assets\\models\\khronos\\DamagedHelmet.glb"));
    }

    public Model3DViewerTopComponent(File file) {
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);
        setBorder(null);

        associateLookup(new AbstractLookup(instanceContent));

        File targetFile = (file != null && file.exists()) ? file : new File("spacecraft_cruiser.gltf");
        this.descriptor = new Model3DDescriptor(targetFile);

        setName("3D Model Viewer - " + descriptor.getName());
        setToolTipText("Read-Only 3D Mesh Preview: " + targetFile.getAbsolutePath());

        instanceContent.set(Collections.singleton(descriptor), null);

        viewerPanel = new Model3DViewerPanel(descriptor);
        viewerPanel.setModelChangedListener(desc -> {
            this.descriptor = desc;
            setName("3D Model Viewer - " + desc.getName());
            instanceContent.set(Collections.singleton(desc), null);
        });

        add(viewerPanel, BorderLayout.CENTER);
    }

    public Model3DViewerPanel getViewerPanel() {
        return viewerPanel;
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
