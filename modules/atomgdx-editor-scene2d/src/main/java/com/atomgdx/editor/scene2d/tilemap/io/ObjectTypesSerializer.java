package com.atomgdx.editor.scene2d.tilemap.io;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tiled Object Types XML (objecttypes.xml) Serializer & Parser.
 */
public class ObjectTypesSerializer {

    public static class ObjectTypeSchema {
        public String name;
        public Color color = new Color(200, 200, 200);
        public final Map<String, String> defaultProperties = new HashMap<>();

        public ObjectTypeSchema(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }

    public static List<ObjectTypeSchema> parseObjectTypes(File file) throws Exception {
        List<ObjectTypeSchema> list = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.parse(file);
        Element root = dom.getDocumentElement();

        NodeList types = root.getElementsByTagName("objecttype");
        for (int i = 0; i < types.getLength(); i++) {
            Element el = (Element) types.item(i);
            String name = el.getAttribute("name");
            Color color = Color.GRAY;
            if (el.hasAttribute("color")) {
                try { color = Color.decode(el.getAttribute("color")); } catch (Exception ignored) {}
            }
            ObjectTypeSchema schema = new ObjectTypeSchema(name, color);

            NodeList props = el.getElementsByTagName("property");
            for (int j = 0; j < props.getLength(); j++) {
                Element pel = (Element) props.item(j);
                schema.defaultProperties.put(pel.getAttribute("name"), pel.getAttribute("default"));
            }
            list.add(schema);
        }
        return list;
    }

    public static void saveObjectTypes(List<ObjectTypeSchema> schemas, File targetFile) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<objecttypes>\n");
        for (ObjectTypeSchema s : schemas) {
            String hexColor = String.format("#%02x%02x%02x", s.color.getRed(), s.color.getGreen(), s.color.getBlue());
            sb.append(String.format("  <objecttype name=\"%s\" color=\"%s\">\n", s.name, hexColor));
            for (Map.Entry<String, String> entry : s.defaultProperties.entrySet()) {
                sb.append(String.format("    <property name=\"%s\" default=\"%s\"/>\n", entry.getKey(), entry.getValue()));
            }
            sb.append("  </objecttype>\n");
        }
        sb.append("</objecttypes>\n");

        if (targetFile.getParentFile() != null) targetFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(targetFile, StandardCharsets.UTF_8)) {
            writer.write(sb.toString());
        }
    }
}
