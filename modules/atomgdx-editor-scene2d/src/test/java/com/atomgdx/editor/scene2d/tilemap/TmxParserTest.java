package com.atomgdx.editor.scene2d.tilemap;

import com.atomgdx.editor.scene2d.tilemap.data.TileLayer;
import com.atomgdx.editor.scene2d.tilemap.data.TileSetVO;
import com.atomgdx.editor.scene2d.tilemap.data.TilemapDocument;
import com.atomgdx.editor.scene2d.tilemap.io.TmxParser;
import com.atomgdx.editor.scene2d.tilemap.io.TmxSerializer;
import com.atomgdx.editor.scene2d.tilemap.io.TsxParser;
import com.atomgdx.editor.scene2d.tilemap.io.TsxSerializer;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class TmxParserTest {

    @Test
    public void testParseSampleIsometricTmx() throws Exception {
        String tmxXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<map version=\"1.10\" orientation=\"isometric\" renderorder=\"right-down\" width=\"10\" height=\"10\" tilewidth=\"64\" tileheight=\"32\">\n" +
                "  <properties>\n" +
                "    <property name=\"title\" value=\"Frontier Outpost\"/>\n" +
                "    <property name=\"dangerLevel\" type=\"int\" value=\"5\"/>\n" +
                "  </properties>\n" +
                "  <tileset firstgid=\"1\" name=\"grassland\" tilewidth=\"64\" tileheight=\"128\" tilecount=\"64\" columns=\"8\">\n" +
                "    <tileoffset x=\"0\" y=\"32\"/>\n" +
                "    <image source=\"grassland.png\" width=\"512\" height=\"1024\"/>\n" +
                "  </tileset>\n" +
                "  <layer id=\"1\" name=\"Ground\" width=\"10\" height=\"10\">\n" +
                "    <data encoding=\"csv\">\n" +
                "1,1,1,0,0,0,0,0,0,0,\n" +
                "1,1,1,0,0,0,0,0,0,0,\n" +
                "0,0,0,2,2,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0,\n" +
                "0,0,0,0,0,0,0,0,0,0\n" +
                "    </data>\n" +
                "  </layer>\n" +
                "</map>";

        TilemapDocument doc = TmxParser.parseTmx(tmxXml, null);
        assertNotNull(doc);
        assertEquals(TilemapGridMode.ISOMETRIC_DIAMOND, doc.getOrientation());
        assertEquals(10, doc.getWidth());
        assertEquals(10, doc.getHeight());
        assertEquals(64, doc.getTileWidth());
        assertEquals(32, doc.getTileHeight());

        // Verify properties
        assertEquals("Frontier Outpost", doc.getProperties().getString("title", ""));
        assertEquals(5, doc.getProperties().getInt("dangerLevel", 0));

        // Verify tileset
        assertEquals(1, doc.getTileSets().size());
        TileSetVO ts = doc.getTileSets().get(0);
        assertEquals("grassland", ts.name);
        assertEquals(64, ts.tileWidth);
        assertEquals(128, ts.tileHeight);
        assertEquals(32, ts.tileOffsetY);

        // Verify layer
        assertEquals(1, doc.getLayers().size());
        TileLayer tl = (TileLayer) doc.getLayers().get(0);
        assertEquals(1, tl.getTile(0, 0));
        assertEquals(2, tl.getTile(3, 2));
    }

    @Test
    public void testTsxRoundtrip() throws Exception {
        TileSetVO ts = new TileSetVO("WaterTiles", 1, "water.png", 64, 64);
        ts.tileOffsetX = -16;
        ts.tileOffsetY = 32;
        ts.columns = 8;
        ts.tileCount = 32;

        String tsxXml = TsxSerializer.toTsxXml(ts);
        assertNotNull(tsxXml);
        assertTrue(tsxXml.contains("<tileoffset x=\"-16\" y=\"32\"/>"));

        File tmp = File.createTempFile("test_tileset", ".tsx");
        tmp.deleteOnExit();
        TsxSerializer.saveTsx(ts, tmp);

        TileSetVO parsed = TsxParser.parseTsx(tmp);
        assertEquals("WaterTiles", parsed.name);
        assertEquals(64, parsed.tileWidth);
        assertEquals(-16, parsed.tileOffsetX);
        assertEquals(32, parsed.tileOffsetY);
    }
}
