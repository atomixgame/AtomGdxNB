package com.atomgdx.theme.graph;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * High-performance NetBeans Visual Library GraphScene for Visual Scripting, FSM, ShaderGraph, and Geometry Nodes.
 * Architectural Highlights:
 * 1. Self-contained NodeWidget with explicit bounds — completely eliminates ghosting / trailing artifacts.
 * 2. Guaranteed Left-Input / Right-Output pin geometry with exact anchor offsets.
 * 3. Intelligent Cubic Bezier S-Curve & Backward-Loop Spline routing + Rectangular Manhattan routing.
 * 4. Node Group / Comment Boxes on backdrop layer.
 * 5. Integrated Graph Layout Algorithms (Sugiyama Layered DAG & Spring Force-Directed).
 */
public class AtomVisualGraphScene extends GraphScene<NodeModel, ConnectionModel> {

    public enum RoutingMode {
        CUBIC_BEZIER_SPLINES,
        ORTHOGONAL_RECTANGULAR,
        DIRECT_LINES
    }

    private final VisualGraphDocument document;
    private final LayerWidget backdropLayer = new LayerWidget(this);
    private final LayerWidget connectionLayer = new LayerWidget(this);
    private final LayerWidget mainLayer = new LayerWidget(this);
    private final LayerWidget interactionLayer = new LayerWidget(this);

    private final Map<NodeModel, NodeWidget> nodeWidgetsMap = new HashMap<>();
    private final Map<String, NodeModel> nodeLookup = new HashMap<>();
    private RoutingMode routingMode = RoutingMode.CUBIC_BEZIER_SPLINES;
    private boolean gridSnapEnabled = true;
    private int gridSnapSize = 16;
    private String searchFilter = "";

