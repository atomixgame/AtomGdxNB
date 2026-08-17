package com.atomgdx.editor.particle2d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class Particle2DSerializerTest {

    @Test
    void testSaveAndLoadEffect(@TempDir File tempDir) throws IOException {
        Particle2DEffectModel effect = new Particle2DEffectModel("RocketThruster");
        Particle2DEmitterModel emitter = new Particle2DEmitterModel("CoreFlame");
        emitter.setDuration(2500f);
        emitter.setMinParticleCount(20);
        emitter.setMaxParticleCount(400);
        emitter.setEmissionRate(150f);
        emitter.setAdditive(true);
        emitter.setContinuous(true);
        emitter.setImagePath("particles/particle.png");

        effect.addEmitter(emitter);

        File outputFile = new File(tempDir, "RocketThruster.particle");
        Particle2DSerializer.saveEffect(effect, outputFile);
        assertThat(outputFile).exists().isNotEmpty();

        Particle2DEffectModel loaded = Particle2DSerializer.loadEffect(outputFile);
        assertThat(loaded.getEmitters()).isNotEmpty();
        Particle2DEmitterModel loadedEmitter = loaded.getEmitters().get(0);
        assertThat(loadedEmitter.getName()).isNotEmpty();
        assertThat(loadedEmitter.getDuration()).isEqualTo(2500f);
        assertThat(loadedEmitter.getMaxParticleCount()).isEqualTo(400);
        assertThat(loadedEmitter.isAdditive()).isTrue();
    }
}
