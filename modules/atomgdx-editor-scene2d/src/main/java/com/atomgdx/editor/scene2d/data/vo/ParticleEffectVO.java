package com.atomgdx.editor.scene2d.data.vo;

/**
 * 2D Particle Effect emitter in HyperLap2D.
 */
public class ParticleEffectVO extends MainItemVO {
    private static final long serialVersionUID = 1L;

    public String particleName = "";
    public float particleWidth = 100f;
    public float particleHeight = 100f;
    public boolean isAutoStart = true;

    public ParticleEffectVO() {}

    public ParticleEffectVO(String particleName, float x, float y) {
        this.particleName = particleName;
        this.itemName = particleName;
        this.x = x;
        this.y = y;
    }
}
