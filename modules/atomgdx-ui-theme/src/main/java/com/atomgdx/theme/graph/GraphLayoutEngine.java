package com.atomgdx.theme.graph;

import java.awt.Point;
import java.util.*;

/**
 * Graph Auto-Layout Algorithms & Alignment Heuristics for Visual Scripting and State Machines.
 * Algorithms:
 * 1. Sugiyama Layered DAG Layout (Left-to-Right hierarchical flow)
 * 2. Force-Directed Spring Electrical Simulation (Organic node balancing)
 * 3. Grid Snapping & Even Spacing Heuristics
 */
public class GraphLayoutEngine {

    public enum Alignment {
        ALIGN_LEFT,
        ALIGN_RIGHT,
        ALIGN_TOP,
        ALIGN_BOTTOM,
        ALIGN_CENTER_X,
        ALIGN_CENTER_Y
    }

    /**
     * Applies Sugiyama Hierarchical Left-to-Right DAG layout to the graph document.
     */
    public static void applyHierarchicalLayout(VisualGraphDocument doc) {
        if (doc == null || doc.nodes.isEmpty()) return;

        Map<String, NodeModel> nodeMap = new HashMap<>();
        Map<String, List<String>> adjList = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        for (NodeModel node : doc.nodes) {
            nodeMap.put(node.nodeId, node);
            adjList.put(node.nodeId, new ArrayList<>());
            inDegree.put(node.nodeId, 0);
        }

        for (ConnectionModel conn : doc.connections) {
            if (adjList.containsKey(conn.sourceNodeId) && inDegree.containsKey(conn.targetNodeId)) {
                adjList.get(conn.sourceNodeId).add(conn.targetNodeId);
                inDegree.put(conn.targetNodeId, inDegree.get(conn.targetNodeId) + 1);
            }
        }

        // 1. Layer assignment using BFS from root nodes (in-degree == 0)
        Map<String, Integer> layerMap = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        for (NodeModel node : doc.nodes) {
            if (inDegree.get(node.nodeId) == 0) {
                queue.add(node.nodeId);
                layerMap.put(node.nodeId, 0);
            }
        }

        // If cyclic and no roots found, pick first node as root
        if (queue.isEmpty() && !doc.nodes.isEmpty()) {
            String firstId = doc.nodes.get(0).nodeId;
            queue.add(firstId);
            layerMap.put(firstId, 0);
        }

        Set<String> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            String curr = queue.poll();
            visited.add(curr);
            int currLayer = layerMap.getOrDefault(curr, 0);

            for (String next : adjList.getOrDefault(curr, Collections.emptyList())) {
                int nextLayer = Math.max(layerMap.getOrDefault(next, 0), currLayer + 1);
                layerMap.put(next, nextLayer);
                if (!visited.contains(next)) {
                    queue.add(next);
                }
            }
        }

        // Group nodes by layer
        Map<Integer, List<NodeModel>> layers = new TreeMap<>();
        for (NodeModel node : doc.nodes) {
            int l = layerMap.getOrDefault(node.nodeId, 0);
            layers.computeIfAbsent(l, k -> new ArrayList<>()).add(node);
        }

        // 2. Position nodes with fixed horizontal column spacing (300px) and vertical row spacing (160px)
        int startX = 80;
        int startY = 100;
        int colSpacing = 320;
        int rowSpacing = 160;

