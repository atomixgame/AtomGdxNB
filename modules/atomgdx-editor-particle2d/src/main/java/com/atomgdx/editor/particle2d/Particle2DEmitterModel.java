package com.atomgdx.editor.particle2d;

import java.io.Serializable;

/**
 * Model representing a 2D particle emitter with curve parameters.
 */
public class Particle2DEmitterModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name = "Default Emitter";
    private boolean enabled = true;
    private boolean additive = true;
    private boolean behind = false;
    private boolean attached = false;
    private boolean continuous = true;

    // Emitter properties
    private float delay = 0f;
    private float duration = 1000f; // ms
    private int minParticleCount = 0;
    private int maxParticleCount = 200;
    private float emissionRate = 50f;
    private float lifeMin = 500f; // ms
    private float lifeMax = 1000f;
    private float scaleMin = 10f;
    private float scaleMax = 30f;
    private float velocityMin = 50f;
    private float velocityMax = 150f;
    private float angleMin = 0f;
    private float angleMax = 360f;
    private float rotationMin = 0f;
    private float rotationMax = 360f;
    private float wind = 0f;
    private float gravity = 0f;
    private String imagePath = "";

    public Particle2DEmitterModel() {
    }

    public Particle2DEmitterModel(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isAdditive() { return additive; }
    public void setAdditive(boolean additive) { this.additive = additive; }

    public boolean isBehind() { return behind; }
    public void setBehind(boolean behind) { this.behind = behind; }

    public boolean isAttached() { return attached; }
    public void setAttached(boolean attached) { this.attached = attached; }

    public boolean isContinuous() { return continuous; }
    public void setContinuous(boolean continuous) { this.continuous = continuous; }

    public float getDelay() { return delay; }
    public void setDelay(float delay) { this.delay = delay; }

    public float getDuration() { return duration; }
    public void setDuration(float duration) { this.duration = duration; }

    public int getMinParticleCount() { return minParticleCount; }
    public void setMinParticleCount(int minParticleCount) { this.minParticleCount = minParticleCount; }

    public int getMaxParticleCount() { return maxParticleCount; }
    public void setMaxParticleCount(int maxParticleCount) { this.maxParticleCount = maxParticleCount; }

    public float getEmissionRate() { return emissionRate; }
    public void setEmissionRate(float emissionRate) { this.emissionRate = emissionRate; }

    public float getLifeMin() { return lifeMin; }
    public void setLifeMin(float lifeMin) { this.lifeMin = lifeMin; }

    public float getLifeMax() { return lifeMax; }
    public void setLifeMax(float lifeMax) { this.lifeMax = lifeMax; }

    public float getScaleMin() { return scaleMin; }
    public void setScaleMin(float scaleMin) { this.scaleMin = scaleMin; }

    public float getScaleMax() { return scaleMax; }
    public void setScaleMax(float scaleMax) { this.scaleMax = scaleMax; }

    public float getVelocityMin() { return velocityMin; }
    public void setVelocityMin(float velocityMin) { this.velocityMin = velocityMin; }

    public float getVelocityMax() { return velocityMax; }
    public void setVelocityMax(float velocityMax) { this.velocityMax = velocityMax; }

    public float getAngleMin() { return angleMin; }
    public void setAngleMin(float angleMin) { this.angleMin = angleMin; }

    public float getAngleMax() { return angleMax; }
    public void setAngleMax(float angleMax) { this.angleMax = angleMax; }

    public float getRotationMin() { return rotationMin; }
    public void setRotationMin(float rotationMin) { this.rotationMin = rotationMin; }

    public float getRotationMax() { return rotationMax; }
    public void setRotationMax(float rotationMax) { this.rotationMax = rotationMax; }

    public float getWind() { return wind; }
    public void setWind(float wind) { this.wind = wind; }

    public float getGravity() { return gravity; }
    public void setGravity(float gravity) { this.gravity = gravity; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
