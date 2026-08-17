package com.atomgdx.theme.windows;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;
import com.atomgdx.viewer3d.ui.Scene3DEditorPanel;
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
 * NetBeans TopComponent for the Interactive 3D Scene & Level Editor.
 * Docks in the main editor area ("editor"), tracking Untitled/Unsaved status and supporting Drag & Drop.
 */
@TopComponent.Description(
        preferredID = "Scene3DEditorTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.atomgdx.theme.windows.Scene3DEditorTopComponent")
@ActionReference(path = "Menu/Window/3D", position = 210)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_Scene3DEditorAction",
        preferredID = "Scene3DEditorTopComponent"
)
@Messages({
        "CTL_Scene3DEditorAction=3D Scene Editor",
        "CTL_Scene3DEditorTopComponent=3D Scene Editor",
        "HINT_Scene3DEditorTopComponent=Interactive LibGDX 3D Scene & Level Editor"
})
public class Scene3DEditorTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private final Scene3DEditorPanel editorPanel;
    private final Scene3DVO scene;
    private File sceneFile;

    public Scene3DEditorTopComponent() {
        this(null);
    }

    public Scene3DEditorTopComponent(File file) {
        this.sceneFile = file;
        this.scene = new Scene3DVO(file != null ? file.getName().replace(".scene3d.json", "") : "Untitled Scene");
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);
        setBorder(null);

        associateLookup(new AbstractLookup(instanceContent));

        editorPanel = new Scene3DEditorPanel(scene);
        editorPanel.setActiveSceneFile(file);

        // Update tab name based on unsaved/untitled state
        updateTitle();

        editorPanel.setDirtyStateListener(dirty -> updateTitle());
        editorPanel.setNodeCreatedListener(node -> {
            instanceContent.set(Collections.singleton(node), null);
            updateTitle();
        });

        add(editorPanel, BorderLayout.CENTER);
    }

    private void updateTitle() {
        if (sceneFile == null) {
            setName("3D Scene Editor - Untitled*");
            setToolTipText("Untitled 3D Scene (Unsaved) - Drag & drop models or prefabs to build scene");
        } else if (editorPanel.isDirty()) {
            setName("3D Scene Editor - " + sceneFile.getName() + "*");
            setToolTipText("Modified 3D Scene: " + sceneFile.getAbsolutePath());
        } else {
            setName("3D Scene Editor - " + sceneFile.getName());
            setToolTipText("3D Scene: " + sceneFile.getAbsolutePath());
        }
    }

    public Scene3DEditorPanel getEditorPanel() {
        return editorPanel;
    }

    public void selectNode(Node3DVO node) {
        if (node != null) {
            instanceContent.set(Collections.singleton(node), null);
        }
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Scene3DEditorTopComponent";
    }
}
