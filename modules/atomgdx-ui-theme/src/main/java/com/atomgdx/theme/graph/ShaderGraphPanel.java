package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Unity ShaderGraph / Blender style visual node shader editor panel.
 */
public class ShaderGraphPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;
    private final JTextArea codePreviewArea = new JTextArea();

    public ShaderGraphPanel() {
        this.graph = createDefaultShaderGraph();
        this.scene = new AtomVisualGraphScene(graph);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        // Top Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(28, 30, 34));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("Visual ShaderGraph Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        toolbar.add(title);
        toolbar.addSeparator(new Dimension(16, 20));

        JButton addNodeBtn = new JButton("+ Add Node");
        addNodeBtn.addActionListener(e -> showAddNodeMenu(addNodeBtn));
        toolbar.add(addNodeBtn);

        JButton compileBtn = new JButton("Compile to GLSL");
        compileBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        compileBtn.setBackground(new Color(44, 93, 212));
        compileBtn.setForeground(Color.WHITE);
        compileBtn.addActionListener(e -> compileGlsl());
        toolbar.add(compileBtn);

        add(toolbar, BorderLayout.NORTH);

        // Center Split: Visual Graph Canvas (Left/Center) & GLSL Code Preview (Right)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(750);
        split.setResizeWeight(0.7);
        split.setBorder(null);

        JComponent graphView = scene.createView();
        split.setLeftComponent(new JScrollPane(graphView));

        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setBackground(new Color(22, 24, 28));
        codePanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel codeHeader = new JLabel("GLSL Shader Code Output");
        codeHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        codeHeader.setForeground(new Color(190, 195, 205));
        codePanel.add(codeHeader, BorderLayout.NORTH);

        codePreviewArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        codePreviewArea.setBackground(new Color(16, 17, 20));
        codePreviewArea.setForeground(new Color(0, 255, 180));
        codePreviewArea.setCaretColor(Color.WHITE);
        codePreviewArea.setEditable(false);
        codePanel.add(new JScrollPane(codePreviewArea), BorderLayout.CENTER);

        split.setRightComponent(codePanel);
        add(split, BorderLayout.CENTER);

        compileGlsl();
    }

    private void compileGlsl() {
        GlslShaderGraphGenerator.ShaderOutput out = GlslShaderGraphGenerator.generateGlsl(graph);
        codePreviewArea.setText(out.fragmentShader);
    }

    private void showAddNodeMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(createMenuItem("Math: Add (+)", () -> addNode("Add", "Math", 0xFFBD93F9, PinType.FLOAT, PinType.FLOAT)));
        menu.add(createMenuItem("Math: Multiply (*)", () -> addNode("Multiply", "Math", 0xFFBD93F9, PinType.FLOAT, PinType.FLOAT)));
        menu.add(createMenuItem("Math: Lerp / Blend", () -> addNode("Lerp", "Math", 0xFFBD93F9, PinType.FLOAT, PinType.FLOAT)));
        menu.addSeparator();
        menu.add(createMenuItem("Texture: Sample Texture 2D", () -> addNode("Sample Texture 2D", "Texture", 0xFF50FA7B, PinType.VEC2, PinType.VEC4_COLOR)));
        menu.add(createMenuItem("Texture: UV Tiling & Offset", () -> addNode("Tiling & Offset", "Texture", 0xFF50FA7B, PinType.VEC2, PinType.VEC2)));
        menu.addSeparator();
        menu.add(createMenuItem("Procedural: Voronoi Noise", () -> addNode("Voronoi Noise", "Procedural", 0xFFFF79C6, PinType.VEC2, PinType.FLOAT)));
        menu.add(createMenuItem("Procedural: Perlin Noise", () -> addNode("Perlin Noise", "Procedural", 0xFFFF79C6, PinType.VEC2, PinType.FLOAT)));
        menu.addSeparator();
        menu.add(createMenuItem("Input: Color Parameter", () -> addNode("Base Color", "Input", 0xFF8BE9FD, null, PinType.VEC4_COLOR)));
        menu.add(createMenuItem("Input: Time Float", () -> addNode("Time", "Input", 0xFF8BE9FD, null, PinType.FLOAT)));
        menu.show(invoker, 0, invoker.getHeight());
    }

    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    private void addNode(String title, String category, int colorRgb, PinType inType, PinType outType) {
        String id = "node_" + System.currentTimeMillis();
        NodeModel n = new NodeModel(id, title, category);
        n.headerColorRgb = colorRgb;
        n.posX = 150 + (int)(Math.random() * 200);
        n.posY = 150 + (int)(Math.random() * 200);
        if (inType != null) {
            n.addInput("inA", "A", inType);
            n.addInput("inB", "B", inType);
        }
        if (outType != null) {
            n.addOutput("out", "Out", outType);
        }
        graph.addNode(n);
        scene.addNode(n);
        scene.validate();
    }

    private static VisualGraphDocument createDefaultShaderGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("PBR_Lit_Graph", VisualGraphDocument.GraphType.SHADER_GRAPH);

        // PBR Master Node
        NodeModel master = new NodeModel("pbr_master", "PBR Master Stack", "Output");
        master.headerColorRgb = 0xFFE11D48;
        master.posX = 480;
        master.posY = 100;
        master.width = 180;
        master.height = 180;
        master.addInput("baseColor", "Base Color", PinType.VEC4_COLOR);
        master.addInput("metallic", "Metallic", PinType.FLOAT);
        master.addInput("roughness", "Roughness", PinType.FLOAT);
        master.addInput("normal", "Normal", PinType.VEC3);
        master.addInput("emission", "Emission", PinType.VEC4_COLOR);
        doc.addNode(master);

        // Texture Sampler Node
        NodeModel texNode = new NodeModel("tex_sampler", "Sample Texture 2D", "Texture");
        texNode.headerColorRgb = 0xFF50FA7B;
        texNode.posX = 120;
        texNode.posY = 80;
        texNode.addInput("uv", "UV Coord", PinType.VEC2);
        texNode.addOutput("rgba", "RGBA", PinType.VEC4_COLOR);
        texNode.addOutput("r", "R", PinType.FLOAT);
        doc.addNode(texNode);

        // Noise Node
        NodeModel noise = new NodeModel("noise_gen", "Perlin Noise", "Procedural");
        noise.headerColorRgb = 0xFFFF79C6;
        noise.posX = 120;
        noise.posY = 240;
        noise.addInput("uv", "UV Coord", PinType.VEC2);
        noise.addOutput("out", "Noise Out", PinType.FLOAT);
        doc.addNode(noise);

        // Connect texture to base color
        doc.connect("tex_sampler", "rgba", "pbr_master", "baseColor");
        doc.connect("noise_gen", "out", "pbr_master", "roughness");

        return doc;
    }
}
