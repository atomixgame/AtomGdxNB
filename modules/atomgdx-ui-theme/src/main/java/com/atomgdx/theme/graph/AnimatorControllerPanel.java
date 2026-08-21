package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Animator Controller & Blend Tree State Machine Studio (Unity Animator style).
 */
public class AnimatorControllerPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;

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
        toolbar.addSeparator(new Dimension(16, 20));

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

        add(toolbar, BorderLayout.NORTH);

        JComponent graphView = scene.createView();
        add(new JScrollPane(graphView), BorderLayout.CENTER);
    }

    private static VisualGraphDocument createDefaultAnimatorGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Hero_Animator", VisualGraphDocument.GraphType.ANIMATOR_CONTROLLER);

        NodeModel entry = new NodeModel("state_entry", "▶ Entry Point", "System");
        entry.headerColorRgb = 0xFF00E5FF;
        entry.posX = 60;
        entry.posY = 140;
        entry.addOutput("default", "Default", PinType.STATE);
        doc.addNode(entry);

        NodeModel idle = new NodeModel("anim_idle", "Clip: Hero_Idle", "Animation");
        idle.headerColorRgb = 0xFF50FA7B;
        idle.posX = 260;
        idle.posY = 120;
        idle.addInput("in", "Transition In", PinType.STATE);
        idle.addOutput("out_run", "Speed > 0.1", PinType.STATE);
        idle.addOutput("out_attack", "Trigger: Attack", PinType.STATE);
        doc.addNode(idle);

        NodeModel run = new NodeModel("anim_run", "Clip: Hero_Run", "Animation");
        run.headerColorRgb = 0xFF50FA7B;
        run.posX = 520;
        run.posY = 80;
        run.addInput("in", "Transition In", PinType.STATE);
        run.addOutput("out_idle", "Speed < 0.1", PinType.STATE);
        run.addOutput("out_jump", "Trigger: Jump", PinType.STATE);
        doc.addNode(run);

        NodeModel attack = new NodeModel("anim_attack", "Clip: Hero_Slash", "Animation");
        attack.headerColorRgb = 0xFFFF5555;
        attack.posX = 420;
        attack.posY = 280;
        attack.addInput("in", "Transition In", PinType.STATE);
        attack.addOutput("out_idle", "Exit Time (100%)", PinType.STATE);
        doc.addNode(attack);

        doc.connect("state_entry", "default", "anim_idle", "in");
        doc.connect("anim_idle", "out_run", "anim_run", "in");
        doc.connect("anim_run", "out_idle", "anim_idle", "in");
        doc.connect("anim_idle", "out_attack", "anim_attack", "in");
        doc.connect("anim_attack", "out_idle", "anim_idle", "in");

        return doc;
    }
}
