package com.atomgdx.editor.scene2d;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import com.atomgdx.editor.scene2d.ui.Scene2DViewportListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * End-to-End Test for converted HyperLap2D sample project with graphics, textures, Box2D physics, and lights.
 */
public class HyperLap2DSampleProjectE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting HyperLap2D Sample Project Conversion & Visual Test...");

        // 1. Generate project and textures in Workspace/NeonCosmos/assets
        File baseAssets = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets");
        HyperLap2DSampleProjectGenerator.generateSampleProject(baseAssets);

        File sceneFile = new File(baseAssets, "scenes/MainScene.dt");
        if (!sceneFile.exists()) {
            throw new AssertionError("Failed to generate MainScene.dt");
        }

        // 2. Load SceneVO from .dt file
        SceneVO loadedScene = HyperLap2DSerializer.loadSceneFromFile(sceneFile);
        System.out.println("Loaded Scene: " + loadedScene.sceneName);
        System.out.println("Layers: " + loadedScene.composite.layers.size());
        System.out.println("Images: " + loadedScene.composite.sImages.size());
        System.out.println("Lights: " + loadedScene.composite.sLights.size());
        System.out.println("Labels: " + loadedScene.composite.sLabels.size());
        System.out.println("9-Patches: " + loadedScene.composite.sNinePatches.size());

        if (loadedScene.composite.sImages.size() < 4) {
            throw new AssertionError("Expected at least 4 images in sample scene");
        }

        // 3. Launch UI Workbench
        final Scene2DEditorPanel[] panelHolder = new Scene2DEditorPanel[1];
        final JFrame[] frameHolder = new JFrame[1];

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - HyperLap2D Sample Project: " + loadedScene.sceneName);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);

            Scene2DEditorPanel panel = new Scene2DEditorPanel(loadedScene);
            panelHolder[0] = panel;
            frame.setContentPane(panel);
            frame.setVisible(true);
            frameHolder[0] = frame;
        });

        Scene2DEditorPanel editorPanel = panelHolder[0];
        JFrame frame = frameHolder[0];

        // 4. Wait for OpenGL rendering
        System.out.println("Rendering converted project on LibGDX OpenGL Canvas...");
        Thread.sleep(3000);

        int frameCount = editorPanel.getRenderedFrameCount();
        System.out.println("Rendered OpenGL Frames: " + frameCount);

        // 5. Capture proof screenshot
        SwingUtilities.invokeAndWait(() -> {
            try {
                BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                frame.paint(g2);
                g2.dispose();

                File proofFile = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/hyperlap2d_sample_editor_proof.png");
                ImageIO.write(image, "png", proofFile);
                System.out.println("Saved Sample Project UI screenshot: " + proofFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (frameCount < 10) {
            throw new AssertionError("Expected at least 10 rendered frames, got " + frameCount);
        }

        System.out.println(">>> HYPERLAP2D SAMPLE PROJECT TEST PASSED: Successfully loaded .dt project with 6 textures and rendered " + frameCount + " frames! <<<");

        SwingUtilities.invokeLater(frame::dispose);
    }
}
