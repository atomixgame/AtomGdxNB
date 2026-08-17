package com.atomgdx.editor.particle3d;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a 3D Flame particle system with particle batches and physics influencers.
 */
public class Particle3DEffectModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RendererType {
        BILLBOARD,
        POINT_SPRITE,
        MODEL_INSTANCE
    }

    public static class Emitter3D implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name = "3D Emitter";
        private RendererType rendererType = RendererType.BILLBOARD;
        private int maxParticleCount = 500;
        private float lifeTime = 2.0f;
        private float spawnRate = 100.0f;
        private boolean useGravity = false;
        private float gravityStrength = -9.8f;
        private boolean useColorInfluencer = true;

        public Emitter3D(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public RendererType getRendererType() { return rendererType; }
        public void setRendererType(RendererType rendererType) { this.rendererType = rendererType; }

        public int getMaxParticleCount() { return maxParticleCount; }
        public void setMaxParticleCount(int maxParticleCount) { this.maxParticleCount = maxParticleCount; }

        public float getLifeTime() { return lifeTime; }
        public void setLifeTime(float lifeTime) { this.lifeTime = lifeTime; }

        public float getSpawnRate() { return spawnRate; }
        public void setSpawnRate(float spawnRate) { this.spawnRate = spawnRate; }

        public boolean isUseGravity() { return useGravity; }
        public void setUseGravity(boolean useGravity) { this.useGravity = useGravity; }

        public float getGravityStrength() { return gravityStrength; }
        public void setGravityStrength(float gravityStrength) { this.gravityStrength = gravityStrength; }

        public boolean isUseColorInfluencer() { return useColorInfluencer; }
        public void setUseColorInfluencer(boolean useColorInfluencer) { this.useColorInfluencer = useColorInfluencer; }
    }

    private String name = "New 3D Flame Effect";
    private final List<Emitter3D> emitters = new ArrayList<>();

    public Particle3DEffectModel() {
        emitters.add(new Emitter3D("Primary 3D Emitter"));
    }

    public Particle3DEffectModel(String name) {
        this.name = name;
        emitters.add(new Emitter3D("Primary 3D Emitter"));
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Emitter3D> getEmitters() { return Collections.unmodifiableList(emitters); }
    public void addEmitter(Emitter3D emitter) { emitters.add(emitter); }
    public void removeEmitter(Emitter3D emitter) { if (emitters.size() > 1) emitters.remove(emitter); }
}
