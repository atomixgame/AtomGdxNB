package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.Scene2DModel;
import com.atomgdx.editor.scene2d.SceneItem;
import com.atomgdx.editor.scene2d.SceneLayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * NetBeans TopComponent for Scene Structure (Hierarchical Layer and Item Tree).
 * Docks on the left side alongside Project Explorer.
 */
public class SceneStructureTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager explorerManager = new ExplorerManager();

    public SceneStructureTopComponent() {
        setName("Scene Structure");
        setToolTipText("LibGDX Scene2D Hierarchy & Layer Structure");
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));

        BeanTreeView treeView = new BeanTreeView();
        treeView.setBorder(new EmptyBorder(4, 4, 4, 4));
        treeView.setBackground(new Color(15, 23, 42));
        treeView.setRootVisible(true);

        add(treeView, BorderLayout.CENTER);

        // Bind NetBeans Global Lookup to this ExplorerManager
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));

        // Populate with default Scene structure
        Scene2DModel defaultScene = new Scene2DModel("NeonCosmosMainStage");
        SceneItem ship = new SceneItem("player_ship", "PlayerShip", 120, 240, 64, 64, "Main");
        SceneItem bg = new SceneItem("space_nebula", "BackgroundNebula", 0, 0, 1920, 1080, "Background");
        defaultScene.addItem(bg);
        defaultScene.addItem(ship);

        explorerManager.setRootContext(new SceneRootNode(defaultScene));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "SceneStructureTopComponent";
    }

    public static class SceneRootNode extends AbstractNode {
        public SceneRootNode(Scene2DModel scene) {
            super(new SceneRootChildren(scene));
            setDisplayName(scene.getSceneName() + " (Scene)");
            setShortDescription("LibGDX Scene Dimensions: " + scene.getSceneWidth() + "x" + scene.getSceneHeight());
        }
    }

    private static class SceneRootChildren extends Children.Keys<SceneLayer> {
        private final Scene2DModel scene;

        SceneRootChildren(Scene2DModel scene) {
            this.scene = scene;
        }

        @Override
        protected void addNotify() {
            setKeys(scene.getLayers());
        }

        @Override
        protected Node[] createNodes(SceneLayer layer) {
            return new Node[]{new SceneLayerNode(layer, scene)};
        }
    }

    public static class SceneLayerNode extends AbstractNode {
        public SceneLayerNode(SceneLayer layer, Scene2DModel scene) {
            super(new LayerChildren(layer, scene));
            setDisplayName("Layer: " + layer.getName());
            setShortDescription("Parallax: (" + layer.getParallaxX() + ", " + layer.getParallaxY() + ")");
        }
    }

    private static class LayerChildren extends Children.Keys<SceneItem> {
        private final SceneLayer layer;
        private final Scene2DModel scene;

        LayerChildren(SceneLayer layer, Scene2DModel scene) {
            this.layer = layer;
            this.scene = scene;
        }

        @Override
        protected void addNotify() {
            java.util.List<SceneItem> layerItems = new java.util.ArrayList<>();
            for (SceneItem item : scene.getItems()) {
                if (layer.getName().equalsIgnoreCase(item.getLayerName())) {
                    layerItems.add(item);
                }
            }
            setKeys(layerItems);
        }

        @Override
        protected Node[] createNodes(SceneItem item) {
            return new Node[]{new SceneItemNode(item)};
        }
    }

    public static class SceneItemNode extends AbstractNode {
        private final SceneItem item;

        public SceneItemNode(SceneItem item) {
            this(item, new InstanceContent());
        }

        private SceneItemNode(SceneItem item, InstanceContent content) {
            super(Children.LEAF, new AbstractLookup(content));
            this.item = item;
            content.add(item);
            setDisplayName(item.getName() + " [" + item.getId() + "]");
            setShortDescription("Item at (" + item.getX() + ", " + item.getY() + ")");
        }

        public SceneItem getItem() {
            return item;
        }
    }
}
