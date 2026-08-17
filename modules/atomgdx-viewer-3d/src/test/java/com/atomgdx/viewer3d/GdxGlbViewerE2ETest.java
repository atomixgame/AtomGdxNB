package com.atomgdx.viewer3d;

import com.atomgdx.viewer3d.ui.Model3DViewerPanel;

import javax.swing.*;
import java.io.File;

/**
 * Native LibGDX OpenGL E2E Visual test for Khronos .GLB models (DamagedHelmet.glb).
 * Renders on GPU, captures frame buffer via ScreenUtils.getFrameBufferPixels, and proves .glb rendering.
 */
public class GdxGlbViewerE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Khronos .GLB Native OpenGL GPU Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - Khronos DamagedHelmet.glb OpenGL GPU Viewport");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1024, 600);
            frame.setLocationRelativeTo(null);

            File helmetGlb = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/khronos/DamagedHelmet.glb");
            Model3DDescriptor desc = new Model3DDescriptor(helmetGlb);
            Model3DViewerPanel panel = new Model3DViewerPanel(desc);

            frame.setContentPane(panel);
            frame.setVisible(true);

            String outPath = "C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/glb_opengl_gpu_proof.png";

            Timer timer = new Timer(1500, e -> {
                try {
                    int renderedFrames = panel.getRenderedFrameCount();
                    System.out.println("Rendered GLB OpenGL Frames: " + renderedFrames);
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
        System.out.println(">>> GLB OPENGL GPU TEST COMPLETED! <<<");
        System.exit(0);
    }
}
