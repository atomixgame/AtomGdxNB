package com.atomgdx.editor.font;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Production Font Generation Engine for LibGDX.
 * Rasterizes TrueType/OpenType fonts, computes glyph metrics, generates signed distance fields (SDF/MSDF),
 * and writes packed .fnt and .png font textures.
 */
public class FontGeneratorEngine {

    public static class GlyphInfo {
        public char character;
        public int id;
        public int x, y, width, height;
        public int xoffset, yoffset, xadvance;
        public BufferedImage glyphImage;
    }

    public static class FontResult {
        public final File fntFile;
        public final File pngFile;
        public final BufferedImage atlasImage;
        public final List<GlyphInfo> glyphs;

        public FontResult(File fntFile, File pngFile, BufferedImage atlasImage, List<GlyphInfo> glyphs) {
            this.fntFile = fntFile;
            this.pngFile = pngFile;
            this.atlasImage = atlasImage;
            this.glyphs = glyphs;
        }
    }

    public static Font loadFont(FontGeneratorSettings settings) {
        String path = settings.getFontFilePath();
        if (path != null && !path.isBlank()) {
            File f = new File(path);
            if (f.exists()) {
                try {
                    return Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(Font.PLAIN, (float) settings.getFontSize());
                } catch (Exception ignored) {}
            }
        }
        return new Font("Segoe UI", Font.PLAIN, settings.getFontSize());
    }

    public static FontResult generateFont(FontGeneratorSettings settings, File outputDir, String fontBaseName) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        BufferedImage atlas = renderAtlas(settings);
        List<GlyphInfo> glyphs = computeAndPackGlyphs(settings, atlas);

        // Write PNG
        File pngFile = new File(outputDir, fontBaseName + ".png");
        ImageIO.write(atlas, "png", pngFile);

        // Write FNT descriptor
        File fntFile = new File(outputDir, fontBaseName + ".fnt");
        String fntContent = buildFntDescriptor(settings, fontBaseName, glyphs);
        Files.writeString(fntFile.toPath(), fntContent, StandardCharsets.UTF_8);

