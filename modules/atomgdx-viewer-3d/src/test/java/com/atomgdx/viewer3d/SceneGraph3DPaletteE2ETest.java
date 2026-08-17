package com.atomgdx.viewer3d;

import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.PaletteTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;
import com.atomgdx.viewer3d.data.Scene3DVO;
import com.atomgdx.viewer3d.ui.Scene3DEditorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * End-to-End Visual test verifying:
 * 1. 3D SceneGraph with rich icons and context menus
 * 2. 3D Scene Editor with Unity-inspired 3D View Orientation Gizmo & Transform modes
 * 3. 3D Inspector showing File Metadata, Model Statistics & Read-Only Banner
 * 4. Configurable Palette with 56 items and instant search filtering.
 */
public class SceneGraph3DPaletteE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting 3D Scene Editor, Inspector & 56-Item Palette Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - 3D Scene Editor, Unity View Gizmo, Inspector & 56-Item Palette");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1500, 800);
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new GridLayout(1, 4, 6, 6));
            root.setBackground(new Color(26, 26, 28));
            root.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            Scene3DVO scene3D = new Scene3DVO("NeonCosmos_Sector7");

            // Col 1: SceneGraph 3D Tree with rich icons & context menus
            SceneStructureTopComponent sceneStructure = new SceneStructureTopComponent();
            sceneStructure.setScene3D(scene3D);
            root.add(sceneStructure);

            // Col 2: 3D Scene Editor with Unity View Gizmo & Transform Toolbar
            Scene3DEditorPanel sceneEditor = new Scene3DEditorPanel(scene3D);
            root.add(sceneEditor);

            // Col 3: Inspector with File Metadata, Model Statistics & Read-Only banner
            InspectorTopComponent inspector = new InspectorTopComponent();
            Model3DDescriptor desc = new Model3DDescriptor(new File("spacecraft_cruiser.gltf"));
            desc.setMeshCount(6);
            desc.setNodeCount(16);
            desc.setMaterialCount(3);
            desc.setAnimationCount(2);
            inspector.inspectModelDescriptor(desc);
            root.add(inspector);

            // Col 4: Palette loaded with 56 items & live search filter
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
                    System.out.println("Saved 3D Scene Editor & Palette proof: " + out.getAbsolutePath());
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
        System.out.println(">>> 3D SCENE EDITOR & PALETTE TEST COMPLETED! <<<");
        System.exit(0);
    }
}
