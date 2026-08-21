package com.atomgdx.theme.graph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Visual Scripting & Finite State Machine (FSM) Editor (Unreal Blueprints / PlayMaker style).
 * Features:
 * - Anti-aliased blueprint dark grid background with zero ghosting
 * - Split-column nodes with properly aligned input/output sockets
 * - Smooth Cubic Bezier Splines / Rectangular wire routing
 * - Interactive MiniMap / MiniView overlay
 * - Right-click context menus for canvas, nodes, and transitions
 * - Zoom to fit, grid snapping, and node search filtering
 * - 1-Click LibGDX Java State Machine Code Generator
 */
public class VisualScriptingPanel extends JPanel {

    private final VisualGraphDocument graph;
    private final AtomVisualGraphScene scene;
    private final GraphMiniMapPanel miniMap;
    private final JLabel statusLabel = new JLabel();

    public VisualScriptingPanel() {
        this.graph = createDefaultFsmGraph();
        this.scene = new AtomVisualGraphScene(graph);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 19, 22));

        // 1. Top Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(28, 30, 36));
        toolbar.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Visual Scripting & FSM Graph");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        toolbar.add(title);
        toolbar.addSeparator(new Dimension(14, 20));

        JButton addStateBtn = new JButton("+ Add State");
        addStateBtn.setToolTipText("Add new FSM State Node");
        addStateBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter State Name:", "New FSM State", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                NodeModel st = new NodeModel("state_" + System.currentTimeMillis(), name.trim(), "State");
                st.headerColorRgb = 0xFFFFB86C;
                st.posX = 200 + (int)(Math.random() * 120);
                st.posY = 150 + (int)(Math.random() * 120);
                st.addInput("enter", "Enter", PinType.FLOW);
                st.addOutput("exit", "Exit / Transition", PinType.FLOW);
                graph.addNode(st);
                scene.addNode(st);
                scene.validate();
                updateStatus();
            }
        });
        toolbar.add(addStateBtn);

        JButton addActionBtn = new JButton("+ Action Node");
        addActionBtn.setToolTipText("Add new Action / Logic Execution Node");
        addActionBtn.addActionListener(e -> {
            NodeModel act = new NodeModel("action_" + System.currentTimeMillis(), "Play Audio SFX", "Action");
            act.headerColorRgb = 0xFF50FA7B;
            act.posX = 250;
            act.posY = 280;
            act.addInput("exec", "Exec", PinType.FLOW);
            act.addInput("clip", "Clip Name", PinType.STRING);
            act.addOutput("then", "Then", PinType.FLOW);
            graph.addNode(act);
            scene.addNode(act);
            scene.validate();
            updateStatus();
        });
        toolbar.add(addActionBtn);
        toolbar.addSeparator(new Dimension(12, 20));

        // Wire Routing Mode Toggle
        JToggleButton routingToggle = new JToggleButton("Spline Curves (~)", true);
        routingToggle.setToolTipText("Toggle between Cubic Bezier Splines and Orthogonal Rectangular Wires");
        routingToggle.addActionListener(e -> {
            if (routingToggle.isSelected()) {
                scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.CUBIC_BEZIER_SPLINES);
                routingToggle.setText("Spline Curves (~)");
            } else {
                scene.setRoutingMode(AtomVisualGraphScene.RoutingMode.ORTHOGONAL_RECTANGULAR);
                routingToggle.setText("Rectangular (|_|)");
            }
            updateStatus();
        });
        toolbar.add(routingToggle);

        // Grid Snap Toggle
        JToggleButton snapToggle = new JToggleButton("Grid Snap (16px)", true);
        snapToggle.setToolTipText("Toggle 16px Grid Snapping");
        snapToggle.addActionListener(e -> {
            scene.setGridSnapEnabled(snapToggle.isSelected());
            snapToggle.setText(snapToggle.isSelected() ? "Grid Snap (16px)" : "Grid Snap (Off)");
        });
        toolbar.add(snapToggle);
        toolbar.addSeparator(new Dimension(12, 20));

        // Zoom Controls
        JButton zoomInBtn = new JButton("+");
        zoomInBtn.setToolTipText("Zoom In");
        zoomInBtn.addActionListener(e -> {
            scene.setZoomFactor(scene.getZoomFactor() * 1.2);
            updateStatus();
        });
        toolbar.add(zoomInBtn);

        JButton zoomOutBtn = new JButton("-");
        zoomOutBtn.setToolTipText("Zoom Out");
        zoomOutBtn.addActionListener(e -> {
            scene.setZoomFactor(scene.getZoomFactor() / 1.2);
            updateStatus();
        });
        toolbar.add(zoomOutBtn);

        JButton zoomFitBtn = new JButton("Zoom Fit [ ]");
        zoomFitBtn.setToolTipText("Zoom to Fit All Nodes");
        zoomFitBtn.addActionListener(e -> {
            scene.zoomToFit();
            updateStatus();
        });
        toolbar.add(zoomFitBtn);

        JButton zoomResetBtn = new JButton("100%");
        zoomResetBtn.setToolTipText("Reset Zoom to 100%");
        zoomResetBtn.addActionListener(e -> {
            scene.setZoomFactor(1.0);
            updateStatus();
        });
        toolbar.add(zoomResetBtn);
        toolbar.addSeparator(new Dimension(12, 20));

        // MiniMap Toggle
        JToggleButton miniMapToggle = new JToggleButton("MiniView", true);
        miniMapToggle.setToolTipText("Toggle MiniMap Overview Panel");
        toolbar.add(miniMapToggle);

        // Generate Java Code Button
        JButton genCodeBtn = new JButton("Generate Java FSM");
        genCodeBtn.setToolTipText("Generate LibGDX / Ashley ECS Java State Machine code");
        genCodeBtn.addActionListener(e -> showCodePreviewDialog());
        toolbar.add(genCodeBtn);

        // Search / Filter Field
        toolbar.add(Box.createHorizontalGlue());
        JLabel searchLbl = new JLabel("Filter: ");
        searchLbl.setForeground(new Color(180, 190, 205));
        toolbar.add(searchLbl);

        JTextField searchField = new JTextField(10);
        searchField.setMaximumSize(new Dimension(130, 24));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { scene.setSearchFilter(searchField.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { scene.setSearchFilter(searchField.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { scene.setSearchFilter(searchField.getText()); }
        });
        toolbar.add(searchField);

        add(toolbar, BorderLayout.NORTH);

        // 2. Main Graph Viewport with Floating MiniMap Overlay
        JComponent graphView = scene.createView();
        graphView.setBackground(new Color(24, 26, 31));

        JScrollPane scrollPane = new JScrollPane(graphView);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(24, 26, 31));

        // Create layered container for MiniMap overlay
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

        // 3. Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(24, 26, 30));
        statusBar.setBorder(new EmptyBorder(3, 10, 3, 10));

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(160, 170, 185));
        statusBar.add(statusLabel, BorderLayout.WEST);

        JLabel hintLabel = new JLabel("Right-click canvas to spawn nodes | Right-click nodes/wires to edit | Scroll to zoom");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLabel.setForeground(new Color(120, 130, 145));
        statusBar.add(hintLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);

        updateStatus();
    }

    private void updateStatus() {
        int nodeCount = graph.nodes.size();
        int connCount = graph.connections.size();
        int zoomPercent = (int) Math.round(scene.getZoomFactor() * 100);
        String routing = scene.getRoutingMode() == AtomVisualGraphScene.RoutingMode.CUBIC_BEZIER_SPLINES ? "Cubic Splines" : "Rectangular";
        statusLabel.setText("Nodes: " + nodeCount + " | Transitions: " + connCount + " | Routing: " + routing + " | Zoom: " + zoomPercent + "%");
    }

    private void showCodePreviewDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.neon.cosmos.ai;\n\n");
        sb.append("import com.badlogic.gdx.ai.fsm.State;\n");
        sb.append("import com.badlogic.gdx.ai.fsm.DefaultStateMachine;\n");
        sb.append("import com.badlogic.gdx.ai.msg.Telegram;\n\n");
        sb.append("/**\n * Generated LibGDX AI State Machine for ").append(graph.graphName).append("\n */\n");
        sb.append("public enum EnemyAIState implements State<EnemyEntity> {\n\n");

        for (NodeModel node : graph.nodes) {
            String enumName = node.title.toUpperCase().replaceAll("[^A-Z0-9_]+", "_");
            sb.append("    ").append(enumName).append(" {\n");
            sb.append("        @Override\n");
            sb.append("        public void enter(EnemyEntity entity) {\n");
            sb.append("            // Entering ").append(node.title).append("\n");
            sb.append("        }\n\n");
            sb.append("        @Override\n");
            sb.append("        public void update(EnemyEntity entity) {\n");
            sb.append("            // Logic execution for ").append(node.title).append("\n");
            sb.append("        }\n\n");
            sb.append("        @Override\n");
            sb.append("        public void exit(EnemyEntity entity) {\n");
            sb.append("            // Cleanup on exit\n");
            sb.append("        }\n\n");
            sb.append("        @Override\n");
            sb.append("        public boolean onMessage(EnemyEntity entity, Telegram msg) {\n");
            sb.append("            return false;\n");
            sb.append("        }\n");
            sb.append("    },\n\n");
        }
        sb.append("}\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(new Color(24, 26, 32));
        area.setForeground(new Color(220, 225, 235));
        area.setCaretColor(Color.WHITE);
        area.setEditable(false);

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(650, 450));

        JOptionPane.showMessageDialog(this, sp, "Generated LibGDX Java State Machine Code", JOptionPane.PLAIN_MESSAGE);
    }

    private static VisualGraphDocument createDefaultFsmGraph() {
        VisualGraphDocument doc = new VisualGraphDocument("Enemy_AI_FSM", VisualGraphDocument.GraphType.VISUAL_SCRIPTING_FSM);

        NodeModel idle = new NodeModel("state_idle", "State: Patrol_Idle", "State");
        idle.headerColorRgb = 0xFFFFB86C;
        idle.posX = 80;
        idle.posY = 100;
        idle.addInput("enter", "Enter", PinType.FLOW);
        idle.addOutput("onDetectPlayer", "On Detect Player", PinType.FLOW);
        doc.addNode(idle);

        NodeModel chase = new NodeModel("state_chase", "State: Chase_Target", "State");
        chase.headerColorRgb = 0xFFFF5555;
        chase.posX = 380;
        chase.posY = 100;
        chase.addInput("enter", "Enter", PinType.FLOW);
        chase.addOutput("inAttackRange", "In Attack Range", PinType.FLOW);
        chase.addOutput("lostTarget", "Lost Target", PinType.FLOW);
        doc.addNode(chase);

        NodeModel attack = new NodeModel("state_attack", "State: Melee_Attack", "State");
        attack.headerColorRgb = 0xFFBD93F9;
        attack.posX = 680;
        attack.posY = 100;
        attack.addInput("enter", "Enter", PinType.FLOW);
        attack.addOutput("onAnimDone", "On Attack Finished", PinType.FLOW);
        doc.addNode(attack);

        NodeModel action = new NodeModel("act_audio", "Play Audio SFX", "Action");
        action.headerColorRgb = 0xFF50FA7B;
        action.posX = 380;
        action.posY = 320;
        action.addInput("exec", "Exec", PinType.FLOW);
        action.addInput("clip", "Clip Name", PinType.STRING);
        action.addOutput("then", "Then", PinType.FLOW);
        doc.addNode(action);

        doc.connect("state_idle", "onDetectPlayer", "state_chase", "enter");
        doc.connect("state_chase", "inAttackRange", "state_attack", "enter");
        doc.connect("state_attack", "onAnimDone", "state_chase", "enter");
        doc.connect("state_chase", "lostTarget", "state_idle", "enter");

        return doc;
    }
}