        for (Map.Entry<Integer, List<NodeModel>> entry : layers.entrySet()) {
            int layerIndex = entry.getKey();
            List<NodeModel> layerNodes = entry.getValue();
            int x = startX + layerIndex * colSpacing;

            for (int i = 0; i < layerNodes.size(); i++) {
                NodeModel node = layerNodes.get(i);
                int y = startY + i * rowSpacing;
                node.posX = x;
                node.posY = y;
            }
        }
    }

    /**
     * Applies Force-Directed Spring Electrical layout to balance cyclic FSM graphs.
     */
    public static void applySpringForceLayout(VisualGraphDocument doc) {
        if (doc == null || doc.nodes.size() < 2) return;

        int iterations = 40;
        double k = 220.0; // Ideal edge distance
        double cRep = 50000.0; // Coulomb repulsion constant
        double cAttr = 0.04; // Hooke spring attraction constant

        Map<String, Point.Double> positions = new HashMap<>();
        Map<String, Point.Double> forces = new HashMap<>();

        for (NodeModel n : doc.nodes) {
            positions.put(n.nodeId, new Point.Double(n.posX, n.posY));
            forces.put(n.nodeId, new Point.Double(0, 0));
        }

        for (int iter = 0; iter < iterations; iter++) {
            // Reset forces
            for (NodeModel n : doc.nodes) {
                forces.get(n.nodeId).setLocation(0, 0);
            }

            // 1. Repulsion between all node pairs
            for (int i = 0; i < doc.nodes.size(); i++) {
                NodeModel u = doc.nodes.get(i);
                Point.Double posU = positions.get(u.nodeId);

                for (int j = i + 1; j < doc.nodes.size(); j++) {
                    NodeModel v = doc.nodes.get(j);
                    Point.Double posV = positions.get(v.nodeId);

                    double dx = posU.x - posV.x;
                    double dy = posU.y - posV.y;
                    double dist = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));

                    double repForce = cRep / (dist * dist);
                    double fx = (dx / dist) * repForce;
                    double fy = (dy / dist) * repForce;

                    forces.get(u.nodeId).x += fx;
                    forces.get(u.nodeId).y += fy;
                    forces.get(v.nodeId).x -= fx;
                    forces.get(v.nodeId).y -= fy;
                }
            }

            // 2. Attraction along connected wires
            for (ConnectionModel conn : doc.connections) {
                Point.Double posSrc = positions.get(conn.sourceNodeId);
                Point.Double posTgt = positions.get(conn.targetNodeId);

                if (posSrc != null && posTgt != null) {
                    double dx = posTgt.x - posSrc.x;
                    double dy = posTgt.y - posSrc.y;
                    double dist = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));

                    double attrForce = cAttr * (dist - k);
                    double fx = (dx / dist) * attrForce;
                    double fy = (dy / dist) * attrForce;

                    forces.get(conn.sourceNodeId).x += fx;
                    forces.get(conn.sourceNodeId).y += fy;
                    forces.get(conn.targetNodeId).x -= fx;
                    forces.get(conn.targetNodeId).y -= fy;
                }
            }

            // 3. Apply displacement with cooling
            double temp = Math.max(0.1, (1.0 - (double) iter / iterations) * 20.0);
            for (NodeModel n : doc.nodes) {
                Point.Double pos = positions.get(n.nodeId);
                Point.Double f = forces.get(n.nodeId);

                double fMag = Math.max(1.0, Math.sqrt(f.x * f.x + f.y * f.y));
                double step = Math.min(temp, fMag);

                pos.x += (f.x / fMag) * step;
                pos.y += (f.y / fMag) * step;
            }
        }

        // Apply updated positions and normalize to positive canvas coordinates
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        for (Point.Double p : positions.values()) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
        }

        for (NodeModel n : doc.nodes) {
            Point.Double p = positions.get(n.nodeId);
            n.posX = (int) Math.round(p.x - minX + 80);
            n.posY = (int) Math.round(p.y - minY + 100);
        }
    }

    /**
     * Aligns a collection of nodes.
     */
    public static void alignNodes(List<NodeModel> nodes, Alignment align) {
        if (nodes == null || nodes.size() < 2) return;

        switch (align) {
            case ALIGN_LEFT:
                int minX = nodes.stream().mapToInt(n -> n.posX).min().orElse(0);
                nodes.forEach(n -> n.posX = minX);
                break;
            case ALIGN_TOP:
                int minY = nodes.stream().mapToInt(n -> n.posY).min().orElse(0);
                nodes.forEach(n -> n.posY = minY);
                break;
            case ALIGN_CENTER_Y:
                double avgY = nodes.stream().mapToInt(n -> n.posY).average().orElse(0);
                int centerY = (int) Math.round(avgY);
                nodes.forEach(n -> n.posY = centerY);
                break;
            case ALIGN_CENTER_X:
                double avgX = nodes.stream().mapToInt(n -> n.posX).average().orElse(0);
                int centerX = (int) Math.round(avgX);
                nodes.forEach(n -> n.posX = centerX);
                break;
            default:
                break;
        }
    }
}
