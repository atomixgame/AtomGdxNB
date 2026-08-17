package com.atomgdx.editor.particle2d;

import com.atomgdx.core.viewport.LwjglNativesLoader;
import com.atomgdx.editor.particle2d.presets.ParticlePresetsLibrary;
import com.atomgdx.editor.particle2d.ui.Particle2DEditorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Particle2DE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Particle2D E2E Visual Test...");

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
            frame.requestFocus();
        });

        System.out.println("Frame displayed, waiting 3s for LibGDX OpenGL frames to render...");
        Thread.sleep(3000);

        // Capture frame screenshot via Swing painting as well as screen bounds
        BufferedImage swingCapture = new BufferedImage(1200, 750, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = swingCapture.createGraphics();
        editor.paint(g2);
        g2.dispose();

        File swingProof = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/particle_editor_swing_proof.png");
        ImageIO.write(swingCapture, "png", swingProof);
        System.out.println("Saved Swing UI screenshot: " + swingProof.getAbsolutePath());

        // Screen capture of visible window
        Point loc = frame.getLocationOnScreen();
        Dimension size = frame.getSize();
        Robot robot = new Robot();
        BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(loc.x, loc.y, size.width, size.height));
        File screenProof = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/particle_editor_e2e_proof.png");
        ImageIO.write(screenCapture, "png", screenProof);
        System.out.println("Saved Screen capture: " + screenProof.getAbsolutePath());

        int renderedFrames = editor.getRenderedFrameCount();
        System.out.println("Total OpenGL Rendered Frames: " + renderedFrames);

        if (renderedFrames > 30) {
            System.out.println(">>> E2E TEST PASSED: LibGDX LwjglAWTCanvas actively rendered " + renderedFrames + " frames! <<<");
        } else {
            System.err.println(">>> E2E TEST FAILED: Insufficient frames rendered! (" + renderedFrames + ") <<<");
        }

        SwingUtilities.invokeLater(frame::dispose);
        System.exit(0);
    }
}
