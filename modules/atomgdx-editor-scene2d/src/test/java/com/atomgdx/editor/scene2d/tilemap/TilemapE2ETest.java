package com.atomgdx.editor.scene2d.tilemap;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.tilemap.io.TilemapJsonSerializer;
import com.atomgdx.editor.scene2d.tilemap.io.TmxSerializer;
import com.atomgdx.editor.scene2d.tilemap.physics.TilemapCollisionGenerator;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.Rectangle;
import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class TilemapE2ETest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testFullTilemapE2EWorkflow() throws Exception {
        // 1. Create Tilemap Document
        TilemapDocument doc = new TilemapDocument("E2E_Dungeon", TilemapGridMode.ORTHOGONAL, 60, 40, 32, 32);
        doc.getProperties().putString("author", "AtomGDX Team");
        doc.getProperties().putBoolean("isUnderground", true);

        // 2. Add Background Image Layer with Parallax
        ImageLayer bg = new ImageLayer("Starfield_BG", "textures/backgrounds/nebula_stars.png");
        bg.setParallaxX(0.2f);
        bg.setParallaxY(0.2f);
        bg.setRepeatX(true);
        doc.addLayer(bg);

        // 3. Add Foreground Collision Tile Layer
        TileLayer collisionLayer = new TileLayer("Collision_Geometry", 60, 40);
        collisionLayer.fillRect(10, 10, 8, 4, 1);
        collisionLayer.fillRect(10, 14, 2, 6, 1);
        doc.addLayer(collisionLayer);

        // 4. Verify Collision Generator Merging (CompositeCollider2D)
        List<Rectangle> colliders = TilemapCollisionGenerator.generateMergedBoxColliders(collisionLayer, 32, 32);
        assertFalse("Colliders list should not be empty", colliders.isEmpty());
        assertTrue("Colliders should be merged", colliders.size() <= 2);

        // 5. Add Object Group Layer
        ObjectGroupLayer objects = new ObjectGroupLayer("Entity_Spawns");
        MapObjectVO bossSpawn = new MapObjectVO("BossDragon", "BossEnemy", 400, 300, 64, 64);
        bossSpawn.properties.putInt("health", 1500);
        bossSpawn.properties.putString("dialogue", "Who dares enter?");
        objects.addObject(bossSpawn);
        doc.addLayer(objects);

        // 6. Test TMX Serializer Export to Disk
        File tmxFile = tempFolder.newFile("E2E_Dungeon.tmx");
        TmxSerializer.saveTmx(doc, tmxFile);
        assertTrue(tmxFile.exists());
        assertTrue(tmxFile.length() > 200);

        String tmxContent = new String(java.nio.file.Files.readAllBytes(tmxFile.toPath()));
        assertTrue(tmxContent.contains("<map"));
        assertTrue(tmxContent.contains("E2E_Dungeon"));
        assertTrue(tmxContent.contains("Starfield_BG"));
        assertTrue(tmxContent.contains("Collision_Geometry"));
        assertTrue(tmxContent.contains("BossDragon"));
        assertTrue(tmxContent.contains("health"));

        // 7. Test JSON Serializer Save & Reload
        File jsonFile = tempFolder.newFile("E2E_Dungeon.tilemap.json");
        TilemapJsonSerializer.saveToFile(doc, jsonFile);
        assertTrue(jsonFile.exists());

        TilemapDocument loadedDoc = TilemapJsonSerializer.loadFromFile(jsonFile);
        assertNotNull(loadedDoc);
        assertEquals("E2E_Dungeon", loadedDoc.getName());
        assertEquals(doc.getLayers().size(), loadedDoc.getLayers().size());
        assertEquals(60, loadedDoc.getWidth());
        assertEquals(40, loadedDoc.getHeight());
        assertTrue(loadedDoc.getProperties().has("author"));
        assertEquals("AtomGDX Team", loadedDoc.getProperties().get("author").value);
    }
}
