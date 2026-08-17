package com.atomgdx.languages.glsl;

/**
 * Token types for GLSL Shader syntax analysis and highlighting.
 */
public enum GlslTokenType {
    KEYWORD,      // attribute, uniform, varying, in, out, inout, precision, highp, mediump, lowp
    TYPE,         // void, bool, int, float, vec2, vec3, vec4, mat2, mat3, mat4, sampler2D, samplerCube
    BUILTIN_FUNC, // texture2D, sin, cos, normalize, dot, cross, clamp, mix, reflect, length
    BUILTIN_VAR,  // gl_Position, gl_FragColor, gl_FragCoord, gl_PointSize
    IDENTIFIER,
    NUMBER,
    OPERATOR,
    COMMENT,
    STRING,
    PREPROCESSOR, // #version, #ifdef, #ifndef, #endif, #define
    WHITESPACE,
    UNKNOWN
}
