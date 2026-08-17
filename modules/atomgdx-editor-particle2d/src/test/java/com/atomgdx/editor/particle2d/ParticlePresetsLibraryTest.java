package com.atomgdx.editor.particle2d;

import com.atomgdx.editor.particle2d.presets.ParticlePreset;
import com.atomgdx.editor.particle2d.presets.ParticlePresetsLibrary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParticlePresetsLibraryTest {

    @Test
    public void testPresetsLibraryCountAndIntegrity() {
        List<ParticlePreset> presets = ParticlePresetsLibrary.getAllPresets();
        assertEquals(100, presets.size(), "Preset library must contain exactly 100 presets");

        List<String> categories = ParticlePresetsLibrary.getCategories();
        assertTrue(categories.size() >= 6, "Must contain at least 6 categories");
        assertTrue(categories.contains("Sci-Fi & Energy"));
        assertTrue(categories.contains("Combat & Weapons"));
        assertTrue(categories.contains("Magic & Fantasy"));
        assertTrue(categories.contains("Nature & Weather"));
        assertTrue(categories.contains("Explosions & Impacts"));
        assertTrue(categories.contains("Atmospheric & Ambient"));

        for (ParticlePreset p : presets) {
            assertNotNull(p.getName());
            assertNotNull(p.getCategory());
            assertFalse(p.getTags().isEmpty(), "Preset " + p.getName() + " must have tags");
            Particle2DEffectModel effect = p.createEffect();
            assertNotNull(effect);
            assertEquals(1, effect.getEmitters().size());
            Particle2DEmitterModel emitter = effect.getEmitters().get(0);
            assertTrue(emitter.getMaxParticleCount() > 0);
            assertTrue(emitter.getDuration() > 0);
        }
    }

    @Test
    public void testSearchAndFiltering() {
        List<ParticlePreset> firePresets = ParticlePresetsLibrary.search("All Categories", "fire");
        assertFalse(firePresets.isEmpty(), "Search for 'fire' must yield presets");

        List<ParticlePreset> scifiCategory = ParticlePresetsLibrary.search("Sci-Fi & Energy", "");
        assertEquals(20, scifiCategory.size(), "Sci-Fi category must contain 20 presets");
    }
}
