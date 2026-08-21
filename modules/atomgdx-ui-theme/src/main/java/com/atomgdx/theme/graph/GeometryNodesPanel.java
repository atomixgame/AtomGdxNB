package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Procedural Geometry & Mesh Modifiers Node Studio (Blender Geometry Nodes style).
 * Features:
 * - Anti-aliased blueprint dark grid
 * - MiniView minimap overlay
 * - Graph Settings ⚙ dialog
 * - Auto-Layout (Sugiyama & Spring Force)
 */
public class GeometryNodesPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;
    private final GraphMiniMapPanel miniMap;

    public GeometryNodesPanel() {
        this.graph = createDefaultGeoNodesGraph();
        this.scene = new AtomVisualGraphScene(graph);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(28, 30, 34));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("Procedural Geometry Nodes Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        toolbar.add(title);
        toolbar.addSeparator(new Dimension(14, 20));

        JButton addNodeBtn = new JButton("+ Add Geometry Node");
        addNodeBtn.addActionListener(e -> {
            NodeModel mod = new NodeModel("geo_" + System.currentTimeMillis(), "Mesh Subdivide & Smooth", "MeshModifier");
            mod.headerColorRgb = 0xFF50FA7B;
            mod.posX = 250;
            mod.posY = 200;
            mod.addInput("inMesh", "Geometry In", PinType.GEOMETRY);
            mod.addInput("levels", "Subdivision Levels", PinType.FLOAT);
            mod.addOutput("outMesh", "Geometry Out", PinType.GEOMETRY);
            graph.addNode(mod);
            scene.addNode(mod);
            scene.validate();
        });
        toolbar.add(addNodeBtn);

        JButton layoutBtn = new JButton("⚡ Auto Layout");
        layoutBtn.addActionListener(e -> scene.autoLayoutHierarchical());
        toolbar.add(layoutBtn);

        JButton springBtn = new JButton("Organic Layout");
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

        add(toolbar, BorderLayout.NORTH);

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

        add(layeredPane, BorderLayout.CENTER);
    }

    private static VisualGraphDocument createDefaultGeoNodesGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Procedural_Asteroid", VisualGraphDocument.GraphType.PROCEDURAL_GEOMETRY);

        NodeModel prim = new NodeModel("geo_ico", "Primitive: IcoSphere", "Geometry");
        prim.headerColorRgb = 0xFF00E5FF;
        prim.posX = 80;
        prim.posY = 120;
        prim.addInput("radius", "Radius", PinType.FLOAT);
        prim.addInput("subdiv", "Subdivisions", PinType.FLOAT);
        prim.addOutput("mesh", "Mesh Out", PinType.GEOMETRY);
        doc.addNode(prim);

        NodeModel noise = new NodeModel("geo_noise", "3D Simplex Noise Displace", "Modifier");
        noise.headerColorRgb = 0xFFFF79C6;
        noise.posX = 380;
        noise.posY = 120;
        noise.addInput("inMesh", "Geometry In", PinType.GEOMETRY);
        noise.addInput("scale", "Noise Scale", PinType.FLOAT);
        noise.addInput("strength", "Displace Strength", PinType.FLOAT);
        noise.addOutput("outMesh", "Geometry Out", PinType.GEOMETRY);
        doc.addNode(noise);

        NodeModel output = new NodeModel("geo_out", "Group Geometry Output", "Output");
        output.headerColorRgb = 0xFFFF5555;
        output.posX = 680;
        output.posY = 120;
        output.addInput("geometry", "Geometry Result", PinType.GEOMETRY);
        doc.addNode(output);

        doc.connect("geo_ico", "mesh", "geo_noise", "inMesh");
        doc.connect("geo_noise", "outMesh", "geo_out", "geometry");

        return doc;
    }
}
