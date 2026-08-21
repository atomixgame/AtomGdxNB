package com.atomgdx.editor.font;

import org.junit.Test;
import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class FontGeneratorEngineTest {

    @Test
    public void testGenerateFontDescriptorAndTexture() throws Exception {
        FontGeneratorSettings settings = new FontGeneratorSettings();
        settings.setFontSize(24);
        settings.setPadding(2);
        settings.setOutlineWidth(1);
        settings.setPageWidth(256);
        settings.setPageHeight(256);
        settings.setCharacterSet("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

        File tempDir = Files.createTempDirectory("atomgdx_font_test").toFile();
        try {
            FontGeneratorEngine.FontResult result = FontGeneratorEngine.generateFont(settings, tempDir, "test_font");

            assertNotNull(result);
            assertTrue(result.fntFile.exists());
            assertTrue(result.pngFile.exists());
            assertNotNull(result.atlasImage);
            assertEquals(36, result.glyphs.size());

            String fntContent = Files.readString(result.fntFile.toPath());
            assertTrue(fntContent.contains("info face=\"test_font\""));
            assertTrue(fntContent.contains("common lineHeight="));
            assertTrue(fntContent.contains("page id=0 file=\"test_font.png\""));
            assertTrue(fntContent.contains("chars count=36"));
        } finally {
            // cleanup
            for (File f : tempDir.listFiles()) f.delete();
            tempDir.delete();
        }
    }

    @Test
    public void testDistanceFieldTransform() throws Exception {
        FontGeneratorSettings settings = new FontGeneratorSettings();
        settings.setFontType(FontGeneratorSettings.FontType.SDF);
        settings.setFontSize(32);
        settings.setSpread(4);
        settings.setCharacterSet("AB");

        var atlas = FontGeneratorEngine.renderAtlas(settings);
        assertNotNull(atlas);
        assertEquals(512, atlas.getWidth());
        assertEquals(512, atlas.getHeight());
    }

    @Test
    public void testGenerateSdfShaderFiles() throws Exception {
        File tempDir = Files.createTempDirectory("atomgdx_shader_test").toFile();
        try {
            FontGeneratorEngine.generateSdfShaderFiles(tempDir, "game_sdf");
            File vert = new File(tempDir, "game_sdf.vert");
            File frag = new File(tempDir, "game_sdf.frag");
            assertTrue(vert.exists());
            assertTrue(frag.exists());
            assertTrue(Files.readString(frag.toPath()).contains("smoothstep"));
        } finally {
            for (File f : tempDir.listFiles()) f.delete();
            tempDir.delete();
        }
    }
}
