package com.atomgdx.theme.graph;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

/**
 * NetBeans Visual Library GraphScene powering Visual Scripting, FSM, ShaderGraph, Animator, and Geometry Nodes.
 * Features:
 * - Anti-aliased blueprint dark grid background (no trail/ghosting artifacts)
 * - Clean dual-column Node layout with separated input/output ports
 * - Smooth Cubic Bezier Spline edge routing with glowing flow wires and arrow heads
 * - Interactive MiniMap (MiniView) overview
 * - Right-click context menus for canvas, nodes, and connection wires
 * - Zoom to fit, grid snapping, and node search filtering
 */
public class AtomVisualGraphScene extends GraphScene<NodeModel, ConnectionModel> {

    public enum RoutingMode {
        CUBIC_BEZIER_SPLINES,
        ORTHOGONAL_RECTANGULAR,
        DIRECT_LINES
    }

    private final VisualGraphDocument document;
    private final LayerWidget backgroundLayer = new LayerWidget(this);
    private final LayerWidget mainLayer = new LayerWidget(this);
    private final LayerWidget connectionLayer = new LayerWidget(this);
    private final LayerWidget interactionLayer = new LayerWidget(this);

    private final Map<String, Widget> pinWidgets = new HashMap<>();
    private final Map<NodeModel, Widget> nodeWidgetsMap = new HashMap<>();
    private RoutingMode routingMode = RoutingMode.CUBIC_BEZIER_SPLINES;
    private boolean gridSnapEnabled = true;
    private int gridSnapSize = 16;
    private String searchFilter = "";

