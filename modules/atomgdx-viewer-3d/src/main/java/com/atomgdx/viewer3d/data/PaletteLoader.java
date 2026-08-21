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
                in = PaletteLoader.class.getClassLoader().getResourceAsStream("com/atomgdx/viewer3d/palette_items.json");
            }
            if (in == null && Thread.currentThread().getContextClassLoader() != null) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/atomgdx/viewer3d/palette_items.json");
            }
            if (in == null) {
                in = PaletteLoader.class.getResourceAsStream("palette_items.json");
            }
            if (in == null) {
                String[] candidatePaths = {
                        "modules/atomgdx-viewer-3d/src/main/resources/com/atomgdx/viewer3d/palette_items.json",
                        "../modules/atomgdx-viewer-3d/src/main/resources/com/atomgdx/viewer3d/palette_items.json",
                        "src/main/resources/com/atomgdx/viewer3d/palette_items.json",
                        "g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/AtomGdxNB/modules/atomgdx-viewer-3d/src/main/resources/com/atomgdx/viewer3d/palette_items.json"
                };
                for (String cp : candidatePaths) {
                    java.io.File cf = new java.io.File(cp);
                    if (cf.exists()) {
                        try {
                            in = new java.io.FileInputStream(cf);
                            break;
                        } catch (Exception ignored) {}
                    }
                }
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
        
        // 1. 2D Shapes (10 items)
        fallback.add(new PaletteItemVO("2d_9patch_box", "9-Patch Button Box", "2D Shapes", "2D", "application_view_tile.png", "Scalable UI 9-Patch container"));
        fallback.add(new PaletteItemVO("2d_rect_shape", "Filled Rectangle", "2D Shapes", "2D", "shape_square.png", "Solid 2D colored quad (100x100px)"));
        fallback.add(new PaletteItemVO("2d_circle_shape", "Circle Shape", "2D Shapes", "2D", "shape_circle.png", "2D radial polygon or circle collider"));
        fallback.add(new PaletteItemVO("2d_capsule_shape", "Capsule 2D", "2D Shapes", "2D", "pill.png", "2D Character collider shape"));
        fallback.add(new PaletteItemVO("2d_polygon_collider", "Polygon Collider", "2D Shapes", "2D", "shape_polygon.png", "Convex / Concave Box2D polygon outline"));
        fallback.add(new PaletteItemVO("2d_star_shape", "Star Shape", "2D Shapes", "2D", "star.png", "5-point vector star graphic"));
        fallback.add(new PaletteItemVO("2d_triangle_shape", "Triangle Polygon", "2D Shapes", "2D", "shape_triangle.png", "3-point equilateral triangle"));
        fallback.add(new PaletteItemVO("2d_diamond_shape", "Diamond Quad", "2D Shapes", "2D", "shape_square.png", "Isometric diamond tile shape"));
        fallback.add(new PaletteItemVO("2d_ray_line", "Laser Ray Line", "2D Shapes", "2D", "lightning.png", "Segment raycast line indicator"));
        fallback.add(new PaletteItemVO("2d_tilemap_grid", "Tilemap Grid 2D", "2D Shapes", "2D", "application_view_tile.png", "Orthogonal 2D tilemap chunk (32x32px)"));

        // 2. 2D Prefabs (10 items)
        fallback.add(new PaletteItemVO("2d_prefab_player", "Player Starfighter 2D", "2D Prefabs", "2D", "car.png", "Player ship with dynamic Box2D physics"));
        fallback.add(new PaletteItemVO("2d_prefab_enemy_scout", "Enemy Scout Drone 2D", "2D Prefabs", "2D", "bug.png", "Patrolling AI drone with sensor collider"));
        fallback.add(new PaletteItemVO("2d_prefab_asteroid", "Asteroid Obstacle 2D", "2D Prefabs", "2D", "world.png", "Destructible spinning space rock"));
        fallback.add(new PaletteItemVO("2d_prefab_coin", "Coin Pickup 2D", "2D Prefabs", "2D", "money.png", "Animated gold coin with trigger volume"));
        fallback.add(new PaletteItemVO("2d_prefab_health", "Health Pack 2D", "2D Prefabs", "2D", "heart.png", "Restorative medkit with glow pulse"));
        fallback.add(new PaletteItemVO("2d_prefab_shield", "Shield Powerup 2D", "2D Prefabs", "2D", "shield.png", "Temporary energy barrier sphere"));
        fallback.add(new PaletteItemVO("2d_prefab_portal", "Portal Gateway 2D", "2D Prefabs", "2D", "wand.png", "Swirling teleportation vortex"));
        fallback.add(new PaletteItemVO("2d_prefab_turret", "Laser Turret 2D", "2D Prefabs", "2D", "cross.png", "Auto-targeting fixed defense tower"));
        fallback.add(new PaletteItemVO("2d_prefab_explosion", "Explosion Particle FX", "2D Prefabs", "2D", "fire.png", "Burst emitter with smoke trails"));
        fallback.add(new PaletteItemVO("2d_prefab_waypoint", "Waypoint Marker", "2D Prefabs", "2D", "flag_blue.png", "Navigation path milestone locator"));

        // 3. 3D Primitives (12 items)
        fallback.add(new PaletteItemVO("3d_cube", "Cube / Box", "3D Primitives", "3D", "box.png", "Standard 3D Cube Mesh (1x1x1m)"));
        fallback.add(new PaletteItemVO("3d_sphere", "UV Sphere", "3D Primitives", "3D", "world.png", "Smooth UV Sphere (32 rings, 32 sectors)"));
        fallback.add(new PaletteItemVO("3d_cylinder", "Radial Cylinder", "3D Primitives", "3D", "cog.png", "32-segment Radial Cylinder"));
        fallback.add(new PaletteItemVO("3d_cone", "Tapered Cone", "3D Primitives", "3D", "bullet_red.png", "Sharp Apex Cone (1x2m)"));
        fallback.add(new PaletteItemVO("3d_plane", "Plane / Floor", "3D Primitives", "3D", "layout.png", "Ground Plane (10x10m)"));
        fallback.add(new PaletteItemVO("3d_capsule", "Capsule 3D", "3D Primitives", "3D", "pill.png", "Character Capsule Collider (0.5m radius)"));
        fallback.add(new PaletteItemVO("3d_torus", "Torus Ring", "3D Primitives", "3D", "shape_circle.png", "Donut geometry with tubular cross-section"));
        fallback.add(new PaletteItemVO("3d_geosphere", "GeoSphere", "3D Primitives", "3D", "world.png", "Equilateral triangular geodesic sphere"));
        fallback.add(new PaletteItemVO("3d_hemisphere", "Hemisphere Dome", "3D Primitives", "3D", "weather_clouds.png", "Half-sphere skybox or forcefield dome"));
        fallback.add(new PaletteItemVO("3d_pyramid", "Pyramid 3D", "3D Primitives", "3D", "shape_triangle.png", "4-sided architectural pyramid"));
        fallback.add(new PaletteItemVO("3d_arrow", "Arrow Gizmo 3D", "3D Primitives", "3D", "arrow_right.png", "Directional orientation vector arrow"));
        fallback.add(new PaletteItemVO("3d_bounding_box", "Bounding Box Wire", "3D Primitives", "3D", "shape_square.png", "Axis-aligned wireframe bounding box"));

        // 4. 3D Prefabs (10 items)
        fallback.add(new PaletteItemVO("3d_fighter", "Spacecraft Fighter", "3D Prefabs", "3D", "car.png", "Player Ship with Thruster Light & PBR Hull"));
        fallback.add(new PaletteItemVO("3d_asteroid", "Large Asteroid Rock", "3D Prefabs", "3D", "world.png", "Ore Asteroid with Static Physics Collider"));
        fallback.add(new PaletteItemVO("3d_station", "Space Station Core", "3D Prefabs", "3D", "building.png", "Orbital Command Outpost with Solar Panels"));
        fallback.add(new PaletteItemVO("3d_turret", "Heavy Turret Cannon", "3D Prefabs", "3D", "cross.png", "Twin-barrel automated plasma cannon"));
        fallback.add(new PaletteItemVO("3d_crate", "Cargo Crate Box", "3D Prefabs", "3D", "package.png", "RigidBody Sci-Fi Supply Container"));
        fallback.add(new PaletteItemVO("3d_shield_dome", "Energy Shield Dome", "3D Prefabs", "3D", "shield.png", "Translucent Hexagonal Energy Shield"));
        fallback.add(new PaletteItemVO("3d_light_spot", "Plasma Thruster Light", "3D Prefabs", "3D", "lightning.png", "Point Light with Cyan Emissive Core"));
        fallback.add(new PaletteItemVO("3d_beacon", "Nav Waypoint Beacon", "3D Prefabs", "3D", "flag_blue.png", "Pulsing navigation relay tower"));
        fallback.add(new PaletteItemVO("3d_crystal", "Mineral Crystal Ore", "3D Prefabs", "3D", "ruby.png", "Facet-cut glowing emissive crystal"));
        fallback.add(new PaletteItemVO("3d_gate", "Jump Gate Ring", "3D Prefabs", "3D", "shape_circle.png", "Interstellar jump ring conduit"));

        // 5. Materials (14 items)
        fallback.add(new PaletteItemVO("mat_gold", "Metallic Gold", "Materials", "MATERIAL", "color_wheel.png", "PBR Gold (Metallic 0.95, Roughness 0.15)"));
        fallback.add(new PaletteItemVO("mat_steel", "Brushed Steel", "Materials", "MATERIAL", "color_wheel.png", "PBR Brushed Alloy (Metallic 0.85, Roughness 0.35)"));
        fallback.add(new PaletteItemVO("mat_neon", "Neon Glow Cyan", "Materials", "MATERIAL", "lightning.png", "Emissive Neon Cyan (Intensity 3.5)"));
        fallback.add(new PaletteItemVO("mat_hologram", "Hologram Blue", "Materials", "MATERIAL", "wand.png", "Translucent Scanline Holographic Shader"));
        fallback.add(new PaletteItemVO("mat_carbon", "Dark Carbon Fiber", "Materials", "MATERIAL", "color_wheel.png", "Woven carbon composite texture"));
        fallback.add(new PaletteItemVO("mat_lava", "Molten Lava", "Materials", "MATERIAL", "fire.png", "Animated emissive magma flow surface"));
        fallback.add(new PaletteItemVO("mat_obsidian", "Obsidian Gloss", "Materials", "MATERIAL", "color_wheel.png", "Ultra-smooth reflective dark glass"));
        fallback.add(new PaletteItemVO("mat_glass", "Frosted Glass", "Materials", "MATERIAL", "shape_square.png", "Refractive frosted dielectric glass"));
        fallback.add(new PaletteItemVO("mat_rust", "Rust Iron", "Materials", "MATERIAL", "brick.png", "Weathered oxide metal surface"));
        fallback.add(new PaletteItemVO("mat_plastic", "Matte Plastic Red", "Materials", "MATERIAL", "bullet_red.png", "Dielectric matte synthetic polymer"));
        fallback.add(new PaletteItemVO("mat_emerald", "Emerald Crystal", "Materials", "MATERIAL", "ruby.png", "Subsurface scattering green gemstone"));
        fallback.add(new PaletteItemVO("mat_wireframe", "Wireframe Grid", "Materials", "MATERIAL", "application_view_tile.png", "Unlit digital debug wire overlay"));
        fallback.add(new PaletteItemVO("mat_space_sky", "Space Skybox Starfield", "Materials", "MATERIAL", "weather_clouds.png", "Equirectangular deep cosmic skybox"));
        fallback.add(new PaletteItemVO("mat_checker", "Checkerboard Test", "Materials", "MATERIAL", "application_view_tile.png", "UV alignment calibration grid (2x2m)"));

        return fallback;
    }
}
