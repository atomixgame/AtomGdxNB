package com.atomgdx.viewer3d;

import com.atomgdx.viewer3d.ui.Model3DViewerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * End-to-End Visual test for the Native LibGDX 3D OpenGL Viewport.
 */
public class Gdx3DViewerE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting LibGDX 3D OpenGL Viewport Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - 3D PBR Model Viewer (OpenGL)");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1024, 600);
            frame.setLocationRelativeTo(null);

            Model3DViewerPanel panel = new Model3DViewerPanel(new Model3DDescriptor(new File("spacecraft.gltf")));
            frame.setContentPane(panel);
            frame.setVisible(true);

            String outPath = "C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/model3d_opengl_gpu_proof.png";

            Timer timer = new Timer(1500, e -> {
                try {
                    int renderedFrames = panel.getRenderedFrameCount();
                    System.out.println("Rendered 3D OpenGL Frames: " + renderedFrames);
                    panel.getViewportListener().requestScreenshot(outPath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            timer.setRepeats(false);
            timer.start();

            Timer closeTimer = new Timer(2200, e -> frame.dispose());
            closeTimer.setRepeats(false);
            closeTimer.start();
        });

        Thread.sleep(3000);
        System.out.println(">>> 3D VIEWPORT TEST COMPLETED! <<<");
        System.exit(0);
    }
}
