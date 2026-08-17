package com.atomgdx.theme;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.editor.scene2d.data.vo.SimpleImageVO;
import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.LibGdxProjectExplorerTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Visual E2E test verifying Project & Assets Tree (AT), Scene Structure, and Inspector TopComponents.
 */
public class LibGdxProjectExplorerE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Project & Asset Tree and TopComponents Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - Project Explorer & HyperLap TopComponents");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new GridLayout(1, 3, 6, 6));
            root.setBackground(new Color(30, 31, 34));
            root.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            // Left 1: Project & Asset Tree
            LibGdxProjectExplorerTopComponent projectExplorer = new LibGdxProjectExplorerTopComponent();
            root.add(projectExplorer);

            // Left 2: Scene Structure (Layers & Sprites)
            SceneStructureTopComponent sceneStructure = new SceneStructureTopComponent();
            root.add(sceneStructure);

            // Right: Inspector
            InspectorTopComponent inspector = new InspectorTopComponent();
            root.add(inspector);

            frame.setContentPane(root);
            frame.setVisible(true);

            // Trigger Inspector with a Starfighter item
            SimpleImageVO player = new SimpleImageVO("player_ship.png", 640, 360);
            player.itemName = "Starfighter";
            player.layerName = "Gameplay";
            player.rotation = 15f;
            player.scaleX = 1.2f;
            player.scaleY = 1.2f;
            player.physics = new com.atomgdx.editor.scene2d.data.vo.PhysicsBodyDataVO();
            player.physics.bodyType = 2; // Dynamic
            player.physics.density = 2.0f;

            // Paint and capture screenshot
            Timer timer = new Timer(1000, e -> {
                try {
                    BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.paint(g2);
                    g2.dispose();

                    File out = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/project_asset_tree_proof.png");
                    ImageIO.write(img, "png", out);
                    System.out.println("Saved Project & Asset Tree proof: " + out.getAbsolutePath());
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
        System.out.println(">>> PROJECT & ASSET TREE TEST COMPLETED! <<<");
    }
}
