package com.atomgdx.editor.scene2d.tilemap.io;

import com.atomgdx.editor.scene2d.tilemap.data.AnimatedTileVO;
import com.atomgdx.editor.scene2d.tilemap.data.TileSetVO;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Tiled External Tileset (.TSX) XML Parser.
 */
public class TsxParser {

    public static TileSetVO parseTsx(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            return parseTsx(in);
        }
    }

    public static TileSetVO parseTsx(InputStream in) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.parse(in);
        Element root = dom.getDocumentElement();

        if (!"tileset".equalsIgnoreCase(root.getTagName())) {
            throw new IllegalArgumentException("Root element is not <tileset>");
        }

        TileSetVO ts = new TileSetVO();
        ts.name = root.getAttribute("name");
        ts.tileWidth = getIntAttr(root, "tilewidth", 32);
        ts.tileHeight = getIntAttr(root, "tileheight", 32);
        ts.spacing = getIntAttr(root, "spacing", 0);
        ts.margin = getIntAttr(root, "margin", 0);
        ts.tileCount = getIntAttr(root, "tilecount", 256);
        ts.columns = getIntAttr(root, "columns", 16);

        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) n;

            if ("image".equalsIgnoreCase(el.getTagName())) {
                ts.imageSource = el.getAttribute("source");
                ts.imageWidth = getIntAttr(el, "width", 512);
                ts.imageHeight = getIntAttr(el, "height", 512);
            } else if ("tileoffset".equalsIgnoreCase(el.getTagName())) {
                ts.tileOffsetX = getIntAttr(el, "x", 0);
                ts.tileOffsetY = getIntAttr(el, "y", 0);
            } else if ("tile".equalsIgnoreCase(el.getTagName())) {
                int tileId = getIntAttr(el, "id", 0);
                NodeList tileChildren = el.getChildNodes();
                for (int j = 0; j < tileChildren.getLength(); j++) {
                    Node tn = tileChildren.item(j);
                    if (tn.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element tel = (Element) tn;

                    if ("animation".equalsIgnoreCase(tel.getTagName())) {
                        AnimatedTileVO anim = new AnimatedTileVO(tileId);
                        NodeList frames = tel.getElementsByTagName("frame");
                        for (int k = 0; k < frames.getLength(); k++) {
                            Element fel = (Element) frames.item(k);
                            int fId = getIntAttr(fel, "tileid", tileId);
                            int dur = getIntAttr(fel, "duration", 100);
                            anim.addFrame(fId, dur);
                        }
                        ts.animatedTiles.put(tileId, anim);
                    }
                }
            }
        }
        return ts;
    }

    private static int getIntAttr(Element el, String attr, int def) {
        if (!el.hasAttribute(attr)) return def;
        try { return Integer.parseInt(el.getAttribute(attr)); } catch (Exception e) { return def; }
    }
}
