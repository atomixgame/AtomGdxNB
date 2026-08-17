package com.atomgdx.viewer3d.data;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads palette items from palette_items.json configuration file.
 */
public class PaletteLoader {

    public static List<PaletteItemVO> loadPaletteItems() {
        List<PaletteItemVO> items = new ArrayList<>();
        try {
            InputStream in = PaletteLoader.class.getResourceAsStream("/com/atomgdx/viewer3d/palette_items.json");
            if (in == null) {
                in = PaletteLoader.class.getResourceAsStream("palette_items.json");
            }

            if (in != null) {
                try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    JsonValue root = new JsonReader().parse(reader);
                    JsonValue itemsArr = root.get("items");
                    if (itemsArr != null) {
                        for (JsonValue entry = itemsArr.child; entry != null; entry = entry.next) {
                            PaletteItemVO item = new PaletteItemVO();
                            item.id = entry.getString("id", "");
                            item.name = entry.getString("name", "Item");
                            item.category = entry.getString("category", "General");
                            item.type = entry.getString("type", "3D");
                            item.shape = entry.getString("shape", null);
                            item.icon = entry.getString("icon", "brick.png");
                            item.subtitle = entry.getString("subtitle", "");

                            JsonValue tagsArr = entry.get("tags");
                            if (tagsArr != null) {
                                for (JsonValue tag = tagsArr.child; tag != null; tag = tag.next) {
                                    item.tags.add(tag.asString());
                                }
                            }
                            items.add(item);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error parsing palette_items.json: " + ex.getMessage());
        }

        if (items.isEmpty()) {
            items = createDefaultFallbackItems();
        }

        return items;
    }

    public static List<PaletteItemVO> createDefaultFallbackItems() {
        List<PaletteItemVO> fallback = new ArrayList<>();
        fallback.add(new PaletteItemVO("3d_cube", "Cube / Box", "3D Primitives", "3D", "box.png", "Standard 3D Cube Mesh"));
        fallback.add(new PaletteItemVO("3d_sphere", "UV Sphere", "3D Primitives", "3D", "world.png", "Smooth UV Sphere"));
        fallback.add(new PaletteItemVO("3d_cylinder", "Radial Cylinder", "3D Primitives", "3D", "cog.png", "32-segment Cylinder"));
        fallback.add(new PaletteItemVO("3d_cone", "Tapered Cone", "3D Primitives", "3D", "bullet_red.png", "Sharp Apex Cone"));
        fallback.add(new PaletteItemVO("3d_plane", "Plane / Floor", "3D Primitives", "3D", "layout.png", "Ground Plane (10x10m)"));
        fallback.add(new PaletteItemVO("3d_capsule", "Capsule 3D", "3D Primitives", "3D", "pill.png", "Capsule Collider"));
        fallback.add(new PaletteItemVO("3d_fighter", "Spacecraft Fighter", "3D Prefabs", "3D", "car.png", "Player Ship with Thruster Light"));
        fallback.add(new PaletteItemVO("3d_asteroid", "Large Asteroid Rock", "3D Prefabs", "3D", "world.png", "Ore Asteroid with Static Physics"));
        fallback.add(new PaletteItemVO("mat_gold", "Metallic Gold", "Materials", "MATERIAL", "color_wheel.png", "PBR Gold"));
        fallback.add(new PaletteItemVO("mat_steel", "Brushed Steel", "Materials", "MATERIAL", "color_wheel.png", "PBR Brushed Alloy"));
        fallback.add(new PaletteItemVO("mat_neon", "Neon Glow Cyan", "Materials", "MATERIAL", "lightning.png", "Emissive Neon Cyan"));
        return fallback;
    }
}
