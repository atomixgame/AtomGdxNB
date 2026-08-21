package com.atomgdx.theme;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.Scene2DTopComponent;
import com.atomgdx.theme.windows.Scene3DEditorTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class SceneStructureAndTransformE2ETest {

    @Test
    public void testDynamicSceneStructureFromDtFile() throws Exception {
        // 1. Create a custom .dt file with layers and composite items
        File tempDt = File.createTempFile("TestGalaxySector", ".dt");
        tempDt.deleteOnExit();

        SceneVO testScene = new SceneVO("GalaxySector7");
        testScene.composite.layers.clear();
        testScene.composite.layers.add(new LayerItemVO("StarfieldLayer"));
        testScene.composite.layers.add(new LayerItemVO("CombatLayer"));
        testScene.composite.layers.add(new LayerItemVO("UIRadarLayer"));

        SimpleImageVO playerShip = new SimpleImageVO("cruiser_alpha", 500, 250);
        playerShip.layerName = "CombatLayer";
        playerShip.scaleX = 1.5f;
        playerShip.scaleY = 1.5f;
        testScene.composite.sImages.add(playerShip);

        LightVO warpCoreLight = new LightVO();
        warpCoreLight.itemIdentifier = "WarpCoreGlow";
        warpCoreLight.layerName = "CombatLayer";
        warpCoreLight.x = 520;
        warpCoreLight.y = 260;
        warpCoreLight.distance = 120;
        testScene.composite.sLights.add(warpCoreLight);

        HyperLap2DSerializer.saveSceneToFile(testScene, tempDt);

        // 2. Open Scene2DTopComponent with that .dt file
        Scene2DTopComponent scene2DComp = new Scene2DTopComponent(tempDt);
        assertNotNull(scene2DComp.getScene());
        assertEquals("GalaxySector7", scene2DComp.getScene().sceneName);
        assertEquals(3, scene2DComp.getScene().composite.layers.size());
        assertEquals("StarfieldLayer", scene2DComp.getScene().composite.layers.get(0).layerName);

        // 3. Test SceneStructureTopComponent loads the real .dt structure dynamically
        SceneStructureTopComponent structureComp = new SceneStructureTopComponent();
        structureComp.loadSceneFromFile(tempDt);
        assertEquals(SceneStructureTopComponent.Mode.SCENE_2D, structureComp.getMode());
        assertNotNull(structureComp.getHierarchyPanel2D());

        // 4. Test 2D Hit-Testing across all item types
        Scene2DEditorPanel editorPanel = scene2DComp.getEditorPanel();
        MainItemVO hitShip = editorPanel.findItemAt(520, 260);
        assertNotNull("Should hit test playerShip image", hitShip);
        assertEquals("cruiser_alpha", ((SimpleImageVO) hitShip).imageName);

        // 5. Test Inspector receives 2D item and allows live transform edits
        InspectorTopComponent inspector = new InspectorTopComponent();
        inspector.inspectSceneItem(playerShip, testScene);
        assertEquals(playerShip, inspector.getCurrentlyInspected());

        // Perform Transform Edit (Translate & Scale)
        playerShip.x = 750;
        playerShip.y = 350;
        playerShip.rotation = 45f;
        playerShip.scaleX = 2.0f;
        playerShip.scaleY = 2.0f;

        assertEquals(750f, playerShip.x, 0.001f);
        assertEquals(350f, playerShip.y, 0.001f);
        assertEquals(45f, playerShip.rotation, 0.001f);
        assertEquals(2.0f, playerShip.scaleX, 0.001f);
    }

    @Test
    public void test3DSceneGraphAndTransformSync() {
        // 1. Create a 3D Scene with nodes
        Scene3DVO scene3D = new Scene3DVO("NebulaOutpost");
        Node3DVO spaceStation = new Node3DVO("OrbitalStation", Node3DVO.NodeType.MESH);
        spaceStation.posX = 10f;
        spaceStation.posY = 5f;
        spaceStation.posZ = -20f;
        scene3D.rootNode.addChild(spaceStation);

        // 2. Open 3D Scene TopComponent
        Scene3DEditorTopComponent scene3dComp = new Scene3DEditorTopComponent();
        scene3dComp.getEditorPanel().setSelectedNode(spaceStation);

        // 3. Open SceneStructureTopComponent and set 3D scene
        SceneStructureTopComponent structureComp = new SceneStructureTopComponent();
        structureComp.setScene3D(scene3D);
        assertEquals(SceneStructureTopComponent.Mode.SCENE_GRAPH_3D, structureComp.getMode());
        assertNotNull(structureComp.getSceneGraphPanel3D());

        // 4. Test Inspector handles 3D Node
        InspectorTopComponent inspector = new InspectorTopComponent();
        inspector.inspectNode3D(spaceStation);
        assertEquals(spaceStation, inspector.getCurrentlyInspected());

        // Perform 3D Transform Edit
        spaceStation.posX = 15f;
        spaceStation.posY = 8f;
        spaceStation.rotY = 90f;
        spaceStation.scaleX = 2.5f;

        assertEquals(15f, spaceStation.posX, 0.001f);
        assertEquals(8f, spaceStation.posY, 0.001f);
        assertEquals(90f, spaceStation.rotY, 0.001f);
        assertEquals(2.5f, spaceStation.scaleX, 0.001f);
    }

    @Test
    public void testMultiSelectionAndMarquee() throws Exception {
        SceneVO scene = new SceneVO("MultiSelectScene");
        SimpleImageVO img1 = new SimpleImageVO("unit_1", 100, 100);
        SimpleImageVO img2 = new SimpleImageVO("unit_2", 200, 100);
        scene.composite.sImages.add(img1);
        scene.composite.sImages.add(img2);

        Scene2DEditorPanel editor = new Scene2DEditorPanel(scene);
        java.util.List<MainItemVO> foundInMarquee = editor.findAllItemsInRect(new com.badlogic.gdx.math.Rectangle(50, 50, 300, 300));
        assertEquals(2, foundInMarquee.size());

        InspectorTopComponent inspector = new InspectorTopComponent();
        inspector.showMultiSelection(foundInMarquee.size(), "2D Scene Object");
        assertEquals("Inspector (2 Objects Selected)", inspector.getName());

        SceneStructureTopComponent structure = new SceneStructureTopComponent();
        structure.setScene2D(scene);
        structure.getHierarchyPanel2D().selectItems(foundInMarquee);
        assertEquals(2, structure.getHierarchyPanel2D().getSelectedItems().size());
    }
}
