package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Procedural Geometry & 3D Mesh Generation Graph Panel (Blender Geometry Nodes style).
 */
public class GeometryNodesPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;

    public GeometryNodesPanel() {
        this.graph = createDefaultGeometryGraph();
        this.scene = new AtomVisualGraphScene(graph);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(28, 30, 34));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("Procedural Geometry & Mesh Graph");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        toolbar.add(title);
        toolbar.addSeparator(new Dimension(16, 20));

        JButton addPrimBtn = new JButton("+ Mesh Primitive");
        addPrimBtn.addActionListener(e -> {
            NodeModel prim = new NodeModel("prim_" + System.currentTimeMillis(), "Grid Mesh Primitive", "Generator");
            prim.headerColorRgb = 0xFF50FA7B;
            prim.posX = 150 + (int)(Math.random() * 100);
            prim.posY = 150 + (int)(Math.random() * 100);
            prim.addInput("sizeX", "Size X", PinType.FLOAT);
            prim.addInput("sizeZ", "Size Z", PinType.FLOAT);
            prim.addOutput("mesh", "Mesh Out", PinType.GEOMETRY);
            graph.addNode(prim);
            scene.addNode(prim);
            scene.validate();
        });
        toolbar.add(addPrimBtn);

        JButton addModBtn = new JButton("+ Mesh Modifier");
        addModBtn.addActionListener(e -> {
            NodeModel mod = new NodeModel("mod_" + System.currentTimeMillis(), "Displace by Noise", "Modifier");
            mod.headerColorRgb = 0xFFFF79C6;
            mod.posX = 320;
            mod.posY = 180;
            mod.addInput("inMesh", "Geometry In", PinType.GEOMETRY);
            mod.addInput("strength", "Noise Scale", PinType.FLOAT);
            mod.addOutput("outMesh", "Geometry Out", PinType.GEOMETRY);
            graph.addNode(mod);
            scene.addNode(mod);
            scene.validate();
        });
        toolbar.add(addModBtn);

        add(toolbar, BorderLayout.NORTH);

        JComponent graphView = scene.createView();
        add(new JScrollPane(graphView), BorderLayout.CENTER);
    }

    private static VisualGraphDocument createDefaultGeometryGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Terrain_Mesh_Gen", VisualGraphDocument.GraphType.PROCEDURAL_GEOMETRY);

        NodeModel gridPrim = new NodeModel("grid_gen", "Grid Mesh Primitive", "Generator");
        gridPrim.headerColorRgb = 0xFF50FA7B;
        gridPrim.posX = 100;
        gridPrim.posY = 120;
        gridPrim.addInput("subdivX", "Subdivisions X", PinType.FLOAT);
        gridPrim.addInput("subdivZ", "Subdivisions Z", PinType.FLOAT);
        gridPrim.addOutput("mesh", "Geometry Out", PinType.GEOMETRY);
        doc.addNode(gridPrim);

        NodeModel displace = new NodeModel("noise_displace", "Displace by Perlin Noise", "Modifier");
        displace.headerColorRgb = 0xFFFF79C6;
        displace.posX = 360;
        displace.posY = 120;
        displace.addInput("inMesh", "Geometry In", PinType.GEOMETRY);
        displace.addInput("height", "Height Scale", PinType.FLOAT);
        displace.addOutput("outMesh", "Geometry Out", PinType.GEOMETRY);
        doc.addNode(displace);

        NodeModel output = new NodeModel("mesh_out", "LibGDX Model Output", "Output");
        output.headerColorRgb = 0xFF00E5FF;
        output.posX = 640;
        output.posY = 120;
        output.addInput("inMesh", "Final Geometry", PinType.GEOMETRY);
        doc.addNode(output);

        doc.connect("grid_gen", "mesh", "noise_displace", "inMesh");
        doc.connect("noise_displace", "outMesh", "mesh_out", "inMesh");

        return doc;
    }
}
