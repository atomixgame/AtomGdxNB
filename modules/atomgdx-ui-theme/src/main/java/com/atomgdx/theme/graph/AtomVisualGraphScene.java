package com.atomgdx.theme.graph;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * NetBeans Visual Library GraphScene powering ShaderGraph, FSM, Animator, and Geometry Nodes.
 */
public class AtomVisualGraphScene extends GraphScene<NodeModel, ConnectionModel> {

    private final VisualGraphDocument document;
    private final LayerWidget mainLayer = new LayerWidget(this);
    private final LayerWidget connectionLayer = new LayerWidget(this);
    private final LayerWidget interactionLayer = new LayerWidget(this);

    private final Map<String, Widget> pinWidgets = new HashMap<>();

    public AtomVisualGraphScene(VisualGraphDocument document) {
        this.document = document != null ? document : new VisualGraphDocument();

        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);

        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createMouseCenteredZoomAction(1.15));

        loadFromDocument();
    }

    public VisualGraphDocument getDocument() {
        return document;
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
        Widget nodeWidget = new Widget(this);
        nodeWidget.setLayout(LayoutFactory.createVerticalFlowLayout());
        nodeWidget.setBorder(BorderFactory.createRoundedBorder(8, 8, new Color(45, 48, 55), new Color(24, 26, 30)));
        nodeWidget.setPreferredLocation(new Point(node.posX, node.posY));
        nodeWidget.setPreferredBounds(new Rectangle(0, 0, Math.max(160, node.width), Math.max(100, node.height)));

        // Header
        Widget header = new Widget(this);
        header.setLayout(LayoutFactory.createHorizontalFlowLayout());
        header.setBackground(new Color(node.headerColorRgb));
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        LabelWidget title = new LabelWidget(this, node.title);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(Color.WHITE);
        header.addChild(title);
        nodeWidget.addChild(header);

        // Body with Input/Output Columns
        Widget body = new Widget(this);
        body.setLayout(LayoutFactory.createHorizontalFlowLayout());
        body.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Inputs Column
        Widget inputCol = new Widget(this);
        inputCol.setLayout(LayoutFactory.createVerticalFlowLayout());
        for (PinModel pin : node.inputPins) {
            Widget pinW = createPinWidget(node, pin);
            inputCol.addChild(pinW);
            pinWidgets.put(node.nodeId + ":" + pin.pinId, pinW);
        }
        body.addChild(inputCol);

        // Outputs Column
        Widget outputCol = new Widget(this);
        outputCol.setLayout(LayoutFactory.createVerticalFlowLayout());
        for (PinModel pin : node.outputPins) {
            Widget pinW = createPinWidget(node, pin);
            outputCol.addChild(pinW);
            pinWidgets.put(node.nodeId + ":" + pin.pinId, pinW);
        }
        body.addChild(outputCol);

        nodeWidget.addChild(body);

        // Node Actions
        nodeWidget.getActions().addAction(ActionFactory.createMoveAction());
        nodeWidget.getActions().addAction(ActionFactory.createSelectAction(new SelectProvider() {
            @Override
            public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
                return true;
            }

            @Override
            public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
                return true;
            }

            @Override
            public void select(Widget widget, Point localLocation, boolean invertSelection) {
                widget.bringToFront();
            }
        }));

        mainLayer.addChild(nodeWidget);
        return nodeWidget;
    }

    private Widget createPinWidget(NodeModel node, PinModel pin) {
        Widget pinContainer = new Widget(this);
        pinContainer.setLayout(LayoutFactory.createHorizontalFlowLayout());
        pinContainer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Pin Socket Dot
        Widget dot = new Widget(this) {
            @Override
            protected void paintWidget() {
                Graphics2D g = getGraphics();
                g.setColor(new Color(pin.type.getColorRgb()));
                g.fillOval(0, 0, 10, 10);
                g.setColor(Color.WHITE);
                g.drawOval(0, 0, 10, 10);
            }
        };
        dot.setPreferredBounds(new Rectangle(0, 0, 10, 10));

        LabelWidget label = new LabelWidget(this, pin.name);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(220, 224, 230));

        if (pin.isInput) {
            pinContainer.addChild(dot);
            pinContainer.addChild(label);
        } else {
            pinContainer.addChild(label);
            pinContainer.addChild(dot);
        }

        return pinContainer;
    }

    @Override
    protected Widget attachEdgeWidget(ConnectionModel edge) {
        ConnectionWidget connWidget = new ConnectionWidget(this);
        connWidget.setLineColor(new Color(0, 220, 255));
        connWidget.setStroke(new BasicStroke(2.5f));
        connWidget.setRouter(RouterFactory.createDirectRouter());

        Widget srcPin = pinWidgets.get(edge.sourceNodeId + ":" + edge.sourcePinId);
        Widget tgtPin = pinWidgets.get(edge.targetNodeId + ":" + edge.targetPinId);

        if (srcPin != null) connWidget.setSourceAnchor(AnchorFactory.createRectangularAnchor(srcPin));
        if (tgtPin != null) connWidget.setTargetAnchor(AnchorFactory.createRectangularAnchor(tgtPin));

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
}
