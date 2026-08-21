package com.atomgdx.editor.scene2d.tilemap;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.tilemap.io.TmxSerializer;
import com.atomgdx.editor.scene2d.tilemap.physics.TilemapCollisionGenerator;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.*;

public class TilemapDocumentTest {

    @Test
    public void testTilemapCreationAndLayers() {
        TilemapDocument doc = new TilemapDocument("TestLevel", TilemapGridMode.ORTHOGONAL, 30, 20, 32, 32);
        assertEquals("TestLevel", doc.getName());
        assertEquals(30, doc.getWidth());
        assertEquals(20, doc.getHeight());
        assertFalse(doc.getLayers().isEmpty());

        TileLayer layer = (TileLayer) doc.getLayers().get(0);
        layer.setTile(5, 5, 42);
        assertEquals(42, layer.getTile(5, 5));
    }

    @Test
    public void testTileLayerFloodFill() {
        TileLayer layer = new TileLayer("Ground", 10, 10);
        layer.clear();
        assertEquals(0, layer.getTile(3, 3));

        // Flood fill empty area with tile GID 7
        layer.floodFill(3, 3, 7);
        assertEquals(7, layer.getTile(0, 0));
        assertEquals(7, layer.getTile(9, 9));
        assertEquals(7, layer.getTile(5, 5));
    }

    @Test
    public void testAutoTileRuleBitmask() {
        TileLayer layer = new TileLayer("Terrain", 5, 5);
        layer.clear();

        // Create a 3x3 block of tiles from (1,1) to (3,3)
        layer.fillRect(1, 1, 3, 3, 1);

        // Center cell (2,2) should have all 8 neighbors matching (255)
        int centerMask = AutoTileRule.calculateBitmask(layer, 2, 2);
        assertEquals(AutoTileRule.ALL_EIGHT, centerMask);

        AutoTileRule rule = new AutoTileRule("Grass", 10);
        int centerGid = rule.resolveTile(centerMask);
        assertEquals(10, centerGid);
    }

    @Test
    public void testCollisionGeneratorCompositeBoxes() {
        TileLayer layer = new TileLayer("Collision", 10, 10);
        layer.clear();

        // Create 2x2 solid block of tiles from (2,2) to (3,3)
        layer.fillRect(2, 2, 2, 2, 1);

        List<Rectangle> colliders = TilemapCollisionGenerator.generateMergedBoxColliders(layer, 32, 32);
        assertNotNull(colliders);
        assertEquals(1, colliders.size());

        Rectangle r = colliders.get(0);
        assertEquals(2 * 32, r.x);
        assertEquals(2 * 32, r.y);
        assertEquals(2 * 32, r.width);
        assertEquals(2 * 32, r.height);
    }

    @Test
    public void testTmxSerializerOutput() {
        TilemapDocument doc = new TilemapDocument("Dungeon_01", TilemapGridMode.ORTHOGONAL, 10, 10, 32, 32);
        String xml = TmxSerializer.toTmxXml(doc);

        assertNotNull(xml);
        assertTrue(xml.contains("<map"));
        assertTrue(xml.contains("orientation=\"orthogonal\""));
        assertTrue(xml.contains("<tileset"));
        assertTrue(xml.contains("<layer"));
        assertTrue(xml.contains("<data encoding=\"csv\">"));
        assertTrue(xml.contains("</map>"));
    }
}
