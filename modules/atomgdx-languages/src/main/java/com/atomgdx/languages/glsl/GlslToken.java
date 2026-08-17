package com.atomgdx.languages.glsl;

/**
 * Represents a token in GLSL source code.
 */
public class GlslToken {
    private final GlslTokenType type;
    private final String text;
    private final int startOffset;
    private final int endOffset;

    public GlslToken(GlslTokenType type, String text, int startOffset, int endOffset) {
        this.type = type;
        this.text = text;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public GlslTokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public String toString() {
        return type + "[" + text + "] (" + startOffset + "-" + endOffset + ")";
    }
}
