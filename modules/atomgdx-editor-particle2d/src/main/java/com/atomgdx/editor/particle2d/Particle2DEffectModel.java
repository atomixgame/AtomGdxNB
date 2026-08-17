package com.atomgdx.editor.particle2d;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a complete 2D particle effect composed of multiple emitters.
 */
public class Particle2DEffectModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name = "New Effect";
    private final List<Particle2DEmitterModel> emitters = new ArrayList<>();

    public Particle2DEffectModel() {
        // Default emitter
        emitters.add(new Particle2DEmitterModel("Main Emitter"));
    }

    public Particle2DEffectModel(String name) {
        this.name = name;
        emitters.add(new Particle2DEmitterModel("Main Emitter"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Particle2DEmitterModel> getEmitters() {
        return Collections.unmodifiableList(emitters);
    }

    public void addEmitter(Particle2DEmitterModel emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(Particle2DEmitterModel emitter) {
        if (emitters.size() > 1) {
            emitters.remove(emitter);
        }
    }

    public void moveEmitterUp(int index) {
        if (index > 0 && index < emitters.size()) {
            Collections.swap(emitters, index, index - 1);
        }
    }

    public void moveEmitterDown(int index) {
        if (index >= 0 && index < emitters.size() - 1) {
            Collections.swap(emitters, index, index + 1);
        }
    }
}
