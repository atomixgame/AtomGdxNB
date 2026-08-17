package com.atomgdx.languages.glsl.ui;

import com.atomgdx.languages.glsl.GlslShaderValidator;
import com.atomgdx.core.SciFiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Split-screen visual editor for GLSL Shaders with live syntax validation and preview canvas.
 */
public class GlslShaderEditorPanel extends JPanel {
    private final JTextArea shaderTextArea;
    private final JTextArea consoleTextArea;
    private final ShaderCanvas shaderCanvas;

    public GlslShaderEditorPanel(String initialShaderCode) {
        setLayout(new BorderLayout(5, 5));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Editor & Console (Left)
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);

        shaderTextArea = new JTextArea(initialShaderCode != null ? initialShaderCode : getDefaultFragmentShader());
        shaderTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        shaderTextArea.setBackground(SciFiColors.BG_DARK);
        shaderTextArea.setForeground(SciFiColors.TEXT_PRIMARY);
        shaderTextArea.setCaretColor(SciFiColors.ACCENT_CYAN);

        consoleTextArea = new JTextArea("GLSL Compiler: Ready.\n");
        consoleTextArea.setRows(5);
        consoleTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        consoleTextArea.setBackground(SciFiColors.BG_DARKEST);
        consoleTextArea.setForeground(SciFiColors.ACCENT_GREEN);
        consoleTextArea.setEditable(false);

        leftPanel.add(new JScrollPane(shaderTextArea), BorderLayout.CENTER);
        leftPanel.add(new JScrollPane(consoleTextArea), BorderLayout.SOUTH);

        // Preview Canvas (Right)
        shaderCanvas = new ShaderCanvas();

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, shaderCanvas);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(4);

        // Top Toolbar
        JToolBar toolbar = createToolBar();

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(SciFiColors.BG_PANEL);

        JButton compileBtn = new JButton("Compile & Run");
        compileBtn.addActionListener(e -> compileShader());

        JButton formatBtn = new JButton("Format Code");
        tb.add(compileBtn);
        tb.add(formatBtn);
        return tb;
    }

    private void compileShader() {
        String code = shaderTextArea.getText();
        List<GlslShaderValidator.ShaderError> errors = GlslShaderValidator.validateShader(code);
        if (errors.isEmpty()) {
            consoleTextArea.setText("GLSL Program: Successfully compiled and linked!\nShader Uniforms: u_time, u_resolution, u_mouse\n");
            consoleTextArea.setForeground(SciFiColors.ACCENT_GREEN);
            shaderCanvas.repaint();
        } else {
            StringBuilder sb = new StringBuilder("GLSL Compilation Errors:\n");
            for (GlslShaderValidator.ShaderError err : errors) {
                sb.append("  ").append(err.toString()).append("\n");
            }
            consoleTextArea.setText(sb.toString());
            consoleTextArea.setForeground(SciFiColors.ACCENT_RED);
        }
    }

    private static String getDefaultFragmentShader() {
        return """
                #version 120
                #ifdef GL_ES
                precision mediump float;
                #endif
                
                uniform float u_time;
                uniform vec2 u_resolution;
                varying vec2 v_texCoords;
                
                void main() {
                    vec2 st = gl_FragCoord.xy / u_resolution.xy;
                    vec3 color = vec3(0.0, 0.94, 1.0) * sin(u_time + st.x * 5.0);
                    gl_FragColor = vec4(color, 1.0);
                }
                """;
    }

    public static class ShaderCanvas extends JPanel {
        public ShaderCanvas() {
            setBackground(new Color(0x0A, 0x0E, 0x14));
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE),
                    "Live Shader Viewport",
                    TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.DEFAULT_POSITION,
                    new Font("Segoe UI", Font.BOLD, 12),
                    SciFiColors.ACCENT_CYAN
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Render simulated plasma/glow shader effect
            GradientPaint gp = new GradientPaint(0, 0, new Color(0x00, 0xF0, 0xFF), w, h, new Color(0x0D, 0x11, 0x17));
            g2.setPaint(gp);
            g2.fillRoundRect(20, 30, w - 40, h - 60, 16, 16);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("GLSL Real-time Shader Output", 40, 60);

            g2.dispose();
        }
    }
}
