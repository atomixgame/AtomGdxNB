package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Visual Scripting & Finite State Machine (FSM) Editor (Unreal Blueprints / PlayMaker style).
 */
public class VisualScriptingPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;

    public VisualScriptingPanel() {
        this.graph = createDefaultFsmGraph();
        this.scene = new AtomVisualGraphScene(graph);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(28, 30, 34));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("Visual Scripting & FSM Graph");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        toolbar.add(title);
        toolbar.addSeparator(new Dimension(16, 20));

        JButton addStateBtn = new JButton("+ Add State");
        addStateBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter State Name:", "New FSM State", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                NodeModel st = new NodeModel("state_" + System.currentTimeMillis(), name.trim(), "State");
                st.headerColorRgb = 0xFFFFB86C;
                st.posX = 200 + (int)(Math.random() * 150);
                st.posY = 150 + (int)(Math.random() * 150);
                st.addInput("in", "Enter", PinType.FLOW);
                st.addOutput("out", "Exit / Transition", PinType.FLOW);
                graph.addNode(st);
                scene.addNode(st);
                scene.validate();
            }
        });
        toolbar.add(addStateBtn);

        JButton addActionBtn = new JButton("+ Action Node");
        addActionBtn.addActionListener(e -> {
            NodeModel act = new NodeModel("action_" + System.currentTimeMillis(), "Play Audio SFX", "Action");
            act.headerColorRgb = 0xFF50FA7B;
            act.posX = 250;
            act.posY = 250;
            act.addInput("in", "Exec", PinType.FLOW);
            act.addInput("clip", "Clip Name", PinType.STRING);
            act.addOutput("out", "Then", PinType.FLOW);
            graph.addNode(act);
            scene.addNode(act);
            scene.validate();
        });
        toolbar.add(addActionBtn);

        add(toolbar, BorderLayout.NORTH);

        JComponent graphView = scene.createView();
        add(new JScrollPane(graphView), BorderLayout.CENTER);
    }

    private static VisualGraphDocument createDefaultFsmGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Enemy_AI_FSM", VisualGraphDocument.GraphType.VISUAL_SCRIPTING_FSM);

        NodeModel idle = new NodeModel("state_idle", "State: Patrol_Idle", "State");
        idle.headerColorRgb = 0xFFFFB86C;
        idle.posX = 100;
        idle.posY = 120;
        idle.addInput("enter", "Enter", PinType.FLOW);
        idle.addOutput("onDetectPlayer", "On Detect Player", PinType.FLOW);
        doc.addNode(idle);

        NodeModel chase = new NodeModel("state_chase", "State: Chase_Target", "State");
        chase.headerColorRgb = 0xFFFF5555;
        chase.posX = 380;
        chase.posY = 120;
        chase.addInput("enter", "Enter", PinType.FLOW);
        chase.addOutput("inAttackRange", "In Attack Range", PinType.FLOW);
        chase.addOutput("lostTarget", "Lost Target", PinType.FLOW);
        doc.addNode(chase);

        NodeModel attack = new NodeModel("state_attack", "State: Melee_Attack", "State");
        attack.headerColorRgb = 0xFFBD93F9;
        attack.posX = 650;
        attack.posY = 120;
        attack.addInput("enter", "Enter", PinType.FLOW);
        attack.addOutput("onAnimDone", "On Attack Finished", PinType.FLOW);
        doc.addNode(attack);

        doc.connect("state_idle", "onDetectPlayer", "state_chase", "enter");
        doc.connect("state_chase", "inAttackRange", "state_attack", "enter");
        doc.connect("state_attack", "onAnimDone", "state_chase", "enter");
        doc.connect("state_chase", "lostTarget", "state_idle", "enter");

        return doc;
    }
}
