package com.atomgdx.editor.particle3d;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Particle3DEffectModelTest {

    @Test
    void testParticle3DEffectManagement() {
        Particle3DEffectModel effect = new Particle3DEffectModel("WarpDrivePlasma");
        assertThat(effect.getName()).isEqualTo("WarpDrivePlasma");
        assertThat(effect.getEmitters()).hasSize(1);

        Particle3DEffectModel.Emitter3D emitter = new Particle3DEffectModel.Emitter3D("CoreJets");
        emitter.setRendererType(Particle3DEffectModel.RendererType.POINT_SPRITE);
        emitter.setSpawnRate(250.0f);

        effect.addEmitter(emitter);
        assertThat(effect.getEmitters()).hasSize(2);
        assertThat(effect.getEmitters().get(1).getRendererType()).isEqualTo(Particle3DEffectModel.RendererType.POINT_SPRITE);
    }
}
