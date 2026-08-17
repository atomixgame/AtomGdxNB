package com.atomgdx.editor.skin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Data model for a LibGDX Scene2D / VisUI Skin containing color palettes, fonts, and widget styles.
 */
public class SkinModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name = "Default Skin";
    private final Map<String, String> colors = new LinkedHashMap<>();
    private final Map<String, String> fonts = new LinkedHashMap<>();
    private final List<WidgetStyle> styles = new ArrayList<>();

    public SkinModel() {
        this("VisUI Cyberpunk Skin");
    }

    public SkinModel(String name) {
        this.name = name;
        colors.put("white", "ffffff");
        colors.put("black", "000000");
        colors.put("neon-cyan", "00f0ff");
        colors.put("neon-purple", "b026ff");
        colors.put("dark-bg", "0d1117");

        fonts.put("default-font", "fonts/default.fnt");
        fonts.put("title-font", "fonts/title.fnt");

        WidgetStyle buttonStyle = new WidgetStyle("com.badlogic.gdx.scenes.scene2d.ui.TextButton$TextButtonStyle", "default");
        buttonStyle.setProperty("up", "button-up");
        buttonStyle.setProperty("down", "button-down");
        buttonStyle.setProperty("over", "button-over");
        buttonStyle.setProperty("font", "default-font");
        buttonStyle.setProperty("fontColor", "white");
        styles.add(buttonStyle);

        WidgetStyle labelStyle = new WidgetStyle("com.badlogic.gdx.scenes.scene2d.ui.Label$LabelStyle", "default");
        labelStyle.setProperty("font", "default-font");
        labelStyle.setProperty("fontColor", "white");
        styles.add(labelStyle);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getColors() {
        return colors;
    }

    public void addColor(String name, String hex) {
        colors.put(name, hex);
    }

    public Map<String, String> getFonts() {
        return fonts;
    }

    public void addFont(String name, String path) {
        fonts.put(name, path);
    }

    public List<WidgetStyle> getStyles() {
        return Collections.unmodifiableList(styles);
    }

    public void addStyle(WidgetStyle style) {
        styles.add(style);
    }

    public void removeStyle(WidgetStyle style) {
        styles.remove(style);
    }

    public static SkinModel fromJsonFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return parseJson(sb.toString(), file.getName().replace(".json", ""));
    }

    public static SkinModel parseJson(String json, String skinName) {
        SkinModel model = new SkinModel(skinName);
        model.colors.clear();
        model.fonts.clear();
        model.styles.clear();

        // Simple tokenizer / extractor for LibGDX skin blocks
        if (json.contains("Color")) {
            extractSimpleMap(json, "Color", model.colors);
        }
        if (json.contains("BitmapFont")) {
            extractFontMap(json, model.fonts);
        }
        if (model.colors.isEmpty()) {
            model.colors.put("white", "ffffff");
            model.colors.put("neon-cyan", "00f0ff");
        }
        if (model.fonts.isEmpty()) {
            model.fonts.put("default-font", "fonts/default.fnt");
        }

        WidgetStyle buttonStyle = new WidgetStyle("com.badlogic.gdx.scenes.scene2d.ui.TextButton$TextButtonStyle", "default");
        buttonStyle.setProperty("up", "button-up");
        buttonStyle.setProperty("down", "button-down");
        buttonStyle.setProperty("font", "default-font");
        model.styles.add(buttonStyle);

        return model;
    }

    private static void extractSimpleMap(String json, String tag, Map<String, String> outMap) {
        int start = json.indexOf(tag);
        if (start == -1) return;
        int openBrace = json.indexOf("{", start);
        if (openBrace == -1) return;
        int closeBrace = json.indexOf("}", openBrace);
        if (closeBrace == -1) return;

        String block = json.substring(openBrace + 1, closeBrace);
        String[] lines = block.split("\n|,");
        for (String l : lines) {
            String clean = l.trim().replace("\"", "");
            if (clean.contains(":")) {
                String[] parts = clean.split(":");
                if (parts.length >= 2) {
                    outMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }

    private static void extractFontMap(String json, Map<String, String> outMap) {
        int start = json.indexOf("BitmapFont");
        if (start == -1) return;
        int openBrace = json.indexOf("{", start);
        if (openBrace == -1) return;
        int closeBrace = json.indexOf("}", openBrace);
        if (closeBrace == -1) return;

        String block = json.substring(openBrace + 1, closeBrace);
        String[] lines = block.split("\n");
        for (String l : lines) {
            String clean = l.trim().replace("\"", "");
            if (clean.contains("file")) {
                int col = clean.indexOf(":");
                if (col > 0) {
                    String fontName = clean.substring(0, col).trim();
                    outMap.put(fontName, "fonts/" + fontName + ".fnt");
                }
            }
        }
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{\n");
        sb.append("  \"com.badlogic.gdx.graphics.Color\": {\n");
        int cIdx = 0;
        for (Map.Entry<String, String> e : colors.entrySet()) {
            sb.append("    \"").append(e.getKey()).append("\": \"").append(e.getValue()).append("\"");
            if (++cIdx < colors.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("  },\n");

        sb.append("  \"com.badlogic.gdx.graphics.g2d.BitmapFont\": {\n");
        int fIdx = 0;
        for (Map.Entry<String, String> e : fonts.entrySet()) {
            sb.append("    \"").append(e.getKey()).append("\": { \"file\": \"").append(e.getValue()).append("\" }");
            if (++fIdx < fonts.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("  },\n");

        for (int i = 0; i < styles.size(); i++) {
            WidgetStyle s = styles.get(i);
            sb.append("  \"").append(s.getWidgetType()).append("\": {\n");
            sb.append("    \"").append(s.getStyleName()).append("\": {\n");
            int pIdx = 0;
            for (Map.Entry<String, String> p : s.getProperties().entrySet()) {
                sb.append("      \"").append(p.getKey()).append("\": \"").append(p.getValue()).append("\"");
                if (++pIdx < s.getProperties().size()) sb.append(",");
                sb.append("\n");
            }
            sb.append("    }\n  }");
            if (i + 1 < styles.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
