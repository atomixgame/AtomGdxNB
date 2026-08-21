package com.atomgdx.theme.graph;

import java.io.Serializable;

/**
 * Data type for Visual Graph Pins (ShaderGraph, Blueprints, FSM, Geometry Nodes).
 */
public enum PinType {
    FLOW("Flow", 0xFFFFFFFF),
    FLOAT("Float", 0xFF8BE9FD),
    VEC2("Vector 2", 0xFF50FA7B),
    VEC3("Vector 3", 0xFFFFB86C),
    VEC4_COLOR("Color / Vector 4", 0xFFFF79C6),
    TEXTURE("Texture 2D", 0xFFBD93F9),
    BOOLEAN("Boolean", 0xFFFF5555),
    STRING("String", 0xFFF1FA8C),
    GEOMETRY("3D Geometry Mesh", 0xFF00E5FF),
    STATE("FSM State", 0xFFFFB86C);

    private final String label;
    private final int colorRgb;

    PinType(String label, int colorRgb) {
        this.label = label;
        this.colorRgb = colorRgb;
    }

    public String getLabel() { return label; }
    public int getColorRgb() { return colorRgb; }

    public boolean canConnectTo(PinType target) {
        if (this == target) return true;
        // Compatible numeric conversions
        if (this == FLOAT && (target == VEC2 || target == VEC3 || target == VEC4_COLOR)) return true;
        if (this == VEC3 && target == VEC4_COLOR) return true;
        if (this == VEC4_COLOR && target == VEC3) return true;
        return false;
    }
}
