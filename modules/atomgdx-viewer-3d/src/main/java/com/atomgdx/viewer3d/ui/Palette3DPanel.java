package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.Prefab3DVO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * 3D Asset Palette displaying 3D Primitives, Prefabs (.prefab.json), and Materials (.mat.json).
 * Allows drag-and-drop or single-click instantiation into the active 3D Scene.
 */
public class Palette3DPanel extends JPanel {

    private Consumer<Object> itemSelectedListener;

    public Palette3DPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(DarkThemeUtils.BG_HEADER);
        tabs.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 11));

        tabs.addTab("Primitives", createPrimitivesTab());
        tabs.addTab("Prefabs", createPrefabsTab());
        tabs.addTab("Materials", createMaterialsTab());

        add(tabs, BorderLayout.CENTER);
    }

    public void setItemSelectedListener(Consumer<Object> listener) {
        this.itemSelectedListener = listener;
    }

    private JScrollPane createPrimitivesTab() {
        JPanel panel = createListPanel();

        panel.add(createPaletteCard("Cube / Box", "box.png", "Standard 3D Cube Mesh (1x1x1m)", () -> {
            notifySelected(new Node3DVO("Cube_Mesh", Node3DVO.MeshShape.BOX, 0, 0.5f, 0));
        }));
        panel.add(createPaletteCard("Sphere", "world.png", "Smooth UV Sphere Mesh", () -> {
            notifySelected(new Node3DVO("Sphere_Mesh", Node3DVO.MeshShape.SPHERE, 0, 0.5f, 0));
        }));
        panel.add(createPaletteCard("Cylinder", "cog.png", "Radial Cylinder Mesh", () -> {
            notifySelected(new Node3DVO("Cylinder_Mesh", Node3DVO.MeshShape.CYLINDER, 0, 0.5f, 0));
        }));
        panel.add(createPaletteCard("Cone", "bullet_red.png", "Tapered Cone Mesh", () -> {
            notifySelected(new Node3DVO("Cone_Mesh", Node3DVO.MeshShape.CONE, 0, 0.5f, 0));
        }));
        panel.add(createPaletteCard("Plane / Floor", "layout.png", "Flat Ground Quad Plane (10x10m)", () -> {
            Node3DVO p = new Node3DVO("Ground_Plane", Node3DVO.MeshShape.PLANE, 0, 0, 0);
            p.scaleX = 10f;
            p.scaleZ = 10f;
            notifySelected(p);
        }));
        panel.add(createPaletteCard("Capsule", "pill.png", "Character Controller Capsule Collider", () -> {
            notifySelected(new Node3DVO("Capsule_Mesh", Node3DVO.MeshShape.CAPSULE, 0, 1.0f, 0));
        }));

        panel.add(Box.createVerticalGlue());
        return wrapScroll(panel);
    }

    private JScrollPane createPrefabsTab() {
        JPanel panel = createListPanel();

        panel.add(createPaletteCard("Spacecraft Fighter", "car.png", "Player Ship with Thruster Light & RigidBody", () -> {
            notifySelected(Prefab3DVO.createSpacecraftFighter().rootNode);
        }));
        panel.add(createPaletteCard("Asteroid Rock", "world.png", "Large Ore Asteroid with Static Physics", () -> {
            notifySelected(Prefab3DVO.createAsteroidRock().rootNode);
        }));
        panel.add(createPaletteCard("SciFi Turret", "shield.png", "Compound Base + Cannon Defense Turret", () -> {
            notifySelected(Prefab3DVO.createSciFiTurret().rootNode);
        }));
        panel.add(createPaletteCard("Energy Shield", "lightning.png", "Translucent Cyan Forcefield Bubble", () -> {
            notifySelected(Prefab3DVO.createEnergyShield().rootNode);
        }));

        panel.add(Box.createVerticalGlue());
        return wrapScroll(panel);
    }

    private JScrollPane createMaterialsTab() {
        JPanel panel = createListPanel();

        panel.add(createPaletteCard("Metallic Gold", "color_wheel.png", "PBR Specular Gold (Metallic 0.9, Roughness 0.2)", () -> {
            notifySelected(Material3DVO.createPreset("metallic gold"));
        }));
        panel.add(createPaletteCard("Brushed Steel", "color_wheel.png", "PBR Brushed Alloy (Metallic 0.85, Roughness 0.35)", () -> {
            notifySelected(Material3DVO.createPreset("brushed steel"));
        }));
        panel.add(createPaletteCard("Neon Glow Cyan", "lightning.png", "Emissive SciFi Neon (Glow Cyan)", () -> {
            notifySelected(Material3DVO.createPreset("neon glow cyan"));
        }));
        panel.add(createPaletteCard("SciFi Hull Paint", "color_wheel.png", "Matte Armor Plate (Metallic 0.3, Roughness 0.6)", () -> {
            notifySelected(Material3DVO.createPreset("scifi hull paint"));
        }));
        panel.add(createPaletteCard("Transparent Glass", "application_view_tile.png", "Refractive Glass (Opacity 35%)", () -> {
            notifySelected(Material3DVO.createPreset("transparent glass"));
        }));

        panel.add(Box.createVerticalGlue());
        return wrapScroll(panel);
    }

    private JPanel createListPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(DarkThemeUtils.BG_DARK);
        p.setBorder(new EmptyBorder(4, 4, 4, 4));
        return p;
    }

    private JScrollPane wrapScroll(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.getViewport().setBackground(DarkThemeUtils.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel createPaletteCard(String title, String iconName, String subtitle, Runnable onAdd) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(DarkThemeUtils.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DarkThemeUtils.BORDER, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        card.setMaximumSize(new Dimension(500, 42));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(DarkThemeUtils.getFatcowIcon(iconName));
        card.add(iconLbl, BorderLayout.WEST);

        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 0));
        textP.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLbl.setForeground(DarkThemeUtils.TEXT_MUTED);

        textP.add(titleLbl);
        textP.add(subLbl);
        card.add(textP, BorderLayout.CENTER);

        JButton addBtn = new JButton("+");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.setBackground(DarkThemeUtils.BG_HEADER);
        addBtn.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(24, 24));
        addBtn.addActionListener(e -> onAdd.run());
        card.add(addBtn, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onAdd.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(DarkThemeUtils.BG_HEADER_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(DarkThemeUtils.BG_PANEL);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(500, 46));
        wrapper.add(card, BorderLayout.CENTER);
        wrapper.add(Box.createVerticalStrut(3), BorderLayout.SOUTH);

        return wrapper;
    }

    private void notifySelected(Object item) {
        if (itemSelectedListener != null) {
            itemSelectedListener.accept(item);
        }
    }
}
