package com.atomgdx.editor.scene2d;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * End-to-End Visual and OpenGL Test for full HyperLap2D Scene Editor in AtomGDX.
 */
public class HyperLap2DE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting HyperLap2D Scene Editor E2E Test...");

        // 1. Initialize Scene Model with items
        SceneVO scene = new SceneVO("CosmicOutpost");
        scene.composite.layers.add(new LayerItemVO("Background"));
        scene.composite.layers.add(new LayerItemVO("Gameplay"));
        scene.composite.layers.add(new LayerItemVO("Foreground"));

        SimpleImageVO player = new SimpleImageVO("hero_starfighter", 640, 360);
        player.layerName = "Gameplay";
        player.scaleX = 1.2f;
        player.scaleY = 1.2f;
        player.rotation = 15f;
        player.physics = new PhysicsBodyDataVO();
        player.physics.bodyType = 2;
        player.physics.density = 2.0f;
        scene.composite.sImages.add(player);

        SimpleImageVO asteroid = new SimpleImageVO("space_station", 450, 250);
        asteroid.layerName = "Background";
        scene.composite.sImages.add(asteroid);

        LabelVO levelLabel = new LabelVO("Sector 7G - Hyperspace", 500, 600);
        levelLabel.layerName = "Foreground";
        scene.composite.sLabels.add(levelLabel);

        // 2. Instantiate Scene2DEditorPanel and mount in Frame
        final Scene2DEditorPanel[] panelHolder = new Scene2DEditorPanel[1];
        final JFrame[] frameHolder = new JFrame[1];

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX HyperLap2D Scene Editor");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);

            Scene2DEditorPanel panel = new Scene2DEditorPanel(scene);
            panelHolder[0] = panel;
            frame.setContentPane(panel);
            frame.setVisible(true);
            frameHolder[0] = frame;
        });

        Scene2DEditorPanel editorPanel = panelHolder[0];
        JFrame frame = frameHolder[0];

        // 3. Allow OpenGL and Swing threads to render
        System.out.println("Waiting for OpenGL frames to render on LwjglAWTCanvas...");
        Thread.sleep(3000);

        int renderedFrames = editorPanel.getRenderedFrameCount();
        System.out.println("Total OpenGL Rendered Frames: " + renderedFrames);

        // 4. Capture proof screenshot of the entire workbench UI
        SwingUtilities.invokeAndWait(() -> {
            try {
                BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                frame.paint(g2);
                g2.dispose();

                File proofFile = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/hyperlap2d_editor_proof.png");
                ImageIO.write(image, "png", proofFile);
                System.out.println("Saved HyperLap2D UI screenshot: " + proofFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Assertions
        if (renderedFrames < 10) {
            throw new AssertionError("Expected at least 10 rendered OpenGL frames, but got " + renderedFrames);
        }

        System.out.println(">>> HYPERLAP2D E2E TEST PASSED: Full scene editor rendered " + renderedFrames + " frames! <<<");

        SwingUtilities.invokeLater(frame::dispose);
    }
}
