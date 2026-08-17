package com.atomgdx.editor.scene2d;

import com.atomgdx.editor.scene2d.data.vo.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility to generate a complete, rich testable HyperLap2D project and scene with graphics and textures.
 */
public class HyperLap2DSampleProjectGenerator {

    public static void generateSampleProject(File baseAssetsDir) throws IOException {
        File texDir = new File(baseAssetsDir, "textures");
        File scenesDir = new File(baseAssetsDir, "scenes");
        if (!texDir.exists()) texDir.mkdirs();
        if (!scenesDir.exists()) scenesDir.mkdirs();

        // 1. Generate Textures
        createPlayerShipTexture(new File(texDir, "player_ship.png"));
        createSpaceStationTexture(new File(texDir, "space_station.png"));
        createAsteroidTexture(new File(texDir, "asteroid.png"));
        createLaserBoltTexture(new File(texDir, "laser_bolt.png"));
        createHealthBarTexture(new File(texDir, "health_bar.png"));
        createNebulaBgTexture(new File(texDir, "nebula_bg.png"));

        // 2. Generate ProjectVO (project.dt)
        ProjectVO project = new ProjectVO();
        project.projectName = "NeonCosmos";
        project.projectVersion = "0.2.0";
        project.originalResolution = new ProjectVO.ResolutionEntryVO("orig", 1920, 1080);
        project.resolutions.add(new ProjectVO.ResolutionEntryVO("mobile", 1280, 720));
        project.resolutions.add(new ProjectVO.ResolutionEntryVO("desktop_fhd", 1920, 1080));
        project.scenes.add("MainScene");
        project.scenes.add("BossStage");

        File projectFile = new File(scenesDir, "hyperlap2d_project.dt");
        HyperLap2DSerializer.saveProjectToFile(project, projectFile);
        System.out.println("Saved Project: " + projectFile.getAbsolutePath());

        // 3. Generate SceneVO (MainScene.dt)
        SceneVO scene = new SceneVO("MainScene");
        scene.ambientColor = new ColorDataVO(0.2f, 0.25f, 0.35f, 1.0f);
        scene.physicsPropertiesLoaded = true;
        scene.lightsPropertiesLoaded = true;
        scene.pixelsPerMU = 80f;

        // Layers
        scene.composite.layers.clear();
        scene.composite.layers.add(new LayerItemVO("Background"));
        scene.composite.layers.add(new LayerItemVO("Gameplay"));
        scene.composite.layers.add(new LayerItemVO("Particles & FX"));
        scene.composite.layers.add(new LayerItemVO("HUD"));

        // Background Nebula
        SimpleImageVO bg = new SimpleImageVO("nebula_bg.png", 0, 0);
        bg.itemName = "BackgroundNebula";
        bg.layerName = "Background";
        bg.width = 1920;
        bg.height = 1080;
        scene.composite.sImages.add(bg);

        // Space Station
        SimpleImageVO station = new SimpleImageVO("space_station.png", 1200, 500);
        station.itemName = "OrbitalStation";
        station.layerName = "Background";
        station.scaleX = 1.3f;
        station.scaleY = 1.3f;
        station.physics = new PhysicsBodyDataVO();
        station.physics.bodyType = 0; // Static
        scene.composite.sImages.add(station);

        // Asteroids
        SimpleImageVO ast1 = new SimpleImageVO("asteroid.png", 350, 450);
        ast1.itemName = "AsteroidAlpha";
        ast1.layerName = "Gameplay";
        ast1.rotation = 28f;
        ast1.physics = new PhysicsBodyDataVO();
        ast1.physics.bodyType = 2; // Dynamic
        ast1.physics.density = 5.0f;
        ast1.physics.restitution = 0.6f;
        scene.composite.sImages.add(ast1);

        SimpleImageVO ast2 = new SimpleImageVO("asteroid.png", 800, 200);
        ast2.itemName = "AsteroidBeta";
        ast2.layerName = "Gameplay";
        ast2.scaleX = 0.7f;
        ast2.scaleY = 0.7f;
        ast2.physics = new PhysicsBodyDataVO();
        ast2.physics.bodyType = 2;
        ast2.physics.density = 4.0f;
        scene.composite.sImages.add(ast2);

        // Player Ship
        SimpleImageVO player = new SimpleImageVO("player_ship.png", 640, 360);
        player.itemName = "Starfighter";
        player.layerName = "Gameplay";
        player.scaleX = 1.0f;
        player.scaleY = 1.0f;
        player.originX = 48f;
        player.originY = 48f;
        player.rotation = 12f;
        player.physics = new PhysicsBodyDataVO();
        player.physics.bodyType = 2; // Dynamic
        player.physics.density = 1.5f;
        player.physics.friction = 0.3f;
        player.physics.bullet = true;
        scene.composite.sImages.add(player);

        // Laser Bolt
        SimpleImageVO laser = new SimpleImageVO("laser_bolt.png", 760, 410);
        laser.itemName = "PlasmaShot";
        laser.layerName = "Gameplay";
        laser.rotation = 12f;
        scene.composite.sImages.add(laser);

        // Point Light on Starfighter
        LightVO shipLight = new LightVO("ThrusterGlow", LightVO.LightType.POINT, 640, 360);
        shipLight.layerName = "Particles & FX";
        shipLight.distance = 280f;
        shipLight.tintColor = new ColorDataVO(0.2f, 0.8f, 1.0f, 0.9f);
        scene.composite.sLights.add(shipLight);

        // HUD Labels & Health Bar
        NinePatchVO hpBar = new NinePatchVO("health_bar.png", 220, 24);
        hpBar.itemName = "PlayerHealthBar";
        hpBar.layerName = "HUD";
        hpBar.x = 40;
        hpBar.y = 1000;
        scene.composite.sNinePatches.add(hpBar);

        LabelVO missionLabel = new LabelVO("MISSION: DEFEND SECTOR 9", 40, 960);
        missionLabel.layerName = "HUD";
        missionLabel.tintColor = new ColorDataVO(0.3f, 0.95f, 1.0f, 1f);
        scene.composite.sLabels.add(missionLabel);

        File sceneFile = new File(scenesDir, "MainScene.dt");
        HyperLap2DSerializer.saveSceneToFile(scene, sceneFile);
        System.out.println("Saved Scene: " + sceneFile.getAbsolutePath());
    }

