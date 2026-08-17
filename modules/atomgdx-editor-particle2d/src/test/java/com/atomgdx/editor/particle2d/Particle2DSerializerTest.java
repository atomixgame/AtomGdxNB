package com.atomgdx.editor.particle2d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Particle2DSerializerTest {

    @Test
    public void testLoadAndSaveParticleEffect(@TempDir File tempDir) throws IOException {
        // Create sample effect
        Particle2DEffectModel effect = new Particle2DEffectModel("PlasmaExplosion");
        Particle2DEmitterModel emitter = new Particle2DEmitterModel("CoreSparks");
        emitter.setDuration(1200f);
        emitter.setMinParticleCount(10);
        emitter.setMaxParticleCount(500);
        emitter.setEmissionRate(80f);
        emitter.setLifeMin(600f);
        emitter.setLifeMax(1500f);
        emitter.setScaleMin(8f);
        emitter.setScaleMax(24f);
        emitter.setVelocityMin(80f);
        emitter.setVelocityMax(200f);
        emitter.setAngleMin(0f);
        emitter.setAngleMax(360f);
        emitter.setAdditive(true);
        emitter.setContinuous(true);
        emitter.setImagePath("particle.png");
        effect.addEmitter(emitter);

        File saveFile = new File(tempDir, "plasma_explosion.p");
        Particle2DSerializer.saveEffect(effect, saveFile);

        assertTrue(saveFile.exists(), "Saved effect file must exist");
        assertTrue(saveFile.length() > 0, "Saved effect file must not be empty");

        // Load back
        Particle2DEffectModel loaded = Particle2DSerializer.loadEffect(saveFile);
        assertNotNull(loaded);
        assertEquals(1, loaded.getEmitters().size());

        Particle2DEmitterModel loadedEmitter = loaded.getEmitters().get(0);
        assertEquals("CoreSparks", loadedEmitter.getName());
        assertEquals(500, loadedEmitter.getMaxParticleCount());
        assertEquals(1200f, loadedEmitter.getDuration(), 0.01f);
        assertEquals(80f, loadedEmitter.getEmissionRate(), 0.01f);
        assertTrue(loadedEmitter.isAdditive());
        assertTrue(loadedEmitter.isContinuous());
    }

    @Test
    public void testLoadNeonCosmosParticle() throws IOException {
        File neonParticle = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/particles/plasma_burst.p");
        if (neonParticle.exists()) {
            Particle2DEffectModel effect = Particle2DSerializer.loadEffect(neonParticle);
            assertNotNull(effect);
            assertFalse(effect.getEmitters().isEmpty());
            Particle2DEmitterModel emitter = effect.getEmitters().get(0);
            assertEquals(300, emitter.getMaxParticleCount());
            assertTrue(emitter.isAdditive());
            assertTrue(emitter.isContinuous());
        }
    }
}
