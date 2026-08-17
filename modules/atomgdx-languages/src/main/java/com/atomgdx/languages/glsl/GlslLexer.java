package com.atomgdx.languages.glsl;

import java.util.*;

/**
 * High-performance tokenizer for OpenGL Shading Language (GLSL).
 */
public class GlslLexer {
    private static final Set<String> KEYWORDS = Set.of(
            "attribute", "uniform", "varying", "in", "out", "inout", "precision",
            "highp", "mediump", "lowp", "if", "else", "for", "while", "do",
            "return", "break", "continue", "discard", "struct", "const", "layout"
    );

    private static final Set<String> TYPES = Set.of(
            "void", "bool", "int", "uint", "float", "double",
            "vec2", "vec3", "vec4", "bvec2", "bvec3", "bvec4", "ivec2", "ivec3", "ivec4",
            "mat2", "mat3", "mat4", "mat2x2", "mat3x3", "mat4x4",
            "sampler2D", "sampler3D", "samplerCube", "sampler2DShadow"
    );

    private static final Set<String> BUILTIN_FUNCTIONS = Set.of(
            "texture2D", "texture", "textureCube", "sin", "cos", "tan", "asin", "acos", "atan",
            "radians", "degrees", "pow", "exp", "log", "exp2", "log2", "sqrt", "inversesqrt",
            "abs", "sign", "floor", "ceil", "fract", "mod", "min", "max", "clamp", "mix",
            "step", "smoothstep", "length", "distance", "dot", "cross", "normalize",
            "faceforward", "reflect", "refract", "matrixCompMult"
    );

    private static final Set<String> BUILTIN_VARS = Set.of(
            "gl_Position", "gl_PointSize", "gl_FragCoord", "gl_FrontFacing", "gl_FragColor",
            "gl_FragData", "gl_PointCoord", "gl_VertexID", "gl_InstanceID"
    );

    public static List<GlslToken> tokenize(String source) {
        List<GlslToken> tokens = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return tokens;
        }

        int length = source.length();
        int pos = 0;

        while (pos < length) {
            char c = source.charAt(pos);

            // Whitespace
            if (Character.isWhitespace(c)) {
                int start = pos;
                while (pos < length && Character.isWhitespace(source.charAt(pos))) {
                    pos++;
                }
                tokens.add(new GlslToken(GlslTokenType.WHITESPACE, source.substring(start, pos), start, pos));
                continue;
            }

            // Single line comment
            if (c == '/' && pos + 1 < length && source.charAt(pos + 1) == '/') {
                int start = pos;
                while (pos < length && source.charAt(pos) != '\n' && source.charAt(pos) != '\r') {
                    pos++;
                }
                tokens.add(new GlslToken(GlslTokenType.COMMENT, source.substring(start, pos), start, pos));
                continue;
            }

            // Multi-line comment
            if (c == '/' && pos + 1 < length && source.charAt(pos + 1) == '*') {
                int start = pos;
                pos += 2;
                while (pos + 1 < length && !(source.charAt(pos) == '*' && source.charAt(pos + 1) == '/')) {
                    pos++;
                }
                if (pos + 1 < length) {
                    pos += 2; // skip */
                } else {
                    pos = length;
                }
                tokens.add(new GlslToken(GlslTokenType.COMMENT, source.substring(start, pos), start, pos));
                continue;
            }

            // Preprocessor
            if (c == '#') {
                int start = pos;
                while (pos < length && source.charAt(pos) != '\n' && source.charAt(pos) != '\r') {
                    pos++;
                }
                tokens.add(new GlslToken(GlslTokenType.PREPROCESSOR, source.substring(start, pos), start, pos));
                continue;
            }

            // Identifiers / Keywords / Types / Built-ins
            if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < length && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) {
                    pos++;
                }
                String word = source.substring(start, pos);
                GlslTokenType type;
                if (KEYWORDS.contains(word)) {
                    type = GlslTokenType.KEYWORD;
                } else if (TYPES.contains(word)) {
                    type = GlslTokenType.TYPE;
                } else if (BUILTIN_FUNCTIONS.contains(word)) {
                    type = GlslTokenType.BUILTIN_FUNC;
                } else if (BUILTIN_VARS.contains(word)) {
                    type = GlslTokenType.BUILTIN_VAR;
                } else {
                    type = GlslTokenType.IDENTIFIER;
                }
                tokens.add(new GlslToken(type, word, start, pos));
                continue;
            }

            // Numbers
            if (Character.isDigit(c) || (c == '.' && pos + 1 < length && Character.isDigit(source.charAt(pos + 1)))) {
                int start = pos;
                boolean hasDot = (c == '.');
                pos++;
                while (pos < length) {
                    char next = source.charAt(pos);
                    if (Character.isDigit(next)) {
                        pos++;
                    } else if (next == '.' && !hasDot) {
                        hasDot = true;
                        pos++;
                    } else if (next == 'f' || next == 'F' || next == 'u' || next == 'U') {
                        pos++;
                        break;
                    } else {
                        break;
                    }
                }
                tokens.add(new GlslToken(GlslTokenType.NUMBER, source.substring(start, pos), start, pos));
                continue;
            }

            // Operators & Punctuation
            tokens.add(new GlslToken(GlslTokenType.OPERATOR, String.valueOf(c), pos, pos + 1));
            pos++;
        }

        return tokens;
    }
}
