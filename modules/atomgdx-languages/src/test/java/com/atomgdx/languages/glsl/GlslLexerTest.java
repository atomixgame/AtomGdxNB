package com.atomgdx.languages.glsl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlslLexerTest {

    @Test
    void testLexerTokenization() {
        String shader = """
                #version 120
                attribute vec4 a_position;
                attribute vec4 a_color;
                attribute vec2 a_texCoord0;
                uniform mat4 u_projTrans;
                varying vec4 v_color;
                varying vec2 v_texCoords;
                
                void main() {
                    v_color = a_color;
                    v_texCoords = a_texCoord0;
                    gl_Position = u_projTrans * a_position;
                }
                """;

        List<GlslToken> tokens = GlslLexer.tokenize(shader);
        assertThat(tokens).isNotEmpty();

        long keywords = tokens.stream().filter(t -> t.getType() == GlslTokenType.KEYWORD).count();
        long types = tokens.stream().filter(t -> t.getType() == GlslTokenType.TYPE).count();
        long builtinVars = tokens.stream().filter(t -> t.getType() == GlslTokenType.BUILTIN_VAR).count();

        assertThat(keywords).isGreaterThanOrEqualTo(6); // attribute, uniform, varying, etc.
        assertThat(types).isGreaterThanOrEqualTo(6);    // vec4, vec2, mat4, void
        assertThat(builtinVars).isGreaterThanOrEqualTo(1); // gl_Position

        // Validate shader structure
        List<GlslShaderValidator.ShaderError> errors = GlslShaderValidator.validateShader(shader);
        assertThat(errors).isEmpty();
    }
}
