package com.atomgdx.editor.scene2d.tilemap.io;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Standard TMX (XML) Serializer & Parser for native LibGDX TmxMapLoader compatibility.
 */
public class TmxSerializer {

    public static String toTmxXml(TilemapDocument doc) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        String orientationStr = "orthogonal";
        if (doc.getOrientation() == TilemapGridMode.ISOMETRIC_DIAMOND) {
            orientationStr = "isometric";
        } else if (doc.getOrientation() == TilemapGridMode.ISOMETRIC_STAGGERED) {
            orientationStr = "staggered";
        } else if (doc.getOrientation() == TilemapGridMode.HEXAGONAL_POINTY || doc.getOrientation() == TilemapGridMode.HEXAGONAL_FLAT) {
            orientationStr = "hexagonal";
        }

        String hexColor = String.format("#%02x%02x%02x",
                doc.getBackgroundColor().getRed(), doc.getBackgroundColor().getGreen(), doc.getBackgroundColor().getBlue());

        sb.append(String.format(
                "<map version=\"1.10\" tiledversion=\"1.10.2\" orientation=\"%s\" renderorder=\"right-down\" width=\"%d\" height=\"%d\" tilewidth=\"%d\" tileheight=\"%d\" infinite=\"0\" backgroundcolor=\"%s\">\n",
                orientationStr, doc.getWidth(), doc.getHeight(), doc.getTileWidth(), doc.getTileHeight(), hexColor
        ));

        // Map Properties
        PropertyMap mapProps = new PropertyMap();
        if (doc.getName() != null && !doc.getName().isBlank()) {
            mapProps.putString("mapName", doc.getName());
        }
        for (Map.Entry<String, PropertyMap.PropertyValue> e : doc.getProperties().getProperties().entrySet()) {
            mapProps.getProperties().put(e.getKey(), e.getValue());
        }
        writeProperties(sb, mapProps, "  ");

        // Tilesets
        for (TileSetVO ts : doc.getTileSets()) {
            if (ts.sourceTsx != null && !ts.sourceTsx.isBlank()) {
                sb.append(String.format("  <tileset firstgid=\"%d\" source=\"%s\"/>\n", ts.firstGid, ts.sourceTsx));
            } else {
                sb.append(String.format(
                        "  <tileset firstgid=\"%d\" name=\"%s\" tilewidth=\"%d\" tileheight=\"%d\" spacing=\"%d\" margin=\"%d\" tilecount=\"%d\" columns=\"%d\">\n",
                        ts.firstGid, ts.name, ts.tileWidth, ts.tileHeight, ts.spacing, ts.margin, ts.tileCount, ts.columns
                ));
                if (ts.tileOffsetX != 0 || ts.tileOffsetY != 0) {
                    sb.append(String.format("    <tileoffset x=\"%d\" y=\"%d\"/>\n", ts.tileOffsetX, ts.tileOffsetY));
                }
                if (ts.imageSource != null && !ts.imageSource.isEmpty()) {
                    sb.append(String.format("    <image source=\"%s\" width=\"%d\" height=\"%d\"/>\n", ts.imageSource, ts.imageWidth, ts.imageHeight));
                }

                // Tile animations
                for (Map.Entry<Integer, AnimatedTileVO> entry : ts.animatedTiles.entrySet()) {
                    int tileId = entry.getKey();
                    AnimatedTileVO anim = entry.getValue();
                    sb.append(String.format("    <tile id=\"%d\">\n", tileId));
                    sb.append("      <animation>\n");
                    for (AnimatedTileVO.Frame frame : anim.frames) {
                        sb.append(String.format("        <frame tileid=\"%d\" duration=\"%d\"/>\n", frame.tileGid, frame.durationMs));
                    }
                    sb.append("      </animation>\n");
                    sb.append("    </tile>\n");
                }

                // Per-tile properties
                for (Map.Entry<Integer, PropertyMap> entry : ts.perTileProperties.entrySet()) {
                    int tileId = entry.getKey();
                    PropertyMap pm = entry.getValue();
                    if (!pm.isEmpty()) {
                        sb.append(String.format("    <tile id=\"%d\">\n", tileId));
                        writeProperties(sb, pm, "      ");
                        sb.append("    </tile>\n");
                    }
                }

                sb.append("  </tileset>\n");
            }
        }

