package com.atomgdx.editor.font;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FontGeneratorEngineTest {

    @Test
    void testFntGeneration(@TempDir File tempDir) throws IOException {
        FontGeneratorSettings settings = new FontGeneratorSettings();
        settings.setFontSize(24);
        settings.setCharacterSet("ABCDEF0123");

        File fnt = FontGeneratorEngine.generateFntDescriptor(settings, tempDir, "SciFiFont");
        assertThat(fnt).exists();
        assertThat(fnt.length()).isGreaterThan(0);
    }
}
