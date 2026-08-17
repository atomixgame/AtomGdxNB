package com.atomgdx.editor.particle2d.presets;

import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.Particle2DEmitterModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Metadata and generator for a single Particle Effect preset.
 */
public class ParticlePreset {

    private final String name;
    private final String category;
    private final List<String> tags;
    private final String description;
    private final float duration;
    private final int maxCount;
    private final float emissionRate;
    private final float lifeMin;
    private final float lifeMax;
    private final float scaleMin;
    private final float scaleMax;
    private final float velMin;
    private final float velMax;
    private final float angleMin;
    private final float angleMax;
    private final float wind;
    private final float gravity;
    private final boolean additive;
    private final boolean continuous;
    private final String imagePath;

    public ParticlePreset(String name, String category, String description,
                          float duration, int maxCount, float emissionRate,
                          float lifeMin, float lifeMax, float scaleMin, float scaleMax,
                          float velMin, float velMax, float angleMin, float angleMax,
                          float wind, float gravity, boolean additive, boolean continuous,
                          String imagePath, String... tags) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.maxCount = maxCount;
        this.emissionRate = emissionRate;
        this.lifeMin = lifeMin;
        this.lifeMax = lifeMax;
        this.scaleMin = scaleMin;
        this.scaleMax = scaleMax;
        this.velMin = velMin;
        this.velMax = velMax;
        this.angleMin = angleMin;
        this.angleMax = angleMax;
        this.wind = wind;
        this.gravity = gravity;
        this.additive = additive;
        this.continuous = continuous;
        this.imagePath = imagePath;
        this.tags = Arrays.asList(tags);
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public List<String> getTags() { return tags; }
    public String getDescription() { return description; }

    public Particle2DEffectModel createEffect() {
        Particle2DEffectModel effect = new Particle2DEffectModel(name);
        effect.clearEmitters();
        Particle2DEmitterModel emitter = new Particle2DEmitterModel(name + " Emitter");
        emitter.setDuration(duration);
        emitter.setMinParticleCount(0);
        emitter.setMaxParticleCount(maxCount);
        emitter.setEmissionRate(emissionRate);
        emitter.setLifeMin(lifeMin);
        emitter.setLifeMax(lifeMax);
        emitter.setScaleMin(scaleMin);
        emitter.setScaleMax(scaleMax);
        emitter.setVelocityMin(velMin);
        emitter.setVelocityMax(velMax);
        emitter.setAngleMin(angleMin);
        emitter.setAngleMax(angleMax);
        emitter.setWind(wind);
        emitter.setGravity(gravity);
        emitter.setAdditive(additive);
        emitter.setContinuous(continuous);
        emitter.setImagePath(imagePath);
        effect.addEmitter(emitter);
        return effect;
    }
}