        return new FontResult(fntFile, pngFile, atlas, glyphs);
    }

    public static BufferedImage renderAtlas(FontGeneratorSettings settings) {
        int pageW = settings.getPageWidth();
        int pageH = settings.getPageHeight();
        BufferedImage atlas = new BufferedImage(pageW, pageH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = atlas.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        Font font = loadFont(settings);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int pad = settings.getPadding();
        int outlineW = settings.getOutlineWidth();
        int curX = pad;
        int curY = pad;
        int rowHeight = 0;

        Color textColor = new Color(settings.getFontColor());
        Color outlineColor = new Color(settings.getOutlineColor());
        Color shadowColor = new Color(settings.getShadowColor(), true);

        String charset = settings.getCharacterSet();
        FontRenderContext frc = g2.getFontRenderContext();

        for (int i = 0; i < charset.length(); i++) {
            char c = charset.charAt(i);
            String str = String.valueOf(c);
            int charW = Math.max(1, fm.charWidth(c));
            int charH = Math.max(1, fm.getHeight());
            int cellW = charW + (pad + outlineW) * 2;
            int cellH = charH + (pad + outlineW) * 2;

            if (curX + cellW >= pageW) {
                curX = pad;
                curY += rowHeight + pad;
                rowHeight = 0;
            }

            if (curY + cellH >= pageH) {
                break; // Exceeded atlas page
            }

            int baselineX = curX + pad + outlineW;
            int baselineY = curY + pad + outlineW + fm.getAscent();

            // Render Shadow
            if (settings.isIncludeShadow()) {
                g2.setColor(shadowColor);
                g2.drawString(str, baselineX + settings.getShadowOffsetX(), baselineY + settings.getShadowOffsetY());
            }

            // Render Outline
            if (outlineW > 0) {
                g2.setColor(outlineColor);
                for (int dx = -outlineW; dx <= outlineW; dx++) {
                    for (int dy = -outlineW; dy <= outlineW; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2.drawString(str, baselineX + dx, baselineY + dy);
                        }
                    }
                }
            }

            // Render Primary Glyph
            g2.setColor(textColor);
            g2.drawString(str, baselineX, baselineY);

            curX += cellW;
            rowHeight = Math.max(rowHeight, cellH);
        }

        g2.dispose();

        // Apply SDF / Distance Field transform if enabled
        if (settings.getFontType() == FontGeneratorSettings.FontType.SDF || settings.getFontType() == FontGeneratorSettings.FontType.MSDF) {
            applyDistanceFieldTransform(atlas, settings.getSpread());
        }

        return atlas;
    }

    private static List<GlyphInfo> computeAndPackGlyphs(FontGeneratorSettings settings, BufferedImage atlas) {
        List<GlyphInfo> glyphs = new ArrayList<>();
        Font font = loadFont(settings);
        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tmp.createGraphics();
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        g2.dispose();

        int pad = settings.getPadding();
        int outlineW = settings.getOutlineWidth();
        int pageW = settings.getPageWidth();
        int pageH = settings.getPageHeight();
        int curX = pad;
        int curY = pad;
        int rowHeight = 0;

        String charset = settings.getCharacterSet();
        for (int i = 0; i < charset.length(); i++) {
            char c = charset.charAt(i);
            int charW = Math.max(1, fm.charWidth(c));
            int charH = Math.max(1, fm.getHeight());
            int cellW = charW + (pad + outlineW) * 2;
            int cellH = charH + (pad + outlineW) * 2;

            if (curX + cellW >= pageW) {
                curX = pad;
                curY += rowHeight + pad;
                rowHeight = 0;
            }

            if (curY + cellH >= pageH) break;

            GlyphInfo info = new GlyphInfo();
            info.character = c;
            info.id = (int) c;
            info.x = curX;
            info.y = curY;
            info.width = cellW;
            info.height = cellH;
            info.xoffset = -outlineW;
            info.yoffset = 0;
            info.xadvance = charW + pad;
            glyphs.add(info);

            curX += cellW;
            rowHeight = Math.max(rowHeight, cellH);
        }

        return glyphs;
    }

    private static String buildFntDescriptor(FontGeneratorSettings settings, String fontBaseName, List<GlyphInfo> glyphs) {
        StringBuilder sb = new StringBuilder();
        int fontSize = settings.getFontSize();

        sb.append("info face=\"").append(fontBaseName).append("\" size=").append(fontSize)
                .append(" bold=0 italic=0 charset=\"\" unicode=1 stretchH=100 smooth=1 aa=1 padding=")
                .append(settings.getPadding()).append(",").append(settings.getPadding()).append(",")
                .append(settings.getPadding()).append(",").append(settings.getPadding()).append(" spacing=1,1\n");

        sb.append("common lineHeight=").append(fontSize + settings.getPadding() * 2 + 4)
                .append(" base=").append(fontSize)
                .append(" scaleW=").append(settings.getPageWidth())
                .append(" scaleH=").append(settings.getPageHeight())
                .append(" pages=1 packed=0\n");

        sb.append("page id=0 file=\"").append(fontBaseName).append(".png\"\n");
        sb.append("chars count=").append(glyphs.size()).append("\n");

        for (GlyphInfo g : glyphs) {
            sb.append("char id=").append(g.id)
                    .append(" x=").append(g.x)
                    .append(" y=").append(g.y)
                    .append(" width=").append(g.width)
                    .append(" height=").append(g.height)
                    .append(" xoffset=").append(g.xoffset)
                    .append(" yoffset=").append(g.yoffset)
                    .append(" xadvance=").append(g.xadvance)
                    .append(" page=0 chnl=15\n");
        }

        return sb.toString();
    }

    private static void applyDistanceFieldTransform(BufferedImage img, int spread) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] rgb = img.getRGB(0, 0, w, h, null, 0, w);
        int[] out = new int[w * h];

        float maxDist = Math.max(1f, (float) spread);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int idx = y * w + x;
                int alpha = (rgb[idx] >> 24) & 0xFF;
                boolean inside = alpha > 128;

                float minDistance = maxDist;
                int minX = Math.max(0, x - spread);
                int maxX = Math.min(w - 1, x + spread);
                int minY = Math.max(0, y - spread);
                int maxY = Math.min(h - 1, y + spread);

                for (int ny = minY; ny <= maxY; ny++) {
                    for (int nx = minX; nx <= maxX; nx++) {
                        int nAlpha = (rgb[ny * w + nx] >> 24) & 0xFF;
                        boolean nInside = nAlpha > 128;
                        if (inside != nInside) {
                            float dist = (float) Math.hypot(x - nx, y - ny);
                            if (dist < minDistance) {
                                minDistance = dist;
                            }
                        }
                    }
                }

                float signedDist = inside ? minDistance : -minDistance;
                float normalized = Math.max(0f, Math.min(1f, 0.5f + (signedDist / (maxDist * 2f))));
                int val = (int) (normalized * 255f);

                out[idx] = (val << 24) | (val << 16) | (val << 8) | val;
            }
        }

        img.setRGB(0, 0, w, h, out, 0, w);
    }

    public static void generateSdfShaderFiles(File outputDir, String shaderBaseName) throws IOException {
        if (!outputDir.exists()) outputDir.mkdirs();

        String vert = "attribute vec4 " + "a_position;\n" +
                "attribute vec4 " + "a_color;\n" +
                "attribute vec2 " + "a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n\n" +
                "void main() {\n" +
                "    v_color = a_color;\n" +
                "    v_texCoords = a_texCoord0;\n" +
                "    gl_Position = u_projTrans * a_position;\n" +
                "}\n";

        String frag = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "const float smoothing = 1.0 / 16.0;\n\n" +
                "void main() {\n" +
                "    float distance = texture2D(u_texture, v_texCoords).a;\n" +
                "    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);\n" +
                "    gl_FragColor = vec4(v_color.rgb, v_color.a * alpha);\n" +
                "}\n";

        Files.writeString(new File(outputDir, shaderBaseName + ".vert").toPath(), vert, StandardCharsets.UTF_8);
        Files.writeString(new File(outputDir, shaderBaseName + ".frag").toPath(), frag, StandardCharsets.UTF_8);
    }
}
