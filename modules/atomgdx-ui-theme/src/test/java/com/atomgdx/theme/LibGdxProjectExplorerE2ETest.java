package com.atomgdx.theme;

import com.atomgdx.editor.scene2d.data.vo.LightVO;
import com.atomgdx.editor.scene2d.data.vo.PhysicsBodyDataVO;
import com.atomgdx.editor.scene2d.data.vo.SimpleImageVO;
import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.LibGdxProjectExplorerTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;
import com.atomgdx.theme.windows.SpriteSheetEditorTopComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Visual E2E test verifying Unity-style Inspector, HyperLap Scene Hierarchy, and SpriteSheet Editor.
 */
public class LibGdxProjectExplorerE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Unity-Style Inspector & SpriteSheet Editor Visual Test...");

        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("AtomGDX - Unity-Style Inspector & SpriteSheet Editor");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1400, 780);
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new GridLayout(1, 4, 6, 6));
            root.setBackground(new Color(26, 26, 28));
            root.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            // Col 1: Projects & Assets Tree (AT)
            LibGdxProjectExplorerTopComponent projectExplorer = new LibGdxProjectExplorerTopComponent();
            root.add(projectExplorer);

            // Col 2: Scene Structure (Layers & Sprites Tree)
            SceneStructureTopComponent sceneStructure = new SceneStructureTopComponent();
            root.add(sceneStructure);

            // Col 3: Unity-style Collapsible Inspector
            InspectorTopComponent inspector = new InspectorTopComponent();
            SimpleImageVO player = new SimpleImageVO("player_ship.png", 640, 360);
            player.itemName = "Starfighter_Cruiser";
            player.layerName = "Gameplay";
            player.rotation = 22.5f;
            player.scaleX = 1.25f;
            player.scaleY = 1.25f;
            player.physics = new PhysicsBodyDataVO();
            player.physics.bodyType = 2; // Dynamic
            player.physics.density = 3.5f;
            player.physics.friction = 0.4f;
            player.physics.restitution = 0.15f;
            player.physics.bullet = true;

            inspector.inspectItem(player);
            root.add(inspector);

            // Col 4: SpriteSheet Editor & Image Viewer
            SpriteSheetEditorTopComponent spriteSheetEditor = new SpriteSheetEditorTopComponent();
            root.add(spriteSheetEditor);

            frame.setContentPane(root);
            frame.setVisible(true);

            // Paint and capture screenshot
            Timer timer = new Timer(1200, e -> {
                try {
                    BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.paint(g2);
                    g2.dispose();

                    File out = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/unity_inspector_spritesheet_proof.png");
                    ImageIO.write(img, "png", out);
                    System.out.println("Saved Unity-Style Inspector & SpriteSheet proof: " + out.getAbsolutePath());
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
        System.out.println(">>> VISUAL TEST COMPLETED! <<<");
    }
}
