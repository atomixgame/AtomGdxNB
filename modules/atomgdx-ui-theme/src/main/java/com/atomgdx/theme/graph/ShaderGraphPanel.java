package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Unity ShaderGraph / Blender style visual node shader editor panel.
 * Features:
 * - Anti-aliased blueprint dark grid
 * - Live GLSL vertex & fragment shader code compilation
 * - MiniView minimap overlay
 * - Graph Settings ⚙ dialog
 * - Auto-Layout (Sugiyama & Spring Force)
 */
public class ShaderGraphPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;
    private final JTextArea codePreviewArea = new JTextArea();
    private final GraphMiniMapPanel miniMap;

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
        toolbar.addSeparator(new Dimension(14, 20));

        JButton addNodeBtn = new JButton("+ Add Node");
        addNodeBtn.addActionListener(e -> showAddNodeMenu(addNodeBtn));
        toolbar.add(addNodeBtn);

        JButton layoutBtn = new JButton("⚡ Auto Layout");
        layoutBtn.setToolTipText("Auto-arrange nodes hierarchically (Left-to-Right DAG)");
        layoutBtn.addActionListener(e -> scene.autoLayoutHierarchical());
        toolbar.add(layoutBtn);

        JButton springBtn = new JButton("Organic Layout");
        springBtn.setToolTipText("Auto-balance nodes using Force-Directed Spring simulation");
        springBtn.addActionListener(e -> scene.autoLayoutSpringForce());
        toolbar.add(springBtn);
        toolbar.addSeparator(new Dimension(10, 20));

        JToggleButton routingToggle = new JToggleButton("Spline Curves (~)", true);
        routingToggle.addActionListener(e -> {
            if (routingToggle.isSelected()) {
                scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.CUBIC_BEZIER_SPLINES);
                routingToggle.setText("Spline Curves (~)");
            } else {
                scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.ORTHOGONAL_RECTANGULAR);
                routingToggle.setText("Rectangular (|_|)");
            }
        });
        toolbar.add(routingToggle);

        JButton settingsBtn = new JButton("Settings ⚙");
        settingsBtn.addActionListener(e -> {
            Frame frame = JOptionPane.getFrameForComponent(this);
            GraphSettingsDialog dialog = new GraphSettingsDialog(frame, scene, null);
            dialog.setVisible(true);
        });
        toolbar.add(settingsBtn);
        toolbar.addSeparator(new Dimension(10, 20));

        // Zoom Controls
        JButton zoomInBtn = new JButton("+");
        zoomInBtn.addActionListener(e -> scene.setZoomFactor(scene.getZoomFactor() * 1.2));
        toolbar.add(zoomInBtn);

        JButton zoomOutBtn = new JButton("-");
        zoomOutBtn.addActionListener(e -> scene.setZoomFactor(scene.getZoomFactor() / 1.2));
        toolbar.add(zoomOutBtn);

        JButton zoomFitBtn = new JButton("Zoom Fit [ ]");
        zoomFitBtn.addActionListener(e -> scene.zoomToFit());
        toolbar.add(zoomFitBtn);

        JButton zoomResetBtn = new JButton("100%");
        zoomResetBtn.addActionListener(e -> scene.setZoomFactor(1.0));
        toolbar.add(zoomResetBtn);
        toolbar.addSeparator(new Dimension(10, 20));

        JToggleButton miniMapToggle = new JToggleButton("MiniView", true);
        toolbar.add(miniMapToggle);

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
        graphView.setBackground(new Color(24, 26, 31));

        JScrollPane scrollPane = new JScrollPane(graphView);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(24, 26, 31));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        miniMap = new GraphMiniMapPanel(scene, scrollPane);
        miniMapToggle.addActionListener(e -> miniMap.setVisible(miniMapToggle.isSelected()));

        JPanel miniMapWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        miniMapWrapper.setOpaque(false);
        miniMapWrapper.add(miniMap);

        layeredPane.add(miniMapWrapper, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        split.setLeftComponent(layeredPane);

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
        NodeModel node = new NodeModel("node_" + System.currentTimeMillis(), title, category);
        node.headerColorRgb = colorRgb;
        node.posX = 150 + (int)(Math.random() * 200);
        node.posY = 120 + (int)(Math.random() * 200);
        if (inType != null) {
            node.addInput("inA", "A", inType);
            node.addInput("inB", "B", inType);
        }
        if (outType != null) {
            node.addOutput("out", "Out", outType);
        }
        graph.addNode(node);
        scene.addNode(node);
        scene.validate();
        compileGlsl();
    }

    private static VisualGraphDocument createDefaultShaderGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Hologram_Shield_Shader", VisualGraphDocument.GraphType.SHADER_GRAPH);

        NodeModel texNode = new NodeModel("node_tex", "Sample Texture 2D", "Texture");
        texNode.headerColorRgb = 0xFF50FA7B;
        texNode.posX = 80;
        texNode.posY = 100;
        texNode.addInput("uv", "UV", PinType.VEC2);
        texNode.addOutput("rgba", "RGBA", PinType.VEC4_COLOR);
        doc.addNode(texNode);

        NodeModel noiseNode = new NodeModel("node_noise", "Voronoi Noise", "Procedural");
        noiseNode.headerColorRgb = 0xFFFF79C6;
        noiseNode.posX = 80;
        noiseNode.posY = 280;
        noiseNode.addInput("uv", "UV", PinType.VEC2);
        noiseNode.addOutput("out", "Out", PinType.FLOAT);
        doc.addNode(noiseNode);

        NodeModel mulNode = new NodeModel("node_mul", "Multiply (*)", "Math");
        mulNode.headerColorRgb = 0xFFBD93F9;
        mulNode.posX = 380;
        mulNode.posY = 140;
        mulNode.addInput("inA", "Color A", PinType.VEC4_COLOR);
        mulNode.addInput("inB", "Factor B", PinType.FLOAT);
        mulNode.addOutput("out", "Out", PinType.VEC4_COLOR);
        doc.addNode(mulNode);

        NodeModel pbrMaster = new NodeModel("node_master", "PBR Master Output", "Master");
        pbrMaster.headerColorRgb = 0xFFFF5555;
        pbrMaster.posX = 680;
        pbrMaster.posY = 120;
        pbrMaster.addInput("albedo", "Albedo", PinType.VEC4_COLOR);
        pbrMaster.addInput("metallic", "Metallic", PinType.FLOAT);
        pbrMaster.addInput("roughness", "Roughness", PinType.FLOAT);
        pbrMaster.addInput("emission", "Emission", PinType.VEC4_COLOR);
        doc.addNode(pbrMaster);

        doc.connect("node_tex", "rgba", "node_mul", "inA");
        doc.connect("node_noise", "out", "node_mul", "inB");
        doc.connect("node_mul", "out", "node_master", "albedo");

        return doc;
    }
}
