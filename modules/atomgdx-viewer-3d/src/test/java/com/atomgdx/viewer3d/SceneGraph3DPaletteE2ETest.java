package com.atomgdx.viewer3d;

import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.PaletteTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;
import com.atomgdx.viewer3d.data.Prefab3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;
import com.atomgdx.viewer3d.ui.Model3DViewerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * End-to-End Visual test for 3D SceneGraph, 3D OpenGL Viewport, 3D Inspector, and Palette.
 */
public class SceneGraph3DPaletteE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting 3D SceneGraph, Inspector & Palette Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - 3D SceneGraph, Inspector & Prefab Palette");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1500, 800);
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new GridLayout(1, 4, 6, 6));
            root.setBackground(new Color(26, 26, 28));
            root.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            Scene3DVO scene3D = new Scene3DVO("NeonCosmos_Sector7");

            // Col 1: SceneGraph 3D Tree
            SceneStructureTopComponent sceneStructure = new SceneStructureTopComponent();
            sceneStructure.setScene3D(scene3D);
            root.add(sceneStructure);

            // Col 2: 3D OpenGL Viewport
            Model3DViewerPanel viewportPanel = new Model3DViewerPanel(new Model3DDescriptor(new File("spacecraft.gltf")));
            root.add(viewportPanel);

            // Col 3: 3D Inspector with PBR Material & Bullet Physics
            InspectorTopComponent inspector = new InspectorTopComponent();
            Prefab3DVO ship = Prefab3DVO.createSpacecraftFighter();
            inspector.inspectNode3D(ship.rootNode);
            root.add(inspector);

            // Col 4: 3D Palette (Primitives, Prefabs, Materials)
            PaletteTopComponent palette = new PaletteTopComponent();
            root.add(palette);

            frame.setContentPane(root);
            frame.setVisible(true);

            Timer timer = new Timer(1500, e -> {
                try {
                    BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.paint(g2);
                    g2.dispose();

                    File out = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/scenegraph_3d_palette_proof.png");
                    ImageIO.write(img, "png", out);
                    System.out.println("Saved 3D SceneGraph & Palette proof: " + out.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    frame.dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        });

        Thread.sleep(2500);
        System.out.println(">>> 3D SCENEGRAPH & PALETTE TEST COMPLETED! <<<");
        System.exit(0);
    }
}
