package com.atomgdx.editor.font;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Generates Bitmap Font descriptors (.fnt) and coordinates SDF/MSDF font assets.
 */
public class FontGeneratorEngine {

    public static File generateFntDescriptor(FontGeneratorSettings settings, File outputDir, String fontBaseName) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File fntFile = new File(outputDir, fontBaseName + ".fnt");
        StringBuilder sb = new StringBuilder();

        sb.append("info face=\"").append(fontBaseName).append("\" size=").append(settings.getFontSize())
                .append(" bold=0 italic=0 charset=\"\" unicode=1 stretchH=100 smooth=1 aa=1 padding=")
                .append(settings.getPadding()).append(",").append(settings.getPadding()).append(",")
                .append(settings.getPadding()).append(",").append(settings.getPadding()).append(" spacing=1,1\n");

        sb.append("common lineHeight=").append(settings.getFontSize() + 4).append(" base=").append(settings.getFontSize())
                .append(" scaleW=").append(settings.getPageWidth()).append(" scaleH=").append(settings.getPageHeight())
                .append(" pages=1 packed=0\n");

        sb.append("page id=0 file=\"").append(fontBaseName).append(".png\"\n");
        sb.append("chars count=").append(settings.getCharacterSet().length()).append("\n");

        int x = 0;
        int y = 0;
        int charWidth = settings.getFontSize();
        int charHeight = settings.getFontSize() + 2;

        for (int i = 0; i < settings.getCharacterSet().length(); i++) {
            char c = settings.getCharacterSet().charAt(i);
            if (x + charWidth > settings.getPageWidth()) {
                x = 0;
                y += charHeight;
            }

            sb.append("char id=").append((int) c)
                    .append(" x=").append(x)
                    .append(" y=").append(y)
                    .append(" width=").append(charWidth)
                    .append(" height=").append(charHeight)
                    .append(" xoffset=0 yoffset=0 xadvance=").append(charWidth - 2)
                    .append(" page=0 chnl=15\n");

            x += charWidth;
        }

        Files.writeString(fntFile.toPath(), sb.toString(), StandardCharsets.UTF_8);
        return fntFile;
    }
}
