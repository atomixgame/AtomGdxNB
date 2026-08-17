package com.atomgdx.viewer.media;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class AudioMetadataTest {

    @Test
    void testFormatDetection() {
        AudioMetadata wav = new AudioMetadata(new File("sounds/laser.wav"));
        assertThat(wav.getFormat()).isEqualTo(AudioMetadata.AudioFormat.WAV);

        AudioMetadata ogg = new AudioMetadata(new File("music/space_theme.ogg"));
        assertThat(ogg.getFormat()).isEqualTo(AudioMetadata.AudioFormat.OGG);

        ogg.setDurationSeconds(120.5f);
        ogg.setLooping(true);
        assertThat(ogg.isLooping()).isTrue();
        assertThat(ogg.getDurationSeconds()).isEqualTo(120.5f);
    }
}