        // Layers
        int layerId = 1;
        for (TilemapLayer layer : doc.getLayers()) {
            if (layer instanceof TileLayer) {
                TileLayer tl = (TileLayer) layer;
                sb.append(String.format(
                        "  <layer id=\"%d\" name=\"%s\" width=\"%d\" height=\"%d\" opacity=\"%.2f\" visible=\"%d\">\n",
                        layerId++, tl.getName(), tl.getWidth(), tl.getHeight(), tl.getOpacity(), tl.isVisible() ? 1 : 0
                ));

                if (!tl.getProperties().isEmpty()) {
                    writeProperties(sb, tl.getProperties(), "    ");
                }

                sb.append("    <data encoding=\"csv\">\n");
                int[][] raw = tl.getRawTiles();
                for (int y = 0; y < tl.getHeight(); y++) {
                    sb.append("      ");
                    for (int x = 0; x < tl.getWidth(); x++) {
                        sb.append(raw[y][x]);
                        if (x < tl.getWidth() - 1 || y < tl.getHeight() - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("\n");
                }
                sb.append("    </data>\n");
                sb.append("  </layer>\n");
            } else if (layer instanceof ObjectGroupLayer) {
                ObjectGroupLayer ogl = (ObjectGroupLayer) layer;
                sb.append(String.format(
                        "  <objectgroup id=\"%d\" name=\"%s\" opacity=\"%.2f\" visible=\"%d\">\n",
                        layerId++, ogl.getName(), ogl.getOpacity(), ogl.isVisible() ? 1 : 0
                ));

                if (!ogl.getProperties().isEmpty()) {
                    writeProperties(sb, ogl.getProperties(), "    ");
                }

                int objId = 1;
                for (MapObjectVO obj : ogl.getObjects()) {
                    sb.append(String.format(
                            "    <object id=\"%d\" name=\"%s\" type=\"%s\" x=\"%.1f\" y=\"%.1f\" width=\"%.1f\" height=\"%.1f\" rotation=\"%.1f\" visible=\"%d\">\n",
                            objId++, obj.name, obj.type, obj.x, obj.y, obj.width, obj.height, obj.rotation, obj.visible ? 1 : 0
                    ));
                    if (!obj.properties.isEmpty()) {
                        writeProperties(sb, obj.properties, "      ");
                    }
                    sb.append("    </object>\n");
                }
                sb.append("  </objectgroup>\n");
            } else if (layer instanceof ImageLayer) {
                ImageLayer il = (ImageLayer) layer;
                sb.append(String.format(
                        "  <imagelayer id=\"%d\" name=\"%s\" offsetx=\"%.1f\" offsety=\"%.1f\" opacity=\"%.2f\" visible=\"%d\">\n",
                        layerId++, il.getName(), il.getOffsetX(), il.getOffsetY(), il.getOpacity(), il.isVisible() ? 1 : 0
                ));
                sb.append(String.format("    <image source=\"%s\" repeatx=\"%d\" repeaty=\"%d\"/>\n", il.getImagePath(), il.isRepeatX() ? 1 : 0, il.isRepeatY() ? 1 : 0));
                sb.append("  </imagelayer>\n");
            }
        }

        sb.append("</map>\n");
        return sb.toString();
    }

    public static void saveTmx(TilemapDocument doc, File targetFile) throws IOException {
        String xml = toTmxXml(doc);
        if (targetFile.getParentFile() != null) {
            targetFile.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(targetFile, StandardCharsets.UTF_8)) {
            writer.write(xml);
        }
    }

    private static void writeProperties(StringBuilder sb, PropertyMap props, String indent) {
        if (props == null || props.isEmpty()) return;
        sb.append(indent).append("<properties>\n");
        for (Map.Entry<String, PropertyMap.PropertyValue> entry : props.getProperties().entrySet()) {
            PropertyMap.PropertyValue pv = entry.getValue();
            sb.append(String.format("%s  <property name=\"%s\" type=\"%s\" value=\"%s\"/>\n",
                    indent, pv.name, pv.type.name().toLowerCase(), pv.value));
        }
        sb.append(indent).append("</properties>\n");
    }
}
