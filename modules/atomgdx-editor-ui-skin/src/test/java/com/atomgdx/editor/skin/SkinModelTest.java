package com.atomgdx.editor.skin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SkinModelTest {

    @Test
    void testSkinJsonRoundTrip() {
        SkinModel skin = new SkinModel("VisUI Cyberpunk");
        skin.addColor("neon-green", "00ff66");
        skin.addFont("header-font", "fonts/header.fnt");

        String json = skin.toJson();
        assertThat(json).contains("com.badlogic.gdx.graphics.Color");
        assertThat(json).contains("neon-green");
        assertThat(json).contains("00ff66");
        assertThat(json).contains("header-font");

        SkinModel parsed = SkinModel.parseJson(json, "VisUI Cyberpunk");
        assertThat(parsed.getColors()).containsKey("neon-green");
        assertThat(parsed.getFonts()).containsKey("header-font");
    }
}
