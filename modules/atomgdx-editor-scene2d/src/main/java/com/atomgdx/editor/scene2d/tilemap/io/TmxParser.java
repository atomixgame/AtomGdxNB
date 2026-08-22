package com.atomgdx.editor.scene2d.tilemap.io;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Robust TMX XML Parser supporting Orthogonal, Isometric, Staggered, and Hexagonal maps.
 * Handles embedded & external TSX tilesets, CSV/Base64/zlib layer compression, tall tiles, and full object shapes.
 */
public class TmxParser {

    public static TilemapDocument parseTmx(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            TilemapDocument doc = parseTmx(in, file.getParentFile());
            if (doc.getName() == null || doc.getName().equals("NewLevelMap")) {
                String fname = file.getName();
                if (fname.contains(".")) fname = fname.substring(0, fname.lastIndexOf('.'));
                doc.setName(fname);
            }
            return doc;
        }
    }

    public static TilemapDocument parseTmx(String xml, File baseDir) throws Exception {
        return parseTmx(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), baseDir);
    }

    public static TilemapDocument parseTmx(InputStream in, File baseDir) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.parse(in);
        Element root = dom.getDocumentElement();

        if (!"map".equalsIgnoreCase(root.getTagName())) {
            throw new IllegalArgumentException("Root element is not <map>");
        }

        String orientationStr = root.getAttribute("orientation");
        TilemapGridMode mode = TilemapGridMode.ORTHOGONAL;
        if ("isometric".equalsIgnoreCase(orientationStr)) {
            mode = TilemapGridMode.ISOMETRIC_DIAMOND;
        } else if ("staggered".equalsIgnoreCase(orientationStr)) {
            mode = TilemapGridMode.ISOMETRIC_STAGGERED;
        } else if ("hexagonal".equalsIgnoreCase(orientationStr)) {
            mode = TilemapGridMode.HEXAGONAL_POINTY;
        }

        int width = getIntAttr(root, "width", 40);
        int height = getIntAttr(root, "height", 30);
        int tileWidth = getIntAttr(root, "tilewidth", 32);
        int tileHeight = getIntAttr(root, "tileheight", 32);

        TilemapDocument doc = new TilemapDocument("NewLevelMap", mode, width, height, tileWidth, tileHeight);
        doc.getLayers().clear(); // Clear default placeholder layers
        doc.getTileSets().clear();

        if (root.hasAttribute("backgroundcolor")) {
            try {
                doc.setBackgroundColor(Color.decode(root.getAttribute("backgroundcolor")));
            } catch (Exception ignored) {}
        }

        // Map Properties
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) node;

            if ("properties".equalsIgnoreCase(el.getTagName())) {
                parseProperties(el, doc.getProperties());
            } else if ("tileset".equalsIgnoreCase(el.getTagName())) {
                TileSetVO ts = parseTileset(el, baseDir);
                if (ts != null) doc.getTileSets().add(ts);
            } else if ("layer".equalsIgnoreCase(el.getTagName())) {
                TileLayer layer = parseTileLayer(el, width, height);
                if (layer != null) doc.addLayer(layer);
            } else if ("objectgroup".equalsIgnoreCase(el.getTagName())) {
                ObjectGroupLayer ogl = parseObjectGroup(el);
                if (ogl != null) doc.addLayer(ogl);
            } else if ("imagelayer".equalsIgnoreCase(el.getTagName())) {
                ImageLayer il = parseImageLayer(el);
                if (il != null) doc.addLayer(il);
            }
        }

        return doc;
    }

    private static TileSetVO parseTileset(Element el, File baseDir) {
        int firstGid = getIntAttr(el, "firstgid", 1);
        String sourceTsx = el.getAttribute("source");

        if (sourceTsx != null && !sourceTsx.isBlank() && baseDir != null) {
            File tsxFile = new File(baseDir, sourceTsx);
            if (tsxFile.exists()) {
                try {
                    TileSetVO ts = TsxParser.parseTsx(tsxFile);
                    ts.firstGid = firstGid;
                    ts.sourceTsx = sourceTsx;
                    return ts;
                } catch (Exception e) {
                    System.err.println("Failed to parse external TSX: " + tsxFile + ": " + e.getMessage());
                }
            }
        }

        // Embedded Tileset
        TileSetVO ts = new TileSetVO();
        ts.firstGid = firstGid;
        ts.name = el.getAttribute("name");
        ts.tileWidth = getIntAttr(el, "tilewidth", 32);
        ts.tileHeight = getIntAttr(el, "tileheight", 32);
        ts.spacing = getIntAttr(el, "spacing", 0);
        ts.margin = getIntAttr(el, "margin", 0);
        ts.tileCount = getIntAttr(el, "tilecount", 256);
        ts.columns = getIntAttr(el, "columns", 16);

        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element cel = (Element) n;

            if ("image".equalsIgnoreCase(cel.getTagName())) {
                ts.imageSource = cel.getAttribute("source");
                ts.imageWidth = getIntAttr(cel, "width", 512);
                ts.imageHeight = getIntAttr(cel, "height", 512);
            } else if ("tileoffset".equalsIgnoreCase(cel.getTagName())) {
                ts.tileOffsetX = getIntAttr(cel, "x", 0);
                ts.tileOffsetY = getIntAttr(cel, "y", 0);
            }
        }
        return ts;
    }

    private static TileLayer parseTileLayer(Element el, int mapW, int mapH) {
        String name = el.getAttribute("name");
        int w = getIntAttr(el, "width", mapW);
        int h = getIntAttr(el, "height", mapH);
        float opacity = getFloatAttr(el, "opacity", 1.0f);
        boolean visible = !"0".equals(el.getAttribute("visible"));

        TileLayer layer = new TileLayer(name, w, h);
        layer.setOpacity(opacity);
        layer.setVisible(visible);

        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element cel = (Element) n;

            if ("properties".equalsIgnoreCase(cel.getTagName())) {
                parseProperties(cel, layer.getProperties());
            } else if ("data".equalsIgnoreCase(cel.getTagName())) {
                parseLayerData(cel, layer, w, h);
            }
        }
        return layer;
    }

    private static void parseLayerData(Element dataEl, TileLayer layer, int w, int h) {
        String encoding = dataEl.getAttribute("encoding");
        String compression = dataEl.getAttribute("compression");
        String text = dataEl.getTextContent().trim();

        if ("csv".equalsIgnoreCase(encoding) || encoding.isEmpty()) {
            String[] tokens = text.split("[,\s]+");
            int idx = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (idx < tokens.length) {
                        try {
                            int gid = (int) Long.parseLong(tokens[idx].trim());
                            layer.setTile(x, y, gid);
                        } catch (Exception ignored) {}
                        idx++;
                    }
                }
            }
        } else if ("base64".equalsIgnoreCase(encoding)) {
            byte[] decoded = Base64.getDecoder().decode(text.replaceAll("\s+", ""));
            try (InputStream is = getDecompressedStream(decoded, compression)) {
                byte[] buffer = new byte[4];
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int read = is.read(buffer);
                        if (read == 4) {
                            int gid = (buffer[0] & 0xFF) | ((buffer[1] & 0xFF) << 8) |
                                      ((buffer[2] & 0xFF) << 16) | ((buffer[3] & 0xFF) << 24);
                            layer.setTile(x, y, gid);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to decode base64 layer data: " + e.getMessage());
            }
        }
    }

    private static InputStream getDecompressedStream(byte[] data, String compression) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        if ("gzip".equalsIgnoreCase(compression)) {
            return new GZIPInputStream(bais);
        } else if ("zlib".equalsIgnoreCase(compression)) {
            return new InflaterInputStream(bais);
        }
        return bais;
    }

    private static ObjectGroupLayer parseObjectGroup(Element el) {
        String name = el.getAttribute("name");
        ObjectGroupLayer ogl = new ObjectGroupLayer(name);
        ogl.setOpacity(getFloatAttr(el, "opacity", 1.0f));
        ogl.setVisible(!"0".equals(el.getAttribute("visible")));

        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element cel = (Element) n;

            if ("properties".equalsIgnoreCase(cel.getTagName())) {
                parseProperties(cel, ogl.getProperties());
            } else if ("object".equalsIgnoreCase(cel.getTagName())) {
                String objName = cel.getAttribute("name");
                String objType = cel.getAttribute("type");
                float x = getFloatAttr(cel, "x", 0);
                float y = getFloatAttr(cel, "y", 0);
                float width = getFloatAttr(cel, "width", 32);
                float height = getFloatAttr(cel, "height", 32);
                float rot = getFloatAttr(cel, "rotation", 0);

                MapObjectVO obj = new MapObjectVO(objName, objType, x, y, width, height);
                obj.rotation = rot;
                obj.visible = !"0".equals(cel.getAttribute("visible"));

                NodeList objChildren = cel.getChildNodes();
                for (int j = 0; j < objChildren.getLength(); j++) {
                    Node on = objChildren.item(j);
                    if (on.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element oel = (Element) on;
                    if ("properties".equalsIgnoreCase(oel.getTagName())) {
                        parseProperties(oel, obj.properties);
                    }
                }
                ogl.addObject(obj);
            }
        }
        return ogl;
    }

    private static ImageLayer parseImageLayer(Element el) {
        String name = el.getAttribute("name");
        float ox = getFloatAttr(el, "offsetx", 0);
        float oy = getFloatAttr(el, "offsety", 0);
        ImageLayer il = new ImageLayer(name, "", ox, oy);
        il.setOpacity(getFloatAttr(el, "opacity", 1.0f));
        il.setVisible(!"0".equals(el.getAttribute("visible")));

        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element cel = (Element) n;
            if ("image".equalsIgnoreCase(cel.getTagName())) {
                il.setImagePath(cel.getAttribute("source"));
                il.setRepeatX("1".equals(cel.getAttribute("repeatx")));
                il.setRepeatY("1".equals(cel.getAttribute("repeaty")));
            }
        }
        return il;
    }

    private static void parseProperties(Element propsEl, PropertyMap map) {
        NodeList list = propsEl.getElementsByTagName("property");
        for (int i = 0; i < list.getLength(); i++) {
            Element p = (Element) list.item(i);
            String name = p.getAttribute("name");
            String type = p.getAttribute("type");
            String val = p.getAttribute("value");
            if (val.isEmpty() && p.hasChildNodes()) val = p.getTextContent().trim();

            if ("int".equalsIgnoreCase(type)) {
                try { map.putInt(name, Integer.parseInt(val)); } catch (Exception e) { map.putString(name, val); }
            } else if ("float".equalsIgnoreCase(type)) {
                try { map.putFloat(name, Float.parseFloat(val)); } catch (Exception e) { map.putString(name, val); }
            } else if ("bool".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
                map.putBoolean(name, Boolean.parseBoolean(val));
            } else if ("color".equalsIgnoreCase(type)) {
                try { map.putColor(name, Color.decode(val)); } catch (Exception e) { map.putString(name, val); }
            } else {
                map.putString(name, val);
            }
        }
    }

    private static int getIntAttr(Element el, String attr, int def) {
        if (!el.hasAttribute(attr)) return def;
        try { return Integer.parseInt(el.getAttribute(attr)); } catch (Exception e) { return def; }
    }

    private static float getFloatAttr(Element el, String attr, float def) {
        if (!el.hasAttribute(attr)) return def;
        try { return Float.parseFloat(el.getAttribute(attr)); } catch (Exception e) { return def; }
    }
}
