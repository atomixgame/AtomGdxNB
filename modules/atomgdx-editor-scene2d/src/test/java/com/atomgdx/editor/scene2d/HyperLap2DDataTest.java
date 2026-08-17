package com.atomgdx.editor.scene2d;

import com.atomgdx.editor.scene2d.data.vo.*;

import java.io.File;

/**
 * Unit and serialization test for HyperLap2D data structures and serializer.
 */
public class HyperLap2DDataTest {

    public static void main(String[] args) {
        System.out.println("Starting HyperLap2D Data & Serialization Tests...");

        // 1. Create Scene with composite items, physics, and lights
        SceneVO scene = new SceneVO("BattleArena");
        scene.composite.layers.add(new LayerItemVO("Background"));
        scene.composite.layers.add(new LayerItemVO("Foreground"));

        SimpleImageVO player = new SimpleImageVO("player_ship", 100, 200);
        player.layerName = "Foreground";
        player.scaleX = 1.5f;
        player.scaleY = 1.5f;
        player.rotation = 45f;
        player.physics = new PhysicsBodyDataVO();
        player.physics.bodyType = 2; // Dynamic
        player.physics.density = 2.5f;
        player.physics.friction = 0.4f;
        player.physics.restitution = 0.8f;
        scene.composite.sImages.add(player);

        SimpleImageVO bg = new SimpleImageVO("space_bg", 0, 0);
        bg.layerName = "Background";
        scene.composite.sImages.add(bg);

        LabelVO scoreLabel = new LabelVO("Score: 9999", 50, 500);
        scene.composite.sLabels.add(scoreLabel);

        LightVO light = new LightVO("ShipPointLight", LightVO.LightType.POINT, 100, 200);
        light.distance = 250f;
        scene.composite.sLights.add(light);

        // 2. Serialize to JSON string
        String json = HyperLap2DSerializer.serializeScene(scene);
        System.out.println("Serialized JSON length: " + json.length() + " chars");
        assertNotNull(json, "Serialized JSON must not be null");

        // 3. Deserialize back to SceneVO
        SceneVO deserialized = HyperLap2DSerializer.deserializeScene(json);
        assertEquals("BattleArena", deserialized.sceneName, "Scene name mismatch");
        assertEquals(3, deserialized.composite.layers.size(), "Layer count mismatch");
        assertEquals(2, deserialized.composite.sImages.size(), "Image count mismatch");
        assertEquals(1, deserialized.composite.sLabels.size(), "Label count mismatch");
        assertEquals(1, deserialized.composite.sLights.size(), "Light count mismatch");

        SimpleImageVO desPlayer = deserialized.composite.sImages.get(0);
        assertEquals("player_ship", desPlayer.imageName, "Player image name mismatch");
        assertEquals(45f, desPlayer.rotation, 0.001f, "Player rotation mismatch");
        assertNotNull(desPlayer.physics, "Player physics should not be null");
        assertEquals(2, desPlayer.physics.bodyType, "Player physics bodyType mismatch");
        assertEquals(2.5f, desPlayer.physics.density, 0.001f, "Player physics density mismatch");

        // 4. Test ProjectVO
        ProjectVO project = new ProjectVO();
        project.projectName = "NeonCosmosProject";
        project.scenes.add("BattleArena");
        project.scenes.add("Level2");
        String projJson = HyperLap2DSerializer.serializeProject(project);
        ProjectVO desProject = HyperLap2DSerializer.deserializeProject(projJson);
        assertEquals("NeonCosmosProject", desProject.projectName, "Project name mismatch");
        assertEquals(2, desProject.scenes.size(), "Project scenes count mismatch");

        System.out.println(">>> ALL HYPERLAP2D DATA & SERIALIZATION TESTS PASSED! <<<");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertEquals(float expected, float actual, float delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }
}
