package com.atomgdx.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Welcome screen component for AtomGdx Studio with Quick Start, Recent Projects, and Documentation.
 */
public class WelcomePanel extends JPanel {

    public WelcomePanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        JLabel title = new JLabel("ATOMGDX STUDIO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(SciFiColors.ACCENT_CYAN);

        JLabel subtitle = new JLabel("Next-Gen LibGDX Game Development Environment | Version 0.1.0");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SciFiColors.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JComponent createContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 25, 0));
        content.setOpaque(false);

        // Left: Quick Start & Tools
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel quickStartLabel = new JLabel("QUICK START");
        quickStartLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        quickStartLabel.setForeground(SciFiColors.ACCENT_BLUE);
        leftPanel.add(quickStartLabel);
        leftPanel.add(Box.createVerticalStrut(15));

        leftPanel.add(createActionCard("New LibGDX Project", "Launch Liftoff Project Generator with multi-platform targets"));
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(createActionCard("Open Particle Designer", "Create and preview 2D & 3D particle visual effects"));
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(createActionCard("Open Scene & Level Designer", "HyperLap2D multi-layer scene editor with Box2D physics"));
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(createActionCard("Open Skin Composer", "Design Scene2D / VisUI skins with live widget preview"));

        // Right: Resources & AI
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JLabel resourceLabel = new JLabel("INTELLIGENCE & RESOURCES");
        resourceLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        resourceLabel.setForeground(SciFiColors.ACCENT_PURPLE);
        rightPanel.add(resourceLabel);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(createActionCard("AI Copilot & MCP Tools", "Connect local/cloud LLMs for GLSL, game logic & MCP tools"));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createActionCard("LibGDX Official Documentation", "Browse APIs, tutorials, and extensions guide"));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createActionCard("Sample Projects & Templates", "Explore 2D platformers, 3D PBR scenes, and shaders"));

        content.add(leftPanel);
        content.add(rightPanel);
        return content;
    }

    private JComponent createActionCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(SciFiColors.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(SciFiColors.TEXT_PRIMARY);

        JLabel descLbl = new JLabel(description);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(SciFiColors.TEXT_SECONDARY);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.SOUTH);
        return card;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel versionLbl = new JLabel("AtomGdx Studio v0.1.0 | i2c Ecosystem");
        versionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLbl.setForeground(SciFiColors.TEXT_MUTED);

        footer.add(versionLbl, BorderLayout.WEST);
        return footer;
    }
}
