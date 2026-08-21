package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.SceneHierarchyTreePanel;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;
import com.atomgdx.viewer3d.ui.SceneGraphTreePanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for Scene Structure & 3D SceneGraph.
 * Docks on the Left side, supporting both 2D HyperLap Layers and 3D SceneGraph Hierarchies.
 * Dynamically reacts to active 2D/3D Scene Editors and .dt / .scene3d files.
 */
@TopComponent.Description(
        preferredID = "SceneStructureTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "com.atomgdx.theme.windows.SceneStructureTopComponent")
@ActionReference(path = "Menu/Window", position = 290)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SceneStructureAction",
        preferredID = "SceneStructureTopComponent"
)
@Messages({
        "CTL_SceneStructureAction=Scene Structure",
        "CTL_SceneStructureTopComponent=Scene Structure",
        "HINT_SceneStructureTopComponent=LibGDX 2D/3D Scene Hierarchy & SceneGraph"
})
public class SceneStructureTopComponent extends TopComponent implements LookupListener {

    public enum Mode {
        SCENE_2D,
        SCENE_GRAPH_3D
    }

    private final InstanceContent instanceContent = new InstanceContent();
    private Mode currentMode = Mode.SCENE_GRAPH_3D;

    private SceneVO currentScene2D;
    private Scene3DVO currentScene3D;

    private SceneHierarchyTreePanel hierarchyPanel2D;
    private SceneGraphTreePanel sceneGraphPanel3D;
    private Lookup.Result<Object> lookupResult;

    public SceneStructureTopComponent() {
        setName(Bundle.CTL_SceneStructureTopComponent());
        setToolTipText(Bundle.HINT_SceneStructureTopComponent());
        setLayout(new BorderLayout());
        setBackground(new Color(43, 45, 48));

        associateLookup(new AbstractLookup(instanceContent));

        this.currentScene2D = loadDefaultScene2D();
        this.currentScene3D = new Scene3DVO("MainScene3D");

        updateView();
    }

    @Override
    protected void componentOpened() {
        super.componentOpened();
        if (lookupResult == null) {
            lookupResult = Utilities.actionsGlobalContext().lookupResult(Object.class);
            lookupResult.addLookupListener(this);
            resultChanged(null);
        }
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();
        if (lookupResult != null) {
            lookupResult.removeLookupListener(this);
            lookupResult = null;
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Lookup.Result<?> res = (ev != null && ev.getSource() instanceof Lookup.Result) ? (Lookup.Result<?>) ev.getSource() : lookupResult;
        if (res == null) return;
        java.util.Collection<?> instances = res.allInstances();
        if (instances.isEmpty()) return;

        for (Object obj : instances) {
            if (obj instanceof SceneVO) {
                SceneVO newScene = (SceneVO) obj;
                if (currentScene2D != newScene || currentMode != Mode.SCENE_2D) {
                    setScene2D(newScene);
                }
                break;
            } else if (obj instanceof Scene3DVO) {
                Scene3DVO newScene3D = (Scene3DVO) obj;
                if (currentScene3D != newScene3D || currentMode != Mode.SCENE_GRAPH_3D) {
                    setScene3D(newScene3D);
                }
                break;
            } else if (obj instanceof Scene2DTopComponent) {
                Scene2DTopComponent comp = (Scene2DTopComponent) obj;
                if (comp.getScene() != null && (currentScene2D != comp.getScene() || currentMode != Mode.SCENE_2D)) {
                    setScene2D(comp.getScene());
                }
                break;
            } else if (obj instanceof Scene3DEditorTopComponent) {
                Scene3DEditorTopComponent comp3d = (Scene3DEditorTopComponent) obj;
                if (comp3d.getEditorPanel() != null && comp3d.getEditorPanel().getScene() != null) {
                    Scene3DVO sc3d = comp3d.getEditorPanel().getScene();
                    if (currentScene3D != sc3d || currentMode != Mode.SCENE_GRAPH_3D) {
                        setScene3D(sc3d);
                    }
                }
                break;
            } else if (obj instanceof File) {
                File file = (File) obj;
                String name = file.getName().toLowerCase();
                if (name.endsWith(".dt") || name.endsWith(".scene2d") || name.endsWith(".h2d")) {
                    loadSceneFromFile(file);
                    break;
                } else if (name.endsWith(".scene3d") || name.endsWith(".scene3d.json")) {
                    loadSceneFromFile(file);
                    break;
                }
            }
        }

        // Highlight items or nodes in tree
        java.util.List<MainItemVO> items = new java.util.ArrayList<>();
        java.util.List<Node3DVO> nodes = new java.util.ArrayList<>();
        for (Object obj : instances) {
            if (obj instanceof MainItemVO) items.add((MainItemVO) obj);
            else if (obj instanceof Node3DVO) nodes.add((Node3DVO) obj);
        }
        if (!items.isEmpty() && hierarchyPanel2D != null && currentMode == Mode.SCENE_2D) {
            hierarchyPanel2D.selectItems(items);
        } else if (!nodes.isEmpty() && sceneGraphPanel3D != null && currentMode == Mode.SCENE_GRAPH_3D) {
            sceneGraphPanel3D.selectNodes(nodes);
        }
    }

    public void loadSceneFromFile(File file) {
        if (file == null || !file.exists()) return;
        String name = file.getName().toLowerCase();
        if (name.endsWith(".dt") || name.endsWith(".scene2d") || name.endsWith(".h2d")) {
            try {
                SceneVO loaded = HyperLap2DSerializer.loadSceneFromFile(file);
                if (loaded != null) {
                    setScene2D(loaded);
                }
            } catch (Exception ex) {
                System.err.println("Error loading 2D scene from " + file.getAbsolutePath() + ": " + ex.getMessage());
            }
        } else if (name.endsWith(".scene3d") || name.endsWith(".scene3d.json")) {
            Scene3DVO loaded3d = new Scene3DVO(file.getName().replace(".scene3d.json", "").replace(".scene3d", ""));
            setScene3D(loaded3d);
        }
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
        File[] candidatePaths = new File[]{
                new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/scenes/MainScene.dt"),
                new File("Workspace/NeonCosmos/assets/scenes/MainScene.dt"),
                new File("assets/scenes/MainScene.dt")
        };
        for (File candidate : candidatePaths) {
            if (candidate.exists()) {
                try {
                    return HyperLap2DSerializer.loadSceneFromFile(candidate);
                } catch (Exception ignored) {}
            }
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
