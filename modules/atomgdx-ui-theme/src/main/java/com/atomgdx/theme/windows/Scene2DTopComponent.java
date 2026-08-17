package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.data.vo.HyperLap2DSerializer;
import com.atomgdx.editor.scene2d.data.vo.SceneVO;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for the center HyperLap2D Scene Editor.
 */
public class Scene2DTopComponent extends TopComponent {

    private final Scene2DEditorPanel editorPanel;

    public Scene2DTopComponent() {
        this(loadDefaultOrWorkspaceScene());
    }

    public Scene2DTopComponent(SceneVO scene) {
        setName(scene != null ? scene.sceneName + " - Scene" : "HyperLap2D Scene");
        setToolTipText("HyperLap2D Composite Scene2D Editor");
        setLayout(new BorderLayout());

        this.editorPanel = new Scene2DEditorPanel(scene);
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

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Scene2DTopComponent";
    }
}
