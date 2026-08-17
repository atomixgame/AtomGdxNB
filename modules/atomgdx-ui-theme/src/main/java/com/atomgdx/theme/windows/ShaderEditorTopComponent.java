package com.atomgdx.theme.windows;

import com.atomgdx.languages.glsl.ui.GlslShaderEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.*;

public class ShaderEditorTopComponent extends TopComponent {

    private static final String DEFAULT_SHADER = 
            "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform float u_time;\n\n" +
            "void main() {\n" +
            "    vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
            "    gl_FragColor = v_color * texColor;\n" +
            "}\n";

    public ShaderEditorTopComponent() {
        setName("GLSL Shader Studio");
        setToolTipText("LibGDX Live GLSL Vertex & Fragment Shader Editor");
        setLayout(new BorderLayout());
        add(new GlslShaderEditorPanel(DEFAULT_SHADER), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "ShaderEditorTopComponent";
    }
}
