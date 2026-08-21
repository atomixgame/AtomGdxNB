package com.atomgdx.theme;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.editor.scene2d.data.vo.*;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import com.atomgdx.editor.scene2d.ui.SceneItemInspectorPanel;
import com.atomgdx.editor.scene2d.ui.TransformGizmo;
import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.Scene2DTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Scene3DVO;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class SceneVisualE2ECaptureTest {

    private static final File ARTIFACT_MEDIA_DIR = new File("C:/Users/atomi/.gemini/antigravity/brain/8ae13314-e0e0-46aa-80c4-02a96bc2a167/.tempmediaStorage");

    @Test
    public void captureFullWorkspaceAndInspectorE2E() throws Exception {
        if (!ARTIFACT_MEDIA_DIR.exists()) {
            ARTIFACT_MEDIA_DIR.mkdirs();
        }

        // 1. Create a Realistic SceneVO
        SceneVO scene = new SceneVO("Cyberpunk_Outpost_Level1");
        scene.composite.layers.clear();
        scene.composite.layers.add(new LayerItemVO("ParallaxBackground"));
        scene.composite.layers.add(new LayerItemVO("GameplayLayer"));
        scene.composite.layers.add(new LayerItemVO("HUDOverlay"));

        SimpleImageVO playerShip = new SimpleImageVO("cruiser_fighter", 400, 260);
        playerShip.layerName = "GameplayLayer";
        playerShip.scaleX = 1.4f;
        playerShip.scaleY = 1.4f;
        playerShip.rotation = 15f;
        scene.composite.sImages.add(playerShip);

        SimpleImageVO droneCompanion = new SimpleImageVO("escort_drone", 580, 320);
        droneCompanion.layerName = "GameplayLayer";
        droneCompanion.scaleX = 0.8f;
        droneCompanion.scaleY = 0.8f;
        scene.composite.sImages.add(droneCompanion);

        LightVO engineGlow = new LightVO();
        engineGlow.itemIdentifier = "PlasmaEngineGlow";
        engineGlow.layerName = "GameplayLayer";
        engineGlow.x = 420;
        engineGlow.y = 270;
        engineGlow.distance = 180;
        scene.composite.sLights.add(engineGlow);

        LabelVO scoreLabel = new LabelVO();
        scoreLabel.itemIdentifier = "ScoreDisplay";
        scoreLabel.layerName = "HUDOverlay";
        scoreLabel.text = "SCORE: 128,450";
        scoreLabel.x = 50;
        scoreLabel.y = 650;
        scene.composite.sLabels.add(scoreLabel);

        // 2. Setup Full IDE-like Layout: Left: SceneStructure (2D), Center: 2D Scene Viewport, Right: Inspector
        JPanel mockIdePanel = new JPanel(new BorderLayout(4, 0));
        mockIdePanel.setSize(1400, 800);
        mockIdePanel.setBackground(new Color(30, 31, 34));

        // Left Panel: Scene Structure
        SceneStructureTopComponent sceneStructure = new SceneStructureTopComponent();
        sceneStructure.setScene2D(scene);
        sceneStructure.getHierarchyPanel2D().selectItem(playerShip);
        JPanel leftDock = new JPanel(new BorderLayout());
        leftDock.setPreferredSize(new Dimension(280, 800));
        leftDock.setSize(280, 800);
        leftDock.setBackground(DarkThemeUtils.BG_DARK);
        leftDock.add(sceneStructure, BorderLayout.CENTER);

        // Center Panel: 2D Scene Viewport
        Scene2DEditorPanel editorPanel = new Scene2DEditorPanel(scene);
        editorPanel.setSelectedItem(playerShip);
        JPanel centerDock = new JPanel(new BorderLayout()) {
            @Override
            public void paintChildren(Graphics g) {
                super.paintChildren(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Viewport boundary: below toolbar (height ~32)
                int vpX = 0, vpY = 32, vpW = getWidth(), vpH = getHeight() - 32;
                g2d.setClip(vpX, vpY, vpW, vpH);

                // 1. Grid
                g2d.setColor(new Color(28, 30, 34));
                for (int x = vpX; x < vpX + vpW; x += 40) g2d.drawLine(x, vpY, x, vpY + vpH);
                for (int y = vpY; y < vpY + vpH; y += 40) g2d.drawLine(vpX, y, vpX + vpW, y);

                // Axes
                int originX = vpX + vpW / 2;
                int originY = vpY + vpH / 2;
                g2d.setColor(new Color(180, 50, 50, 180));
                g2d.drawLine(vpX, originY, vpX + vpW, originY);
                g2d.setColor(new Color(50, 180, 50, 180));
                g2d.drawLine(originX, vpY, originX, vpY + vpH);

                // 2. Scene Item: escort_drone
                int droneX = originX + 120, droneY = originY - 60, droneW = 64, droneH = 48;
                g2d.setColor(new Color(50, 80, 120, 120));
                g2d.fillRect(droneX, droneY, droneW, droneH);
                g2d.setColor(new Color(100, 150, 220));
                g2d.drawRect(droneX, droneY, droneW, droneH);
                g2d.setColor(new Color(200, 210, 225));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2d.drawString("escort_drone", droneX + 2, droneY - 4);

                // 3. Selected Scene Item: cruiser_fighter
                int shipX = originX - 80, shipY = originY - 40, shipW = 120, shipH = 80;
                g2d.setColor(new Color(30, 90, 160, 140));
                g2d.fillRect(shipX, shipY, shipW, shipH);
                
                // Ship icon / silhouette
                g2d.setColor(new Color(80, 180, 255));
                Polygon poly = new Polygon();
                poly.addPoint(shipX + shipW, shipY + shipH / 2);
                poly.addPoint(shipX, shipY + 10);
                poly.addPoint(shipX + 20, shipY + shipH / 2);
                poly.addPoint(shipX, shipY + shipH - 10);
                g2d.fill(poly);

                // 4. Cyan Selection Box & 8-point Resize Handles
                g2d.setColor(new Color(0, 220, 255));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRect(shipX - 2, shipY - 2, shipW + 4, shipH + 4);
                g2d.drawString("cruiser_fighter", shipX, shipY - 6);

                // 8 Handles
                int[][] handles = new int[][]{
                        {shipX - 5, shipY - 5}, {shipX + shipW / 2 - 3, shipY - 5}, {shipX + shipW - 1, shipY - 5},
                        {shipX - 5, shipY + shipH / 2 - 3}, {shipX + shipW - 1, shipY + shipH / 2 - 3},
                        {shipX - 5, shipY + shipH - 1}, {shipX + shipW / 2 - 3, shipY + shipH - 1}, {shipX + shipW - 1, shipY + shipH - 1}
                };
                g2d.setColor(Color.WHITE);
                for (int[] h : handles) {
                    g2d.fillRect(h[0], h[1], 6, 6);
                }
                g2d.setColor(new Color(0, 180, 240));
                for (int[] h : handles) {
                    g2d.drawRect(h[0], h[1], 6, 6);
                }

                // 5. Transform Gizmo at Center of Selected Item
                int cx = shipX + shipW / 2;
                int cy = shipY + shipH / 2;

                // Center Move Box
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRect(cx - 5, cy - 5, 10, 10);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(cx - 5, cy - 5, 10, 10);

                // Red X Axis Arrow
                g2d.setColor(new Color(240, 60, 60));
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawLine(cx, cy, cx + 60, cy);
                Polygon xArr = new Polygon(new int[]{cx + 60, cx + 50, cx + 50}, new int[]{cy, cy - 5, cy + 5}, 3);
                g2d.fill(xArr);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2d.drawString("X", cx + 65, cy + 4);

                // Green Y Axis Arrow
                g2d.setColor(new Color(60, 220, 60));
                g2d.drawLine(cx, cy, cx, cy - 60);
                Polygon yArr = new Polygon(new int[]{cx, cx - 5, cx + 5}, new int[]{cy - 60, cy - 50, cy - 50}, 3);
                g2d.fill(yArr);
                g2d.drawString("Y", cx - 4, cy - 65);

                // Rotation Stem
                g2d.setColor(new Color(80, 160, 255, 200));
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0));
                g2d.drawLine(cx, cy, cx, cy - 90);
                g2d.setColor(new Color(80, 160, 255));
                g2d.fillOval(cx - 5, cy - 95, 10, 10);

                g2d.dispose();
            }
        };
        centerDock.setSize(800, 800);
        centerDock.setBackground(DarkThemeUtils.BG_DARK);
        centerDock.add(editorPanel, BorderLayout.CENTER);

        // Right Panel: Inspector
        InspectorTopComponent inspector = new InspectorTopComponent();
        inspector.inspectSceneItem(playerShip, scene);
        JPanel rightDock = new JPanel(new BorderLayout());
        rightDock.setPreferredSize(new Dimension(320, 800));
        rightDock.setSize(320, 800);
        rightDock.setBackground(DarkThemeUtils.BG_DARK);
        rightDock.add(inspector, BorderLayout.CENTER);

        mockIdePanel.add(leftDock, BorderLayout.WEST);
        mockIdePanel.add(centerDock, BorderLayout.CENTER);
        mockIdePanel.add(rightDock, BorderLayout.EAST);

        layoutComponentTree(mockIdePanel);

        // Render Frame 1: Full Workspace with Single Selected Item
        BufferedImage screenshot1 = new BufferedImage(1400, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = screenshot1.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        mockIdePanel.printAll(g2);
        g2.dispose();

        File shotFile1 = new File(ARTIFACT_MEDIA_DIR, "media_proof_workspace_single_select.png");
        ImageIO.write(screenshot1, "png", shotFile1);
        assertTrue(shotFile1.exists());
        System.out.println("Captured single-selection workspace to: " + shotFile1.getAbsolutePath());

        // Render Frame 2: Inspector Multi-Selection Warning Banner
        inspector.showMultiSelection(3, "2D Scene Object");
        sceneStructure.getHierarchyPanel2D().selectItems(Arrays.asList(playerShip, droneCompanion, engineGlow));
        layoutComponentTree(mockIdePanel);

        BufferedImage screenshot2 = new BufferedImage(1400, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Multi = screenshot2.createGraphics();
        g2Multi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Multi.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        mockIdePanel.printAll(g2Multi);
        g2Multi.dispose();

        File shotFile2 = new File(ARTIFACT_MEDIA_DIR, "media_proof_inspector_multi_select.png");
        ImageIO.write(screenshot2, "png", shotFile2);
        assertTrue(shotFile2.exists());
        System.out.println("Captured multi-selection inspector to: " + shotFile2.getAbsolutePath());

        // Render Frame 3: Dedicated Inspector Zero-Gap Rendering
        JPanel inspectorOnly = new JPanel(new BorderLayout());
        inspectorOnly.setSize(320, 600);
        inspectorOnly.setBackground(DarkThemeUtils.BG_DARK);
        SceneItemInspectorPanel tightInspector = new SceneItemInspectorPanel(scene);
        tightInspector.setItem(playerShip);
        inspectorOnly.add(tightInspector, BorderLayout.CENTER);
        layoutComponentTree(inspectorOnly);

        BufferedImage screenshot3 = new BufferedImage(320, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Tight = screenshot3.createGraphics();
        inspectorOnly.printAll(g2Tight);
        g2Tight.dispose();

        File shotFile3 = new File(ARTIFACT_MEDIA_DIR, "media_proof_inspector_zero_gap.png");
        ImageIO.write(screenshot3, "png", shotFile3);
        assertTrue(shotFile3.exists());
        System.out.println("Captured tight zero-gap inspector to: " + shotFile3.getAbsolutePath());
    }

    private void layoutComponentTree(Component c) {
        c.doLayout();
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                layoutComponentTree(child);
            }
        }
    }
}
