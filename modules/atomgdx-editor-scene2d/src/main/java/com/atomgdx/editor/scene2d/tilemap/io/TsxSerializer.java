package com.atomgdx.editor.scene2d.tilemap.io;

import com.atomgdx.editor.scene2d.tilemap.data.AnimatedTileVO;
import com.atomgdx.editor.scene2d.tilemap.data.PropertyMap;
import com.atomgdx.editor.scene2d.tilemap.data.TileSetVO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Tiled External Tileset (.TSX) XML Serializer.
 */
public class TsxSerializer {

    public static String toTsxXml(TileSetVO ts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append(String.format(
                "<tileset version=\"1.10\" tiledversion=\"1.10.2\" name=\"%s\" tilewidth=\"%d\" tileheight=\"%d\" spacing=\"%d\" margin=\"%d\" tilecount=\"%d\" columns=\"%d\">\n",
                ts.name, ts.tileWidth, ts.tileHeight, ts.spacing, ts.margin, ts.tileCount, ts.columns
        ));

        if (ts.tileOffsetX != 0 || ts.tileOffsetY != 0) {
            sb.append(String.format("  <tileoffset x=\"%d\" y=\"%d\"/>\n", ts.tileOffsetX, ts.tileOffsetY));
        }

        if (ts.imageSource != null && !ts.imageSource.isEmpty()) {
            sb.append(String.format("  <image source=\"%s\" width=\"%d\" height=\"%d\"/>\n", ts.imageSource, ts.imageWidth, ts.imageHeight));
        }

        // Animated Tiles
        for (Map.Entry<Integer, AnimatedTileVO> entry : ts.animatedTiles.entrySet()) {
            int tileId = entry.getKey();
            AnimatedTileVO anim = entry.getValue();
            sb.append(String.format("  <tile id=\"%d\">\n", tileId));
            sb.append("    <animation>\n");
            for (AnimatedTileVO.Frame frame : anim.frames) {
                sb.append(String.format("      <frame tileid=\"%d\" duration=\"%d\"/>\n", frame.tileGid, frame.durationMs));
            }
            sb.append("    </animation>\n");
            sb.append("  </tile>\n");
        }

        // Per-Tile Properties
        for (Map.Entry<Integer, PropertyMap> entry : ts.perTileProperties.entrySet()) {
            int tileId = entry.getKey();
            PropertyMap pm = entry.getValue();
            if (!pm.isEmpty()) {
                sb.append(String.format("  <tile id=\"%d\">\n", tileId));
                sb.append("    <properties>\n");
                for (Map.Entry<String, PropertyMap.PropertyValue> pe : pm.getProperties().entrySet()) {
                    PropertyMap.PropertyValue pv = pe.getValue();
                    sb.append(String.format("      <property name=\"%s\" type=\"%s\" value=\"%s\"/>\n",
                            pv.name, pv.type.name().toLowerCase(), pv.value));
                }
                sb.append("    </properties>\n");
                sb.append("  </tile>\n");
            }
        }

        sb.append("</tileset>\n");
        return sb.toString();
    }

    public static void saveTsx(TileSetVO ts, File targetFile) throws IOException {
        String xml = toTsxXml(ts);
        if (targetFile.getParentFile() != null) targetFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(targetFile, StandardCharsets.UTF_8)) {
            writer.write(xml);
        }
    }
}