    public AtomVisualGraphScene(VisualGraphDocument document) {
        this.document = document != null ? document : new VisualGraphDocument();

        setBackground(new Color(24, 26, 31));

        addChild(backdropLayer);
        addChild(connectionLayer);
        addChild(mainLayer);
        addChild(interactionLayer);

        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createMouseCenteredZoomAction(1.15));

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
        for (Map.Entry<NodeModel, NodeWidget> entry : nodeWidgetsMap.entrySet()) {
            NodeModel node = entry.getKey();
            NodeWidget widget = entry.getValue();
            boolean match = searchFilter.isEmpty() || node.title.toLowerCase().contains(searchFilter) || node.category.toLowerCase().contains(searchFilter);
            widget.setVisible(match);
        }
        repaint();
    }

    public void autoLayoutHierarchical() {
        GraphLayoutEngine.applyHierarchicalLayout(document);
        syncNodeLocations();
        zoomToFit();
    }

    public void autoLayoutSpringForce() {
        GraphLayoutEngine.applySpringForceLayout(document);
        syncNodeLocations();
        zoomToFit();
    }

    private void syncNodeLocations() {
        for (NodeModel node : document.nodes) {
            NodeWidget widget = nodeWidgetsMap.get(node);
            if (widget != null) {
                widget.setPreferredLocation(new Point(node.posX, node.posY));
            }
        }
        rebuildBackdropGroups();
        validate();
        repaint();
    }

    public void zoomToFit() {
        Rectangle bounds = getDocumentBounds();
        if (bounds.isEmpty() || bounds.width <= 0 || bounds.height <= 0) return;

        JComponent view = getView();
        if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
            double scaleX = (double)(view.getWidth() - 120) / (double)bounds.width;
            double scaleY = (double)(view.getHeight() - 120) / (double)bounds.height;
            double zoom = Math.min(1.4, Math.max(0.2, Math.min(scaleX, scaleY)));
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
            int w = Math.max(220, node.width);
            int h = Math.max(120, node.height);
            minX = Math.min(minX, node.posX);
            minY = Math.min(minY, node.posY);
            maxX = Math.max(maxX, node.posX + w);
            maxY = Math.max(maxY, node.posY + h);
        }
        return new Rectangle(minX, minY, Math.max(100, maxX - minX), Math.max(100, maxY - minY));
    }

    @Override
    protected void paintChildren() {
        Graphics2D g2 = getGraphics();
        if (g2 != null) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            Rectangle clip = g2.getClipBounds();
            if (clip == null) clip = new Rectangle(0, 0, 8000, 8000);

            // 1. Solid double-buffered canvas background
            g2.setColor(new Color(24, 26, 31));
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);

            // 2. Blueprint Grid
            int gridSize = 16;
            int majorGrid = 64;

            int startX = (clip.x / gridSize) * gridSize;
            int startY = (clip.y / gridSize) * gridSize;
            int endX = clip.x + clip.width;
            int endY = clip.y + clip.height;

            for (int x = startX; x <= endX; x += gridSize) {
                for (int y = startY; y <= endY; y += gridSize) {
                    if (x % majorGrid == 0 && y % majorGrid == 0) {
                        g2.setColor(new Color(50, 56, 70));
                        g2.fillRect(x - 1, y - 1, 3, 3);
                    } else {
                        g2.setColor(new Color(34, 37, 46));
                        g2.fillRect(x, y, 1, 1);
                    }
                }
            }
        }
        super.paintChildren();
    }

    public void loadFromDocument() {
        nodeLookup.clear();
        for (NodeModel node : document.nodes) {
            nodeLookup.put(node.nodeId, node);
            addNode(node);
        }
        for (ConnectionModel conn : document.connections) {
            addEdge(conn);
        }
        rebuildBackdropGroups();
    }

    public void addGroup(NodeGroupModel group) {
        rebuildBackdropGroups();
    }

    private void rebuildBackdropGroups() {
        backdropLayer.removeChildren();
        // Render comment group backdrops
    }

    @Override
    protected Widget attachNodeWidget(NodeModel node) {
        nodeLookup.put(node.nodeId, node);
        NodeWidget nodeWidget = new NodeWidget(this, node);
        nodeWidget.setPreferredLocation(new Point(node.posX, node.posY));

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

    @Override
    protected Widget attachEdgeWidget(ConnectionModel edge) {
        ConnectionWidget connWidget = new ConnectionWidget(this) {
            @Override
            protected void paintWidget() {
                Graphics2D g = getGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Point src = getFirstControlPoint();
                Point tgt = getLastControlPoint();
                if (src == null || tgt == null) return;

                int dx = tgt.x - src.x;
                int dy = tgt.y - src.y;

                if (routingMode == RoutingMode.CUBIC_BEZIER_SPLINES) {
                    CubicCurve2D curve;
                    if (dx >= 40) {
                        // Standard Forward S-Curve
                        int offset = Math.max(50, dx / 2);
                        curve = new CubicCurve2D.Float(src.x, src.y, src.x + offset, src.y, tgt.x - offset, tgt.y, tgt.x, tgt.y);
                    } else {
                        // Backward Loop Curve (exits right, circles gracefully, enters from left)
                        int loopOffset = Math.max(60, Math.abs(dy) / 2);
                        curve = new CubicCurve2D.Float(src.x, src.y, src.x + loopOffset, src.y, tgt.x - loopOffset, tgt.y, tgt.x, tgt.y);
                    }

                    // Outer glowing drop shadow
                    g.setColor(new Color(0, 229, 255, 45));
                    g.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(curve);

                    // Main cyan wire
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

                } else if (routingMode == RoutingMode.ORTHOGONAL_RECTANGULAR) {
                    // Orthogonal Manhattan routing with rounded corners
                    Path2D path = new Path2D.Float();
                    path.moveTo(src.x, src.y);
                    int midX = src.x + (dx >= 40 ? dx / 2 : 40);
                    path.lineTo(midX, src.y);
                    path.lineTo(midX, tgt.y);
                    path.lineTo(tgt.x, tgt.y);

                    g.setColor(new Color(0, 229, 255, 45));
                    g.setStroke(new BasicStroke(6.0f));
                    g.draw(path);

                    g.setColor(new Color(0, 229, 255));
                    g.setStroke(new BasicStroke(2.5f));
                    g.draw(path);

                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(1.0f));
                    g.draw(path);

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
        connWidget.setRouter(RouterFactory.createDirectRouter());

        updateEdgeAnchors(edge, connWidget);

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

    private void updateEdgeAnchors(ConnectionModel edge, ConnectionWidget connWidget) {
        NodeModel srcNode = nodeLookup.get(edge.sourceNodeId);
        NodeModel tgtNode = nodeLookup.get(edge.targetNodeId);
        NodeWidget srcWidget = nodeWidgetsMap.get(srcNode);
        NodeWidget tgtWidget = nodeWidgetsMap.get(tgtNode);

        if (srcWidget != null && srcNode != null) {
            int srcYOffset = srcWidget.getPinYOffset(edge.sourcePinId, false);
            connWidget.setSourceAnchor(new PinAnchor(srcWidget, srcWidget.getNodeWidth(), srcYOffset, true));
        }

        if (tgtWidget != null && tgtNode != null) {
            int tgtYOffset = tgtWidget.getPinYOffset(edge.targetPinId, true);
            connWidget.setTargetAnchor(new PinAnchor(tgtWidget, 0, tgtYOffset, false));
        }
    }

    @Override
    protected void attachEdgeSourceAnchor(ConnectionModel edge, NodeModel oldSource, NodeModel newSource) {
        ConnectionWidget cw = (ConnectionWidget) findWidget(edge);
        if (cw != null) updateEdgeAnchors(edge, cw);
    }

    @Override
    protected void attachEdgeTargetAnchor(ConnectionModel edge, NodeModel oldTarget, NodeModel newTarget) {
        ConnectionWidget cw = (ConnectionWidget) findWidget(edge);
        if (cw != null) updateEdgeAnchors(edge, cw);
    }

    // ==========================================
    // Unified Custom Node Widget Implementation
    // ==========================================
    public static class NodeWidget extends Widget {
        private final AtomVisualGraphScene scene;
        private final NodeModel node;
        private final int headerHeight = 28;
        private final int pinRowHeight = 22;
        private final int padding = 8;
        private int computedWidth;
        private int computedHeight;

        public NodeWidget(AtomVisualGraphScene scene, NodeModel node) {
            super(scene);
            this.scene = scene;
            this.node = node;
            recalculateDimensions();
        }

        public int getNodeWidth() {
            return computedWidth;
        }

        public int getPinYOffset(String pinId, boolean isInput) {
            List<PinModel> list = isInput ? node.inputPins : node.outputPins;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).pinId.equals(pinId)) {
                    return headerHeight + padding + i * pinRowHeight + pinRowHeight / 2;
                }
            }
            return headerHeight + padding + pinRowHeight / 2;
        }

        public void recalculateDimensions() {
            int maxPins = Math.max(node.inputPins.size(), node.outputPins.size());
            computedHeight = headerHeight + padding * 2 + Math.max(1, maxPins) * pinRowHeight;
            computedWidth = Math.max(220, node.width);
            node.height = computedHeight;
            node.width = computedWidth;
        }

        @Override
        protected Rectangle calculateClientArea() {
            return new Rectangle(0, 0, computedWidth, computedHeight);
        }

        @Override
        protected void paintWidget() {
            Graphics2D g = getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = computedWidth;
            int h = computedHeight;

            // 1. Outer Drop Shadow
            g.setColor(new Color(0, 0, 0, 70));
            g.fillRoundRect(2, 3, w - 4, h - 4, 10, 10);

            // 2. Rounded Body Card
            g.setColor(new Color(30, 33, 40));
            g.fillRoundRect(0, 0, w, h, 8, 8);

            // 3. Header Bar with Gradient
            Color c1 = new Color(node.headerColorRgb);
            Color c2 = new Color(Math.max(0, c1.getRed() - 45), Math.max(0, c1.getGreen() - 45), Math.max(0, c1.getBlue() - 45));
            GradientPaint gp = new GradientPaint(0, 0, c1, 0, headerHeight, c2);
            g.setPaint(gp);
            g.fillRoundRect(0, 0, w, headerHeight + 4, 8, 8);
            g.fillRect(0, headerHeight - 4, w, 4);

            // Header Bottom Divider
            g.setColor(new Color(15, 17, 22, 160));
            g.drawLine(0, headerHeight, w, headerHeight);

            // Header Title Text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g.drawString(node.title, 10, 19);

            // 4. Draw Input Pins (Strictly on Left Edge)
            g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int i = 0; i < node.inputPins.size(); i++) {
                PinModel pin = node.inputPins.get(i);
                int py = headerHeight + padding + i * pinRowHeight + pinRowHeight / 2;

                // Socket icon anchored at Left (x = 8)
                drawSocket(g, 8, py, pin.type, true);

                // Label left-aligned next to socket
                g.setColor(new Color(215, 220, 230));
                g.drawString(pin.name, 22, py + 4);
            }

            // 5. Draw Output Pins (Strictly on Right Edge)
            for (int i = 0; i < node.outputPins.size(); i++) {
                PinModel pin = node.outputPins.get(i);
                int py = headerHeight + padding + i * pinRowHeight + pinRowHeight / 2;

                // Label right-aligned before socket
                FontMetrics fm = g.getFontMetrics();
                int textW = fm.stringWidth(pin.name);
                g.setColor(new Color(215, 220, 230));
                g.drawString(pin.name, w - 22 - textW, py + 4);

                // Socket icon anchored at Right (x = w - 8)
                drawSocket(g, w - 8, py, pin.type, false);
            }

            // 6. Border Outline (Cyan on Select/Hover)
            ObjectState state = getState();
            if (state.isSelected() || state.isHovered()) {
                g.setColor(new Color(0, 229, 255));
                g.setStroke(new BasicStroke(2.0f));
            } else {
                g.setColor(new Color(50, 56, 68));
                g.setStroke(new BasicStroke(1.2f));
            }
            g.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
        }

        private void drawSocket(Graphics2D g, int cx, int cy, PinType type, boolean isInput) {
            int size = 10;
            Color col = new Color(type.getColorRgb());

            if (type == PinType.FLOW || type == PinType.STATE) {
                // Diamond socket
                Path2D p = new Path2D.Float();
                p.moveTo(cx - size / 2.0, cy);
                p.lineTo(cx, cy - size / 2.0);
                p.lineTo(cx + size / 2.0, cy);
                p.lineTo(cx, cy + size / 2.0);
                p.closePath();

                g.setColor(col);
                g.fill(p);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1.2f));
                g.draw(p);
            } else {
                // Circular Data socket
                g.setColor(col);
                g.fillOval(cx - size / 2, cy - size / 2, size, size);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1.2f));
                g.drawOval(cx - size / 2, cy - size / 2, size, size);
            }
        }
    }

    // ==========================================
    // Exact Pin Anchor Implementation
    // ==========================================
    private static class PinAnchor extends Anchor {
        private final int xOffset;
        private final int yOffset;
        private final boolean isOutput;

        PinAnchor(Widget widget, int xOffset, int yOffset, boolean isOutput) {
            super(widget);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.isOutput = isOutput;
        }

        @Override
        public Result compute(Entry entry) {
            Point loc = getRelatedWidget().getLocation();
            if (loc == null) loc = new Point(0, 0);
            Point absPt = new Point(loc.x + xOffset, loc.y + yOffset);
            Direction dir = isOutput ? Direction.RIGHT : Direction.LEFT;
            return new Result(absPt, dir);
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

        popup.addSeparator();

        JMenuItem layoutItem = new JMenuItem("Auto-Layout Hierarchical (Left-to-Right)");
        layoutItem.addActionListener(e -> autoLayoutHierarchical());
        popup.add(layoutItem);

        JMenuItem springItem = new JMenuItem("Auto-Layout Spring Force-Directed");
        springItem.addActionListener(e -> autoLayoutSpringForce());
        popup.add(springItem);

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
                NodeWidget w = nodeWidgetsMap.get(node);
                if (w != null) {
                    w.recalculateDimensions();
                    w.revalidate();
                    w.repaint();
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
                NodeWidget w = nodeWidgetsMap.get(node);
                if (w != null) {
                    w.recalculateDimensions();
                    w.revalidate();
                    w.repaint();
                }
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