    private static void createPlayerShipTexture(File out) throws IOException {
        BufferedImage img = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Starfighter fuselage
        Path2D.Float ship = new Path2D.Float();
        ship.moveTo(48, 8);
        ship.lineTo(84, 80);
        ship.lineTo(56, 68);
        ship.lineTo(48, 76);
        ship.lineTo(40, 68);
        ship.lineTo(12, 80);
        ship.closePath();

        g.setColor(new Color(40, 110, 220));
        g.fill(ship);
        g.setColor(new Color(120, 200, 255));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(ship);

        // Cockpit glass
        g.setColor(new Color(0, 240, 255, 220));
        g.fillOval(42, 28, 12, 24);

        // Thruster core
        g.setColor(new Color(255, 140, 0));
        g.fillOval(44, 70, 8, 14);

        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static void createSpaceStationTexture(File out) throws IOException {
        BufferedImage img = new BufferedImage(160, 160, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer Ring
        g.setColor(new Color(70, 85, 110));
        g.setStroke(new BasicStroke(12f));
        g.drawOval(16, 16, 128, 128);

        // Spoke Arms
        g.setStroke(new BasicStroke(6f));
        g.setColor(new Color(110, 130, 160));
        g.drawLine(80, 16, 80, 144);
        g.drawLine(16, 80, 144, 80);

        // Center Core
        g.setColor(new Color(30, 40, 60));
        g.fillOval(54, 54, 52, 52);
        g.setColor(new Color(0, 230, 200));
        g.fillOval(68, 68, 24, 24);

        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static void createAsteroidTexture(File out) throws IOException {
        BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Path2D.Float rock = new Path2D.Float();
        rock.moveTo(40, 8);
        rock.lineTo(68, 18);
        rock.lineTo(76, 50);
        rock.lineTo(58, 74);
        rock.lineTo(24, 70);
        rock.lineTo(8, 44);
        rock.lineTo(16, 18);
        rock.closePath();

        g.setColor(new Color(90, 80, 75));
        g.fill(rock);
        g.setColor(new Color(140, 130, 120));
        g.setStroke(new BasicStroke(2f));
        g.draw(rock);

        // Craters
        g.setColor(new Color(60, 50, 45));
        g.fillOval(26, 28, 14, 12);
        g.fillOval(46, 44, 18, 16);

        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static void createLaserBoltTexture(File out) throws IOException {
        BufferedImage img = new BufferedImage(48, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0, 255, 240, 180));
        g.fillRoundRect(2, 2, 44, 12, 6, 6);
        g.setColor(Color.WHITE);
        g.fillRoundRect(8, 5, 32, 6, 4, 4);

        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static void createHealthBarTexture(File out) throws IOException {
        BufferedImage img = new BufferedImage(120, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Frame
        g.setColor(new Color(30, 32, 38));
        g.fillRoundRect(0, 0, 120, 20, 6, 6);
        // Fill
        g.setColor(new Color(40, 210, 90));
        g.fillRoundRect(3, 3, 100, 14, 4, 4);
        // Border
        g.setColor(new Color(80, 90, 110));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(0, 0, 119, 19, 6, 6);

        g.dispose();
        ImageIO.write(img, "png", out);
    }

    private static void createNebulaBgTexture(File out) throws IOException {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Dark deep space gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(15, 16, 24), 256, 256, new Color(24, 20, 42));
        g.setPaint(gp);
        g.fillRect(0, 0, 256, 256);

        // Stars
        g.setColor(Color.WHITE);
        java.util.Random rnd = new java.util.Random(42);
        for (int i = 0; i < 80; i++) {
            int x = rnd.nextInt(256);
            int y = rnd.nextInt(256);
            int size = rnd.nextInt(3) + 1;
            g.fillRect(x, y, size, size);
        }

        g.dispose();
        ImageIO.write(img, "png", out);
    }
}
