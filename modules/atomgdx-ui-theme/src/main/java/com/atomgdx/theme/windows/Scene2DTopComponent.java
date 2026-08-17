package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.data.vo.HyperLap2DSerializer;
import com.atomgdx.editor.scene2d.data.vo.MainItemVO;
import com.atomgdx.editor.scene2d.data.vo.SceneVO;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;
import java.util.Collections;

/**
 * NetBeans TopComponent for the dedicated Center HyperLap2D Scene Editor.
 */
public class Scene2DTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private final Scene2DEditorPanel editorPanel;
    private final SceneVO scene;

    public Scene2DTopComponent() {
        this(loadDefaultOrWorkspaceScene());
    }

    public Scene2DTopComponent(SceneVO scene) {
        this.scene = scene != null ? scene : new SceneVO("MainScene");
        setName(this.scene.sceneName + " - Scene");
        setToolTipText("HyperLap2D Composite Scene2D Editor");
        setLayout(new BorderLayout());

        // Associate NetBeans Lookup with this TopComponent
        associateLookup(new AbstractLookup(instanceContent));

        this.editorPanel = new Scene2DEditorPanel(this.scene);
        this.editorPanel.setItemSelectionListener(item -> {
            instanceContent.set(item != null ? Collections.singleton(item) : Collections.emptyList(), null);
        });

        add(editorPanel, BorderLayout.CENTER);
    }

    private static SceneVO loadDefaultOrWorkspaceScene() {
        File sceneFile = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/scenes/MainScene.dt");
        if (sceneFile.exists()) {
            try {
                return HyperLap2DSerializer.loadSceneFromFile(sceneFile);
            } catch (Exception ignored) {}
        }
        return new SceneVO("MainScene");
    }

    public Scene2DEditorPanel getEditorPanel() {
        return editorPanel;
    }

    public SceneVO getScene() {
        return scene;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Scene2DTopComponent";
    }
}
