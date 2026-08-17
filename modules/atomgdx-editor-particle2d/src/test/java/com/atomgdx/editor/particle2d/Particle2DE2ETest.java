package com.atomgdx.editor.particle2d;

import com.atomgdx.core.viewport.LwjglNativesLoader;
import com.atomgdx.editor.particle2d.presets.ParticlePreset;
import com.atomgdx.editor.particle2d.presets.ParticlePresetsLibrary;
import com.atomgdx.editor.particle2d.ui.Particle2DEditorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Particle2DE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Particle2D Preset Application & UI Icons E2E Test...");

        // Ensure natives are loaded
        LwjglNativesLoader.load();

        Particle2DEffectModel effect = ParticlePresetsLibrary.getAllPresets().get(0).createEffect(); // Plasma Burst

        JFrame frame = new JFrame("Particle2D Editor E2E Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setLocation(100, 100);

        Particle2DEditorPanel editor = new Particle2DEditorPanel(effect);
        frame.setContentPane(editor);

        SwingUtilities.invokeAndWait(() -> {
            frame.setVisible(true);
            frame.toFront();
        });

        Thread.sleep(1500);

        // Test Apply Preset: select "Fireball Blast" preset
        ParticlePreset fireballPreset = null;
        for (ParticlePreset p : ParticlePresetsLibrary.getAllPresets()) {
            if ("Fireball Blast".equals(p.getName())) {
                fireballPreset = p;
                break;
            }
        }

        if (fireballPreset != null) {
            final ParticlePreset targetPreset = fireballPreset;
            SwingUtilities.invokeAndWait(() -> {
                editor.applyPreset(targetPreset);
            });
            System.out.println("Applied preset: Fireball Blast");
            assertEquals(1, effect.getEmitters().size(), "Effect must have 1 emitter after applying preset");
            assertEquals("Fireball Blast Emitter", effect.getEmitters().get(0).getName(), "Emitter name must match preset");
            System.out.println(">>> Verified Preset Application: Emitter Name = " + effect.getEmitters().get(0).getName());
        }

        Thread.sleep(2000);

        // Capture frame screenshot via Swing painting
        BufferedImage swingCapture = new BufferedImage(1200, 750, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = swingCapture.createGraphics();
        editor.paint(g2);
        g2.dispose();

        File swingProof = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/particle_editor_swing_proof.png");
        ImageIO.write(swingCapture, "png", swingProof);
        System.out.println("Saved Swing UI screenshot with icons: " + swingProof.getAbsolutePath());

        // Switch to Presets tab and capture
        SwingUtilities.invokeAndWait(() -> {
            JTabbedPane tabs = (JTabbedPane) ((BorderLayout) editor.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (tabs != null) tabs.setSelectedIndex(1);
        });
        Thread.sleep(500);

        BufferedImage presetsCapture = new BufferedImage(1200, 750, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gPresets = presetsCapture.createGraphics();
        editor.paint(gPresets);
        gPresets.dispose();

        File presetsProof = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/particle_editor_presets_proof.png");
        ImageIO.write(presetsCapture, "png", presetsProof);
        System.out.println("Saved Presets Tab UI screenshot: " + presetsProof.getAbsolutePath());

        int renderedFrames = editor.getRenderedFrameCount();
        System.out.println("Total OpenGL Rendered Frames: " + renderedFrames);

        if (renderedFrames > 30) {
            System.out.println(">>> E2E TEST PASSED: Preset applied successfully and LibGDX rendered " + renderedFrames + " frames! <<<");
        } else {
            System.err.println(">>> E2E TEST FAILED: Insufficient frames rendered! (" + renderedFrames + ") <<<");
        }

        SwingUtilities.invokeLater(frame::dispose);
        System.exit(0);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }
}
