package com.atomgdx.theme;

import java.awt.Color;

/**
 * Color palette definitions for AtomGdx Studio's Sci-Fi Theme.
 */
public final class SciFiColors {
    private SciFiColors() {}

    // Backgrounds
    public static final Color BG_DARKEST = new Color(0x0A, 0x0E, 0x14); // #0A0E14
    public static final Color BG_DARK = new Color(0x0D, 0x11, 0x17);    // #0D1117
    public static final Color BG_PANEL = new Color(0x16, 0x1B, 0x22);   // #161B22
    public static final Color BG_CARD = new Color(0x21, 0x26, 0x2D);    // #21262D

    // Neon Accents
    public static final Color ACCENT_CYAN = new Color(0x00, 0xF0, 0xFF);  // #00F0FF (Primary Neon)
    public static final Color ACCENT_BLUE = new Color(0x38, 0x8B, 0xFF);  // #388BFF
    public static final Color ACCENT_PURPLE = new Color(0xA3, 0x71, 0xF7);// #A371F7
    public static final Color ACCENT_GREEN = new Color(0x3F, 0xB9, 0x50); // #3FB950
    public static final Color ACCENT_AMBER = new Color(0xD2, 0x99, 0x22); // #D29922
    public static final Color ACCENT_RED = new Color(0xF8, 0x51, 0x49);   // #F85149

    // Text
    public static final Color TEXT_PRIMARY = new Color(0xEB, 0xF0, 0xF8);
    public static final Color TEXT_SECONDARY = new Color(0x8B, 0x94, 0x9E);
    public static final Color TEXT_MUTED = new Color(0x48, 0x4F, 0x58);

    // Borders & Dividers
    public static final Color BORDER_SUBTLE = new Color(0x30, 0x36, 0x3D);
    public static final Color BORDER_GLOW = new Color(0x00, 0xF0, 0xFF, 120);
}
