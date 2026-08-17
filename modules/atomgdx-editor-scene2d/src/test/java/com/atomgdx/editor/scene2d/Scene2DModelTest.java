package com.atomgdx.editor.scene2d;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Scene2DModelTest {

    @Test
    void testSceneGraphSerialization() {
        Scene2DModel scene = new Scene2DModel("Level01_SpaceStation");
        scene.setAmbientColorHex("0d1117");
        scene.setGravityY(-9.8f);

        SceneItem ship = new SceneItem("player_ship", "PlayerShip", 100, 200, 64, 64, "Main");
        ship.setPhysicsData(new Box2DPhysicsData("Dynamic", 1.0f, 0.3f, 0.1f));
        ship.setLightData(new LightData("PointLight", "00f0ff", 250f));

        scene.addItem(ship);

        String json = scene.toJson();
        assertThat(json).contains("Level01_SpaceStation");
        assertThat(json).contains("player_ship");
        assertThat(json).contains("PlayerShip");
        assertThat(json).contains("Dynamic");
        assertThat(json).contains("00f0ff");
    }
}
