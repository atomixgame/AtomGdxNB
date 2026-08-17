package com.atomgdx.editor.particle2d;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Particle2DEffectModelTest {

    @Test
    void testParticleEffectManagement() {
        Particle2DEffectModel effect = new Particle2DEffectModel("PlasmaExplosion");
        assertThat(effect.getName()).isEqualTo("PlasmaExplosion");
        assertThat(effect.getEmitters()).hasSize(1);

        Particle2DEmitterModel sparks = new Particle2DEmitterModel("Sparks");
        sparks.setAdditive(true);
        sparks.setMaxParticleCount(500);

        effect.addEmitter(sparks);
        assertThat(effect.getEmitters()).hasSize(2);
        assertThat(effect.getEmitters().get(1).getName()).isEqualTo("Sparks");

        effect.moveEmitterUp(1);
        assertThat(effect.getEmitters().get(0).getName()).isEqualTo("Sparks");
    }
}
