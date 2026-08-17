package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.SceneHierarchyTreePanel;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for Scene Structure (Hierarchical Layers & Sprites).
 * Docks on the Left side ("navigator" / "explorer") hosting the full interactive SceneHierarchyTreePanel.
 */
public class SceneStructureTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private SceneVO currentScene;
    private SceneHierarchyTreePanel hierarchyPanel;

    public SceneStructureTopComponent() {
        setName("Scene Structure");
        setToolTipText("HyperLap2D Hierarchy & Layer Structure");
        setLayout(new BorderLayout());
        setBackground(new Color(43, 45, 48));

        // Associate NetBeans Lookup with our InstanceContent
        associateLookup(new AbstractLookup(instanceContent));

        // Default or workspace scene
        SceneVO defaultScene = loadDefaultScene();
        setScene(defaultScene);
    }

    private static SceneVO loadDefaultScene() {
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

    public void setScene(SceneVO scene) {
        this.currentScene = scene != null ? scene : new SceneVO("MainScene");
        removeAll();

        hierarchyPanel = new SceneHierarchyTreePanel(this.currentScene);
        hierarchyPanel.setSelectionListener(item -> {
            instanceContent.set(item != null ? java.util.Collections.singleton(item) : java.util.Collections.emptyList(), null);
        });

        add(hierarchyPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public SceneHierarchyTreePanel getHierarchyPanel() {
        return hierarchyPanel;
    }

    public void selectItem(MainItemVO item) {
        if (hierarchyPanel != null) {
            hierarchyPanel.selectItem(item);
        }
    }

    public void rebuildTree() {
        if (hierarchyPanel != null) {
            hierarchyPanel.rebuildTree();
        }
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
