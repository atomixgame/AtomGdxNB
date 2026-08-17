package com.atomgdx.genart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.io.File;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class GenerativeArtServiceTest {

    @Test
    void testProceduralTextureGeneration(@TempDir File tempDir) throws ExecutionException, InterruptedException {
        File out = new File(tempDir, "neon_grid.png");
        File generated = GenerativeArtService.generateProceduralTexture(
                out,
                128,
                128,
                GenerativeArtService.TexturePreset.NEON_GRID,
                new Color(0x00, 0xF0, 0xFF),
                new Color(0x0D, 0x11, 0x17)
        ).get();

        assertThat(generated).exists();
        assertThat(generated.length()).isGreaterThan(0);
    }
}
