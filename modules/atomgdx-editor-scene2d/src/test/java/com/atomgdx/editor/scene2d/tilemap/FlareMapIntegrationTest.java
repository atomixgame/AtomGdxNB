package com.atomgdx.editor.scene2d.tilemap;

import com.atomgdx.editor.scene2d.tilemap.data.TileLayer;
import com.atomgdx.editor.scene2d.tilemap.data.TilemapDocument;
import com.atomgdx.editor.scene2d.tilemap.io.TmxParser;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FlareMapIntegrationTest {

    @Test
    public void testParseRealFlareFrontierOutpostMap() throws Exception {
        File mapFile = new File("Workspace/NeonCosmos/assets/tilemaps/alpha_demo/frontier_outpost.tmx");
        if (!mapFile.exists()) {
            mapFile = new File("../Workspace/NeonCosmos/assets/tilemaps/alpha_demo/frontier_outpost.tmx");
        }
        assertTrue("Frontier outpost map must exist", mapFile.exists());

        TilemapDocument doc = TmxParser.parseTmx(mapFile);
        assertNotNull(doc);
        assertEquals(TilemapGridMode.ISOMETRIC_DIAMOND, doc.getOrientation());
        assertEquals(64, doc.getWidth());
        assertEquals(64, doc.getHeight());
        assertEquals(64, doc.getTileWidth());
        assertEquals(32, doc.getTileHeight());

        // Verify Flare Map Properties
        assertEquals("Frontier Outpost", doc.getProperties().getString("title", ""));
        assertEquals("46,57", doc.getProperties().getString("hero_pos", ""));
        assertEquals("music/unrest_theme.ogg", doc.getProperties().getString("music", ""));

        // Verify Multiple Tilesets Loaded
        assertFalse("Should have multiple tilesets", doc.getTileSets().isEmpty());
        assertEquals(7, doc.getTileSets().size());

        // Verify Tall structures & Trees have correct heights and offsets
        assertEquals(128, doc.getTileSets().get(1).tileHeight); // grassland (64x128)
        assertEquals(32, doc.getTileSets().get(2).tileOffsetY); // water (offset y=32)
        assertEquals(256, doc.getTileSets().get(3).tileHeight); // tall structures (64x256)
        assertEquals(-32, doc.getTileSets().get(4).tileOffsetX); // trees (offset x=-32)

        // Verify Layers
        assertFalse("Should have layers", doc.getLayers().isEmpty());
        TileLayer bg = (TileLayer) doc.getLayers().get(0);
        assertEquals("background", bg.getName());
        assertEquals(64, bg.getWidth());
        assertEquals(64, bg.getHeight());
    }
}
