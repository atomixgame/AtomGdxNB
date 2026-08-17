package com.atomgdx.theme;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Sci-Fi Theme customizer for AtomGdx Studio setting dark obsidian backgrounds and neon cyan highlights.
 */
public class SciFiTheme {
    public static final String NAME = "AtomGdx Sci-Fi Dark";

    public static boolean setup() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            applyCustomProperties();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void applyCustomProperties() {
        UIManager.put("Panel.background", SciFiColors.BG_DARK);
        UIManager.put("Panel.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("Label.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("Button.background", SciFiColors.BG_CARD);
        UIManager.put("Button.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("TextField.background", SciFiColors.BG_CARD);
        UIManager.put("TextField.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("TextArea.background", SciFiColors.BG_DARK);
        UIManager.put("TextArea.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("ScrollPane.background", SciFiColors.BG_DARK);
        UIManager.put("Tree.background", SciFiColors.BG_DARK);
        UIManager.put("Tree.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("List.background", SciFiColors.BG_DARK);
        UIManager.put("List.foreground", SciFiColors.TEXT_PRIMARY);
        UIManager.put("Component.accentColor", SciFiColors.ACCENT_CYAN);
        UIManager.put("ProgressBar.foreground", SciFiColors.ACCENT_CYAN);
    }
}
