package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.theme.project.LibGdxProjectNode;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * NetBeans TopComponent for Scene Structure (HyperLap2D Hierarchical Layer and Item Tree).
 * Docks on the Left navigator/explorer side with live selection synchronization.
 */
public class SceneStructureTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager explorerManager = new ExplorerManager();
    private SceneVO currentScene;

    public SceneStructureTopComponent() {
        setName("Scene Structure");
        setToolTipText("HyperLap2D Hierarchy & Layer Structure");
        setLayout(new BorderLayout());
        setBackground(new Color(30, 31, 34));

        BeanTreeView treeView = new BeanTreeView();
        treeView.setBorder(new LineBorder(new Color(60, 63, 65), 1));
        treeView.setBackground(new Color(30, 31, 34));
        treeView.setRootVisible(true);

        if (treeView.getViewport() != null) {
            treeView.getViewport().setBackground(new Color(30, 31, 34));
        }

        add(treeView, BorderLayout.CENTER);

        // Bind NetBeans Global Lookup to this ExplorerManager
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));

        // Default demo scene
        SceneVO defaultScene = new SceneVO("MainScene");
        defaultScene.composite.layers.clear();
        defaultScene.composite.layers.add(new LayerItemVO("Background"));
        defaultScene.composite.layers.add(new LayerItemVO("Gameplay"));
        defaultScene.composite.layers.add(new LayerItemVO("HUD"));

        SimpleImageVO bg = new SimpleImageVO("nebula_bg.png", 0, 0);
        bg.itemName = "BackgroundNebula";
        bg.layerName = "Background";
        defaultScene.composite.sImages.add(bg);

        SimpleImageVO ship = new SimpleImageVO("player_ship.png", 640, 360);
        ship.itemName = "Starfighter";
        ship.layerName = "Gameplay";
        defaultScene.composite.sImages.add(ship);

        setScene(defaultScene);
    }

    public void setScene(SceneVO scene) {
        this.currentScene = scene;
        if (scene != null) {
            explorerManager.setRootContext(new SceneRootNode(scene));
        }
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
        public SceneRootNode(SceneVO scene) {
            super(new SceneRootChildren(scene));
            setDisplayName(scene.sceneName + " (Scene)");
            setShortDescription("HyperLap2D Scene Root Container");
        }

        @Override
        public Image getIcon(int type) {
            return LibGdxProjectNode.getCustomIcon("star.png");
        }

        @Override
        public Image getOpenedIcon(int type) {
            return LibGdxProjectNode.getCustomIcon("star.png");
        }
    }

    private static class SceneRootChildren extends Children.Keys<LayerItemVO> {
        private final SceneVO scene;

        SceneRootChildren(SceneVO scene) {
            this.scene = scene;
        }

        @Override
        protected void addNotify() {
            setKeys(scene.composite.layers);
        }

        @Override
        protected Node[] createNodes(LayerItemVO layer) {
            return new Node[]{new SceneLayerNode(layer, scene)};
        }
    }

    public static class SceneLayerNode extends AbstractNode {
        public SceneLayerNode(LayerItemVO layer, SceneVO scene) {
            super(new LayerChildren(layer, scene));
            setDisplayName("Layer: " + layer.layerName);
            setShortDescription("Layer visibility: " + (layer.isVisible ? "Visible" : "Hidden"));
        }

        @Override
        public Image getIcon(int type) {
            return LibGdxProjectNode.getCustomIcon("folder.png");
        }

        @Override
        public Image getOpenedIcon(int type) {
            return LibGdxProjectNode.getCustomIcon("folder.png");
        }
    }

    private static class LayerChildren extends Children.Keys<MainItemVO> {
        private final LayerItemVO layer;
        private final SceneVO scene;

        LayerChildren(LayerItemVO layer, SceneVO scene) {
            this.layer = layer;
            this.scene = scene;
        }

        @Override
        protected void addNotify() {
            java.util.List<MainItemVO> items = new java.util.ArrayList<>();
            for (SimpleImageVO img : scene.composite.sImages) {
                if (layer.layerName.equals(img.layerName)) items.add(img);
            }
            for (LabelVO lbl : scene.composite.sLabels) {
                if (layer.layerName.equals(lbl.layerName)) items.add(lbl);
            }
            for (LightVO lt : scene.composite.sLights) {
                if (layer.layerName.equals(lt.layerName)) items.add(lt);
            }
            for (ParticleEffectVO p : scene.composite.sParticleEffects) {
                if (layer.layerName.equals(p.layerName)) items.add(p);
            }
            for (NinePatchVO np : scene.composite.sNinePatches) {
                if (layer.layerName.equals(np.layerName)) items.add(np);
            }
            for (CompositeItemVO c : scene.composite.sComposites) {
                if (layer.layerName.equals(c.layerName)) items.add(c);
            }
            setKeys(items);
        }

        @Override
        protected Node[] createNodes(MainItemVO item) {
            return new Node[]{new SceneItemNode(item)};
        }
    }

    public static class SceneItemNode extends AbstractNode {
        private final MainItemVO item;

        public SceneItemNode(MainItemVO item) {
            this(item, new InstanceContent());
        }

        private SceneItemNode(MainItemVO item, InstanceContent content) {
            super(Children.LEAF, new AbstractLookup(content));
            this.item = item;
            content.add(item);
            setDisplayName(item.itemName != null && !item.itemName.isEmpty() ? item.itemName : "Item #" + item.uniqueId);
            setShortDescription("Transform: (" + item.x + ", " + item.y + ") Scale: (" + item.scaleX + ", " + item.scaleY + ")");
        }

        @Override
        public Image getIcon(int type) {
            if (item instanceof SimpleImageVO) return LibGdxProjectNode.getCustomIcon("picture.png");
            if (item instanceof LightVO) return LibGdxProjectNode.getCustomIcon("lightning.png");
            if (item instanceof ParticleEffectVO) return LibGdxProjectNode.getCustomIcon("fire.png");
            if (item instanceof LabelVO) return LibGdxProjectNode.getCustomIcon("wand.png");
            if (item instanceof NinePatchVO) return LibGdxProjectNode.getCustomIcon("picture.png");
            return LibGdxProjectNode.getCustomIcon("cog.png");
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
    }
}
