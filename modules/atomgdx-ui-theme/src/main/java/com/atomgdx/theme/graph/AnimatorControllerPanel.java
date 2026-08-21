package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Animator Controller & Blend Tree State Machine Studio (Unity Animator style).
 * Features:
 * - Anti-aliased blueprint dark grid
 * - MiniView minimap overlay
 * - Graph Settings ⚙ dialog
 * - Auto-Layout (Sugiyama & Spring Force)
 */
public class AnimatorControllerPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;
    private final GraphMiniMapPanel miniMap;

    public AnimatorControllerPanel() {
        this.graph = createDefaultAnimatorGraph();
        this.scene = new AtomVisualGraphScene(graph);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(28, 30, 34));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("Animator Controller & Blend Tree Studio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        toolbar.add(title);
        toolbar.addSeparator(new Dimension(14, 20));

        JButton addAnimBtn = new JButton("+ Animation State");
        addAnimBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Animation Clip Name:", "New Animation State", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                NodeModel st = new NodeModel("anim_" + System.currentTimeMillis(), "Clip: " + name.trim(), "Animation");
                st.headerColorRgb = 0xFF50FA7B;
                st.posX = 220 + (int)(Math.random() * 150);
                st.posY = 160 + (int)(Math.random() * 150);
                st.addInput("transIn", "Transition In", PinType.STATE);
                st.addOutput("transOut", "Transition Out", PinType.STATE);
                graph.addNode(st);
                scene.addNode(st);
                scene.validate();
            }
        });
        toolbar.add(addAnimBtn);

        JButton addBlendBtn = new JButton("+ 2D Blend Tree");
        addBlendBtn.addActionListener(e -> {
            NodeModel bt = new NodeModel("blend_" + System.currentTimeMillis(), "2D Directional Blend Tree", "BlendSpace");
            bt.headerColorRgb = 0xFFBD93F9;
            bt.posX = 300;
            bt.posY = 200;
            bt.addInput("velX", "Velocity X", PinType.FLOAT);
            bt.addInput("velY", "Velocity Y", PinType.FLOAT);
            bt.addOutput("out", "Pose Out", PinType.STATE);
            graph.addNode(bt);
            scene.addNode(bt);
            scene.validate();
        });
        toolbar.add(addBlendBtn);
        toolbar.addSeparator(new Dimension(10, 20));

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

    private static VisualGraphDocument createDefaultAnimatorGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Spacecraft_Animator", VisualGraphDocument.GraphType.ANIMATOR_CONTROLLER);

        NodeModel idle = new NodeModel("anim_idle", "Clip: Ship_Idle", "Animation");
        idle.headerColorRgb = 0xFFFFB86C;
        idle.posX = 80;
        idle.posY = 120;
        idle.addInput("transIn", "Transition In", PinType.STATE);
        idle.addOutput("onThrust", "Speed > 0.1", PinType.STATE);
        doc.addNode(idle);

        NodeModel blendTree = new NodeModel("anim_blend", "2D Directional Flight Blend", "BlendSpace");
        blendTree.headerColorRgb = 0xFFBD93F9;
        blendTree.posX = 400;
        blendTree.posY = 120;
        blendTree.addInput("transIn", "Transition In", PinType.STATE);
        blendTree.addInput("pitch", "Pitch (-1..1)", PinType.FLOAT);
        blendTree.addInput("yaw", "Yaw (-1..1)", PinType.FLOAT);
        blendTree.addOutput("onStop", "Speed == 0", PinType.STATE);
        blendTree.addOutput("onHyper", "HyperDrive Trigger", PinType.STATE);
        doc.addNode(blendTree);

        NodeModel hyper = new NodeModel("anim_hyper", "Clip: HyperDrive_Warp", "Animation");
        hyper.headerColorRgb = 0xFFFF5555;
        hyper.posX = 720;
        hyper.posY = 120;
        hyper.addInput("transIn", "Transition In", PinType.STATE);
        hyper.addOutput("onDone", "On Warp Finished", PinType.STATE);
        doc.addNode(hyper);

        doc.connect("anim_idle", "onThrust", "anim_blend", "transIn");
        doc.connect("anim_blend", "onHyper", "anim_hyper", "transIn");
        doc.connect("anim_hyper", "onDone", "anim_idle", "transIn");
        doc.connect("anim_blend", "onStop", "anim_idle", "transIn");

        return doc;
    }
}