    public AtomVisualGraphScene(VisualGraphDocument document) {
        this.document = document != null ? document : new VisualGraphDocument();

        setBackground(new Color(24, 26, 31));

        addChild(backgroundLayer);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);

        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createMouseCenteredZoomAction(1.15));

        // Right-click on empty canvas opens Node Spawner Menu
        getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
            @Override
            public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
                return createCanvasContextMenu(localLocation);
            }
        }));

        loadFromDocument();
    }

    public VisualGraphDocument getDocument() {
        return document;
    }

    public RoutingMode getRoutingMode() {
        return routingMode;
    }

    public void setRoutingMode(RoutingMode mode) {
        this.routingMode = mode;
        for (ConnectionModel conn : document.connections) {
            Widget w = findWidget(conn);
            if (w != null) {
                w.revalidate();
                w.repaint();
            }
        }
        validate();
        repaint();
    }

    public boolean isGridSnapEnabled() {
        return gridSnapEnabled;
    }

    public void setGridSnapEnabled(boolean enabled) {
        this.gridSnapEnabled = enabled;
    }

    public void setSearchFilter(String filter) {
        this.searchFilter = filter != null ? filter.trim().toLowerCase() : "";
        for (Map.Entry<NodeModel, Widget> entry : nodeWidgetsMap.entrySet()) {
            NodeModel node = entry.getKey();
            Widget widget = entry.getValue();
            boolean match = searchFilter.isEmpty() || node.title.toLowerCase().contains(searchFilter) || node.category.toLowerCase().contains(searchFilter);
            widget.setVisible(match);
        }
        repaint();
    }

    public void zoomToFit() {
        Rectangle bounds = getDocumentBounds();
        if (bounds.isEmpty() || bounds.width <= 0 || bounds.height <= 0) return;

        JComponent view = getView();
        if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
            double scaleX = (double)(view.getWidth() - 100) / (double)bounds.width;
            double scaleY = (double)(view.getHeight() - 100) / (double)bounds.height;
            double zoom = Math.min(1.5, Math.max(0.2, Math.min(scaleX, scaleY)));
            setZoomFactor(zoom);
            panToLocation(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        }
    }

    public void panToLocation(int sceneX, int sceneY) {
        JComponent view = getView();
        if (view != null && view.getParent() instanceof JViewport) {
            JViewport vp = (JViewport) view.getParent();
            int viewX = (int)(sceneX * getZoomFactor() - vp.getWidth() / 2.0);
            int viewY = (int)(sceneY * getZoomFactor() - vp.getHeight() / 2.0);
            vp.setViewPosition(new Point(Math.max(0, viewX), Math.max(0, viewY)));
        }
    }

    public Rectangle getDocumentBounds() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        if (document.nodes.isEmpty()) return new Rectangle(0, 0, 800, 600);

        for (NodeModel node : document.nodes) {
            minX = Math.min(minX, node.posX);
            minY = Math.min(minY, node.posY);
            maxX = Math.max(maxX, node.posX + Math.max(220, node.width));
            maxY = Math.max(maxY, node.posY + Math.max(140, node.height));
        }
        return new Rectangle(minX, minY, Math.max(100, maxX - minX), Math.max(100, maxY - minY));
    }

    @Override
    protected void paintChildren() {
        Graphics2D g2 = getGraphics();
        if (g2 != null) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Fill crisp solid canvas background
            Rectangle clip = g2.getClipBounds();
            if (clip == null) clip = new Rectangle(0, 0, 5000, 5000);
            g2.setColor(new Color(24, 26, 31));
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);

            // Draw Blueprint Grid
            int gridSize = 16;
            int majorGrid = 64;

            // Minor grid dots
            g2.setColor(new Color(36, 39, 48));
            int startX = (clip.x / gridSize) * gridSize;
            int startY = (clip.y / gridSize) * gridSize;
            int endX = clip.x + clip.width;
            int endY = clip.y + clip.height;

            for (int x = startX; x <= endX; x += gridSize) {
                for (int y = startY; y <= endY; y += gridSize) {
                    if (x % majorGrid == 0 && y % majorGrid == 0) {
                        g2.setColor(new Color(48, 54, 66));
                        g2.fillRect(x - 1, y - 1, 3, 3);
                        g2.setColor(new Color(36, 39, 48));
                    } else {
                        g2.fillRect(x, y, 1, 1);
                    }
                }
            }
        }
        super.paintChildren();
    }

    public void loadFromDocument() {
        for (NodeModel node : document.nodes) {
            addNode(node);
        }
        for (ConnectionModel conn : document.connections) {
            addEdge(conn);
        }
    }

    @Override
    protected Widget attachNodeWidget(NodeModel node) {
        int nodeWidth = Math.max(220, node.width);

        Widget nodeWidget = new Widget(this) {
            @Override
            protected void paintWidget() {
                Graphics2D g = getGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle bounds = getClientArea();

                // Outer glow / shadow
                g.setColor(new Color(0, 0, 0, 60));
                g.fillRoundRect(bounds.x + 2, bounds.y + 3, bounds.width - 4, bounds.height - 4, 10, 10);

                // Body background
                g.setColor(new Color(33, 36, 44));
                g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);

                // Border
                ObjectState state = getState();
                if (state.isSelected() || state.isHovered()) {
                    g.setColor(new Color(0, 229, 255));
                    g.setStroke(new BasicStroke(2.0f));
                } else {
                    g.setColor(new Color(48, 54, 66));
                    g.setStroke(new BasicStroke(1.2f));
                }
                g.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 8, 8);
            }
        };

        nodeWidget.setLayout(LayoutFactory.createVerticalFlowLayout());
        nodeWidget.setPreferredLocation(new Point(node.posX, node.posY));

        // 1. Header Bar with Gradient & Icon Badge
        Widget header = new Widget(this) {
            @Override
            protected void paintWidget() {
                Graphics2D g = getGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle b = getClientArea();
                Color c1 = new Color(node.headerColorRgb);
                Color c2 = new Color(Math.max(0, c1.getRed() - 40), Math.max(0, c1.getGreen() - 40), Math.max(0, c1.getBlue() - 40));
                GradientPaint gp = new GradientPaint(b.x, b.y, c1, b.x, b.y + b.height, c2);
                g.setPaint(gp);
                g.fillRoundRect(b.x, b.y, b.width, b.height + 4, 8, 8);
                g.fillRect(b.x, b.y + b.height - 4, b.width, 4);

                // Bottom divider line
                g.setColor(new Color(20, 22, 28, 180));
                g.drawLine(b.x, b.y + b.height - 1, b.x + b.width, b.y + b.height - 1);
            }
        };

        header.setLayout(LayoutFactory.createHorizontalFlowLayout());
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        LabelWidget title = new LabelWidget(this, node.title);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(Color.WHITE);
        header.addChild(title);
        nodeWidget.addChild(header);

        // 2. Body Container with Dual Columns (Left Inputs, Right Outputs)
        Widget body = new Widget(this);
        body.setLayout(LayoutFactory.createHorizontalFlowLayout());
        body.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));

        // Left Column (Inputs)
        Widget inputCol = new Widget(this);
        inputCol.setLayout(LayoutFactory.createVerticalFlowLayout());
        inputCol.setPreferredBounds(new Rectangle(0, 0, nodeWidth / 2 - 8, 20));

        for (PinModel pin : node.inputPins) {
            Widget pinW = createPinWidget(node, pin, nodeWidth / 2 - 8);
            inputCol.addChild(pinW);
            pinWidgets.put(node.nodeId + ":" + pin.pinId, pinW);
        }
        body.addChild(inputCol);

        // Right Column (Outputs)
        Widget outputCol = new Widget(this);
        outputCol.setLayout(LayoutFactory.createVerticalFlowLayout());
        outputCol.setPreferredBounds(new Rectangle(0, 0, nodeWidth / 2 - 8, 20));

        for (PinModel pin : node.outputPins) {
            Widget pinW = createPinWidget(node, pin, nodeWidth / 2 - 8);
            outputCol.addChild(pinW);
            pinWidgets.put(node.nodeId + ":" + pin.pinId, pinW);
        }
        body.addChild(outputCol);

        nodeWidget.addChild(body);

        // Node Drag & Movement Actions with Grid Snapping
        nodeWidget.getActions().addAction(ActionFactory.createMoveAction((w, cur, sug) -> {
            if (gridSnapEnabled) {
                sug.x = Math.round((float) sug.x / gridSnapSize) * gridSnapSize;
                sug.y = Math.round((float) sug.y / gridSnapSize) * gridSnapSize;
            }
            node.posX = sug.x;
            node.posY = sug.y;
            return sug;
        }, null));

        nodeWidget.getActions().addAction(ActionFactory.createSelectAction(new SelectProvider() {
            @Override public boolean isAimingAllowed(Widget w, Point p, boolean inv) { return true; }
            @Override public boolean isSelectionAllowed(Widget w, Point p, boolean inv) { return true; }
            @Override public void select(Widget w, Point p, boolean inv) { w.bringToFront(); }
        }));

        // Node Right-Click Context Menu
        nodeWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
            @Override
            public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
                return createNodeContextMenu(node);
            }
        }));

        mainLayer.addChild(nodeWidget);
        nodeWidgetsMap.put(node, nodeWidget);
        return nodeWidget;
    }

    private Widget createPinWidget(NodeModel node, PinModel pin, int colWidth) {
        Widget pinContainer = new Widget(this);
        pinContainer.setLayout(LayoutFactory.createHorizontalFlowLayout());
        pinContainer.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));

        // Socket Icon Widget (Diamond for Flow, Circle with dot for Data)
        Widget socketDot = new Widget(this) {
            @Override
            protected void paintWidget() {
                Graphics2D g = getGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle b = getClientArea();

                int size = 10;
                int ox = b.x + (b.width - size) / 2;
                int oy = b.y + (b.height - size) / 2;

                Color col = new Color(pin.type.getColorRgb());

                if (pin.type == PinType.FLOW || pin.type == PinType.STATE) {
                    // Exec Chevron / Diamond socket
                    Path2D path = new Path2D.Float();
                    path.moveTo(ox, oy + size / 2.0);
                    path.lineTo(ox + size / 2.0, oy);
                    path.lineTo(ox + size, oy + size / 2.0);
                    path.lineTo(ox + size / 2.0, oy + size);
                    path.closePath();

                    g.setColor(col);
                    g.fill(path);
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(1.2f));
                    g.draw(path);
                } else {
                    // Data Circle socket
                    g.setColor(col);
                    g.fillOval(ox, oy, size, size);
                    g.setColor(new Color(255, 255, 255, 220));
                    g.fillOval(ox + 3, oy + 3, 4, 4);
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(1.2f));
                    g.drawOval(ox, oy, size, size);
                }
            }
        };
        socketDot.setPreferredBounds(new Rectangle(0, 0, 14, 14));

        LabelWidget label = new LabelWidget(this, pin.name);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(215, 220, 230));

        if (pin.isInput) {
            pinContainer.addChild(socketDot);
            pinContainer.addChild(label);
        } else {
            pinContainer.addChild(label);
            pinContainer.addChild(socketDot);
        }

        return pinContainer;
    }

    @Override
    protected Widget attachEdgeWidget(ConnectionModel edge) {
        ConnectionWidget connWidget = new ConnectionWidget(this) {
            @Override
            protected void paintWidget() {
                if (routingMode == RoutingMode.CUBIC_BEZIER_SPLINES) {
                    Graphics2D g = getGraphics();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Point src = getFirstControlPoint();
                    Point tgt = getLastControlPoint();
                    if (src == null || tgt == null) return;

                    int dx = Math.abs(tgt.x - src.x);
                    int offset = Math.max(40, dx / 2);

                    int c1x = src.x + offset;
                    int c1y = src.y;
                    int c2x = tgt.x - offset;
                    int c2y = tgt.y;

                    CubicCurve2D curve = new CubicCurve2D.Float(src.x, src.y, c1x, c1y, c2x, c2y, tgt.x, tgt.y);

                    // Outer Glow Shadow
                    g.setColor(new Color(0, 229, 255, 50));
                    g.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(curve);

                    // Main Glow Wire
                    g.setColor(new Color(0, 229, 255));
                    g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(curve);

                    // Core bright line
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(curve);

                    // Target Arrow Head
                    int arrowSize = 6;
                    Polygon arrow = new Polygon();
                    arrow.addPoint(tgt.x, tgt.y);
                    arrow.addPoint(tgt.x - arrowSize * 2, tgt.y - arrowSize);
                    arrow.addPoint(tgt.x - arrowSize * 2, tgt.y + arrowSize);
                    g.setColor(Color.WHITE);
                    g.fillPolygon(arrow);
                } else {
                    super.paintWidget();
                }
            }
        };

        connWidget.setLineColor(new Color(0, 229, 255));
        connWidget.setStroke(new BasicStroke(2.5f));
        connWidget.setRouter(routingMode == RoutingMode.ORTHOGONAL_RECTANGULAR ? 
                RouterFactory.createOrthogonalSearchRouter(mainLayer) : RouterFactory.createDirectRouter());

        Widget srcPin = pinWidgets.get(edge.sourceNodeId + ":" + edge.sourcePinId);
        Widget tgtPin = pinWidgets.get(edge.targetNodeId + ":" + edge.targetPinId);

        if (srcPin != null) connWidget.setSourceAnchor(AnchorFactory.createRectangularAnchor(srcPin));
        if (tgtPin != null) connWidget.setTargetAnchor(AnchorFactory.createRectangularAnchor(tgtPin));

        // Wire Right-Click Context Menu
        connWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
            @Override
            public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem del = new JMenuItem("Delete Transition Wire");
                del.addActionListener(e -> {
                    document.connections.remove(edge);
                    removeEdge(edge);
                    validate();
                    repaint();
                });
                menu.add(del);
                return menu;
            }
        }));

        connectionLayer.addChild(connWidget);
        return connWidget;
    }

    @Override
    protected void attachEdgeSourceAnchor(ConnectionModel edge, NodeModel oldSource, NodeModel newSource) {
        Widget w = pinWidgets.get(edge.sourceNodeId + ":" + edge.sourcePinId);
        if (w != null) {
            ConnectionWidget cw = (ConnectionWidget) findWidget(edge);
            if (cw != null) cw.setSourceAnchor(AnchorFactory.createRectangularAnchor(w));
        }
    }

    @Override
    protected void attachEdgeTargetAnchor(ConnectionModel edge, NodeModel oldTarget, NodeModel newTarget) {
        Widget w = pinWidgets.get(edge.targetNodeId + ":" + edge.targetPinId);
        if (w != null) {
            ConnectionWidget cw = (ConnectionWidget) findWidget(edge);
            if (cw != null) cw.setTargetAnchor(AnchorFactory.createRectangularAnchor(w));
        }
    }

    private JPopupMenu createCanvasContextMenu(Point loc) {
        JPopupMenu popup = new JPopupMenu();

        JMenu stateMenu = new JMenu("State Nodes (FSM)");
        addCreationItem(stateMenu, "Patrol / Idle State", "State", 0xFFFFB86C, loc, "enter", "onDetectPlayer");
        addCreationItem(stateMenu, "Chase Target State", "State", 0xFFFF5555, loc, "enter", "inAttackRange", "lostTarget");
        addCreationItem(stateMenu, "Melee Attack State", "State", 0xFFBD93F9, loc, "enter", "onAttackDone");
        addCreationItem(stateMenu, "Flee / Evade State", "State", 0xFF8BE9FD, loc, "enter", "onSafe");
        popup.add(stateMenu);

        JMenu actionMenu = new JMenu("Action & Blueprint Nodes");
        addCreationItem(actionMenu, "Play Audio SFX", "Action", 0xFF50FA7B, loc, "Exec", "Then");
        addCreationItem(actionMenu, "Spawn Entity Prefab", "Action", 0xFF50FA7B, loc, "Exec", "Spawned");
        addCreationItem(actionMenu, "Apply Physics Force 2D", "Action", 0xFF50FA7B, loc, "Exec", "Done");
        addCreationItem(actionMenu, "Set Blackboard Variable", "Action", 0xFF50FA7B, loc, "Exec", "Done");
        addCreationItem(actionMenu, "Log Console Message", "Action", 0xFF50FA7B, loc, "Exec", "Done");
        popup.add(actionMenu);

        JMenu flowMenu = new JMenu("Flow Control");
        addCreationItem(flowMenu, "Branch (If / Else)", "Flow", 0xFFF1FA8C, loc, "Exec", "True", "False");
        addCreationItem(flowMenu, "Delay Timer", "Flow", 0xFFF1FA8C, loc, "Exec", "Completed");
        addCreationItem(flowMenu, "Sequence", "Flow", 0xFFF1FA8C, loc, "Exec", "Then 0", "Then 1");
        popup.add(flowMenu);

        JMenu mathMenu = new JMenu("Math & Logic");
        addCreationItem(mathMenu, "Float Add (+)", "Math", 0xFF8BE9FD, loc, "A,B", "Result");
        addCreationItem(mathMenu, "Float Multiply (*)", "Math", 0xFF8BE9FD, loc, "A,B", "Result");
        addCreationItem(mathMenu, "Compare Values (==, >)", "Math", 0xFF8BE9FD, loc, "A,B", "True", "False");
        addCreationItem(mathMenu, "Distance to Target", "Math", 0xFF8BE9FD, loc, "PosA,PosB", "Distance");
        popup.add(mathMenu);

        popup.addSeparator();

        JMenuItem fitItem = new JMenuItem("Zoom to Fit All Nodes");
        fitItem.addActionListener(e -> zoomToFit());
        popup.add(fitItem);

        JMenuItem resetZoom = new JMenuItem("Reset Zoom (100%)");
        resetZoom.addActionListener(e -> setZoomFactor(1.0));
        popup.add(resetZoom);

        return popup;
    }

    private void addCreationItem(JMenu parent, String name, String category, int headerColor, Point loc, String inputs, String... outputs) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> {
            NodeModel node = new NodeModel("node_" + System.currentTimeMillis(), name, category);
            node.headerColorRgb = headerColor;
            node.posX = loc != null ? loc.x : 200;
            node.posY = loc != null ? loc.y : 200;

            if (inputs != null && !inputs.isEmpty()) {
                for (String inp : inputs.split(",")) {
                    node.addInput(inp.trim().toLowerCase().replaceAll("\\s+", "_"), inp.trim(), PinType.FLOW);
                }
            }
            if (outputs != null) {
                for (String out : outputs) {
                    node.addOutput(out.trim().toLowerCase().replaceAll("\\s+", "_"), out.trim(), PinType.FLOW);
                }
            }

            document.addNode(node);
            addNode(node);
            validate();
            repaint();
        });
        parent.add(item);
    }

    private JPopupMenu createNodeContextMenu(NodeModel node) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem renameItem = new JMenuItem("Rename Node");
        renameItem.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(getView(), "Enter new title:", node.title);
            if (newTitle != null && !newTitle.isBlank()) {
                node.title = newTitle.trim();
                Widget w = findWidget(node);
                if (w != null) {
                    removeNode(node);
                    addNode(node);
                    validate();
                    repaint();
                }
            }
        });
        menu.add(renameItem);

        JMenuItem dupItem = new JMenuItem("Duplicate Node (Ctrl+D)");
        dupItem.addActionListener(e -> {
            NodeModel dup = new NodeModel("node_" + System.currentTimeMillis(), node.title + "_Copy", node.category);
            dup.headerColorRgb = node.headerColorRgb;
            dup.posX = node.posX + 30;
            dup.posY = node.posY + 30;
            for (PinModel p : node.inputPins) dup.addInput(p.pinId, p.name, p.type);
            for (PinModel p : node.outputPins) dup.addOutput(p.pinId, p.name, p.type);
            document.addNode(dup);
            addNode(dup);
            validate();
            repaint();
        });
        menu.add(dupItem);

        JMenuItem addOutPin = new JMenuItem("Add Transition Output Pin");
        addOutPin.addActionListener(e -> {
            String pinName = JOptionPane.showInputDialog(getView(), "Enter Output Transition Name:", "On Transition");
            if (pinName != null && !pinName.isBlank()) {
                node.addOutput(pinName.trim().toLowerCase().replaceAll("\\s+", "_"), pinName.trim(), PinType.FLOW);
                removeNode(node);
                addNode(node);
                validate();
                repaint();
            }
        });
        menu.add(addOutPin);

        menu.addSeparator();

        JMenuItem delItem = new JMenuItem("Delete Node (Del)");
        delItem.addActionListener(e -> {
            document.nodes.remove(node);
            removeNode(node);
            validate();
            repaint();
        });
        menu.add(delItem);

        return menu;
    }
}
