package com.atomgdx.core.viewport;

import org.junit.Test;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.*;

public class DeviceResolutionTest {

    @Test
    public void testStandardPresets() {
        List<DeviceResolution> presets = DeviceResolution.getStandardPresets();
        assertNotNull(presets);
        assertFalse(presets.isEmpty());

        DeviceResolution freeAspect = presets.get(0);
        assertTrue(freeAspect.isFreeAspect());

        DeviceResolution fhd = presets.stream()
                .filter(p -> p.getName().contains("1080p"))
                .findFirst()
                .orElse(null);
        assertNotNull(fhd);
        assertEquals(1920, fhd.getWidth());
        assertEquals(1080, fhd.getHeight());
    }

    @Test
    public void testLetterboxBoundsCalculation() {
        DeviceResolution fhd = new DeviceResolution("1080p", 1920, 1080, "16:9", DeviceResolution.Category.DESKTOP_CONSOLE);

        // Exact match
        Rectangle bounds = fhd.calculateLetterboxBounds(1920, 1080, DeviceResolution.Orientation.LANDSCAPE);
        assertEquals(0, bounds.x);
        assertEquals(0, bounds.y);
        assertEquals(1920, bounds.width);
        assertEquals(1080, bounds.height);

        // Ultrawide container (black bars on left & right)
        Rectangle ultraBounds = fhd.calculateLetterboxBounds(2560, 1080, DeviceResolution.Orientation.LANDSCAPE);
        assertTrue(ultraBounds.x > 0);
        assertEquals(1920, ultraBounds.width);
        assertEquals(1080, ultraBounds.height);
    }
}
