package com.atomgdx.editor.scene2d.tilemap.io;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Importer for Flare ARPG .txt map files into standard LibGDX / AtomGDX TilemapDocuments.
 */
public class FlareMapConverter {

    public static TilemapDocument parseFlareMap(File file) throws Exception {
        TilemapDocument doc = new TilemapDocument();
        doc.setOrientation(TilemapGridMode.ISOMETRIC_DIAMOND);
        doc.getLayers().clear();
        doc.getTileSets().clear();

        int width = 64;
        int height = 64;
        int tileW = 64;
        int tileH = 32;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";
            TileLayer currentLayer = null;
            int layerY = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length() - 1).toLowerCase();
                    continue;
                }

                if (section.equals("header")) {
                    if (line.startsWith("title=")) doc.setName(line.substring(6).trim());
                    else if (line.startsWith("width=")) width = Integer.parseInt(line.substring(6).trim());
                    else if (line.startsWith("height=")) height = Integer.parseInt(line.substring(7).trim());
                    else if (line.startsWith("tilewidth=")) tileW = Integer.parseInt(line.substring(10).trim());
                    else if (line.startsWith("tileheight=")) tileH = Integer.parseInt(line.substring(11).trim());
                } else if (section.equals("layer")) {
                    if (line.startsWith("type=")) {
                        String type = line.substring(5).trim();
                        currentLayer = new TileLayer(type, width, height);
                        doc.addLayer(currentLayer);
                        layerY = 0;
                    } else if (line.startsWith("data=") && currentLayer != null) {
                        String dataStr = line.substring(5).trim();
                        String[] tokens = dataStr.split(",");
                        for (int x = 0; x < Math.min(width, tokens.length); x++) {
                            try {
                                int gid = Integer.parseInt(tokens[x].trim());
                                currentLayer.setTile(x, layerY, gid);
                            } catch (Exception ignored) {}
                        }
                        layerY++;
                    }
                }
            }
        }

        return doc;
    }
}
