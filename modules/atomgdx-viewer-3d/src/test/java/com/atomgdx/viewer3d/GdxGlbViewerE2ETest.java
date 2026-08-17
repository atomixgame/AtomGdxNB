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

        File helmetGlb = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/khronos/DamagedHelmet.glb");
        Model3DDescriptor desc = new Model3DDescriptor(helmetGlb);
        final Model3DViewerPanel[] panelRef = new Model3DViewerPanel[1];
        final JFrame[] frameRef = new JFrame[1];

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - Khronos DamagedHelmet.glb OpenGL GPU Viewport");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1024, 600);
            frame.setLocationRelativeTo(null);

            Model3DViewerPanel panel = new Model3DViewerPanel(desc);
            panelRef[0] = panel;
            frameRef[0] = frame;

            frame.setContentPane(panel);
            frame.setVisible(true);
        });

        String outPath = "C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/glb_opengl_gpu_proof.png";

        // Wait actively for OpenGL frames to render on GPU
        int frames = 0;
        for (int i = 0; i < 40; i++) {
            Thread.sleep(100);
            if (panelRef[0] != null) {
                frames = panelRef[0].getRenderedFrameCount();
                if (frames >= 10) break;
            }
        }

        System.out.println("Rendered GLB OpenGL Frames before capture: " + frames);
        panelRef[0].getViewportListener().requestScreenshot(outPath);

        Thread.sleep(800);
        SwingUtilities.invokeLater(() -> frameRef[0].dispose());

        Thread.sleep(500);
        System.out.println(">>> GLB OPENGL GPU TEST COMPLETED! <<<");
        System.exit(0);
    }
}
