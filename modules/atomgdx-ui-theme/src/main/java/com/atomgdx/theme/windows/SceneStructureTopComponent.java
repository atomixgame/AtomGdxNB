package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.SceneHierarchyTreePanel;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;
import com.atomgdx.viewer3d.ui.SceneGraphTreePanel;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for Scene Structure & 3D SceneGraph.
 * Docks on the Left side, supporting both 2D HyperLap Layers and 3D SceneGraph Hierarchies.
 */
public class SceneStructureTopComponent extends TopComponent {

    public enum Mode {
        SCENE_2D,
        SCENE_GRAPH_3D
    }

    private final InstanceContent instanceContent = new InstanceContent();
    private Mode currentMode = Mode.SCENE_GRAPH_3D; // Default to 3D SceneGraph for 3D View

    private SceneVO currentScene2D;
    private Scene3DVO currentScene3D;

    private SceneHierarchyTreePanel hierarchyPanel2D;
    private SceneGraphTreePanel sceneGraphPanel3D;

    public SceneStructureTopComponent() {
        setName("Scene Structure");
        setToolTipText("LibGDX Scene Hierarchy & 3D SceneGraph");
        setLayout(new BorderLayout());
        setBackground(new Color(43, 45, 48));

        associateLookup(new AbstractLookup(instanceContent));

        this.currentScene2D = loadDefaultScene2D();
        this.currentScene3D = new Scene3DVO("MainScene3D");

        updateView();
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        updateView();
    }

    public Mode getMode() {
        return currentMode;
    }

    private void updateView() {
        removeAll();

        if (currentMode == Mode.SCENE_GRAPH_3D) {
            setName("SceneGraph 3D");
            sceneGraphPanel3D = new SceneGraphTreePanel(currentScene3D);
            sceneGraphPanel3D.setSelectionListener(node -> {
                instanceContent.set(node != null ? java.util.Collections.singleton(node) : java.util.Collections.emptyList(), null);
            });
            add(sceneGraphPanel3D, BorderLayout.CENTER);
        } else {
            setName("Scene Structure (2D)");
            hierarchyPanel2D = new SceneHierarchyTreePanel(currentScene2D);
            hierarchyPanel2D.setSelectionListener(item -> {
                instanceContent.set(item != null ? java.util.Collections.singleton(item) : java.util.Collections.emptyList(), null);
            });
            add(hierarchyPanel2D, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    public void setScene3D(Scene3DVO scene) {
        this.currentScene3D = scene != null ? scene : new Scene3DVO("MainScene3D");
        this.currentMode = Mode.SCENE_GRAPH_3D;
        updateView();
    }

    public void setScene2D(SceneVO scene) {
        this.currentScene2D = scene != null ? scene : new SceneVO("MainScene");
        this.currentMode = Mode.SCENE_2D;
        updateView();
    }

    public SceneGraphTreePanel getSceneGraphPanel3D() {
        return sceneGraphPanel3D;
    }

    public SceneHierarchyTreePanel getHierarchyPanel2D() {
        return hierarchyPanel2D;
    }

    public Scene3DVO getScene3D() {
        return currentScene3D;
    }

    private static SceneVO loadDefaultScene2D() {
        File sceneFile = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/scenes/MainScene.dt");
        if (sceneFile.exists()) {
            try {
                return HyperLap2DSerializer.loadSceneFromFile(sceneFile);
            } catch (Exception ignored) {}
        }
        SceneVO sc = new SceneVO("MainScene");
        sc.composite.layers.clear();
        sc.composite.layers.add(new LayerItemVO("Background"));
        sc.composite.layers.add(new LayerItemVO("Gameplay"));
        sc.composite.layers.add(new LayerItemVO("HUD"));
        return sc;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "SceneStructureTopComponent";
    }
}
