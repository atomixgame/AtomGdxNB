package com.atomgdx.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Sci-Fi Branded About Dialog for AtomGdx Studio displaying banner, version, Java 21 info, and memory stats.
 */
public class AboutDialogPanel extends JPanel {

    public AboutDialogPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createBannerHeader(), BorderLayout.NORTH);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JComponent createBannerHeader() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(SciFiColors.BG_CARD);
        bannerPanel.setBorder(BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1));
        bannerPanel.setPreferredSize(new Dimension(580, 160));

        // 1. Try loading from classpath resource
        try (InputStream in = getClass().getResourceAsStream("/com/atomgdx/theme/about.png")) {
            if (in != null) {
                BufferedImage img = ImageIO.read(in);
                if (img != null) {
                    JLabel imgLabel = new JLabel(new ImageIcon(img.getScaledInstance(580, 160, Image.SCALE_SMOOTH)));
                    bannerPanel.add(imgLabel, BorderLayout.CENTER);
                    return bannerPanel;
                }
            }
        } catch (Exception ignored) {}

        // 2. Try loading from local path
        File[] candidateFiles = new File[]{
                new File("branding/modules/org-netbeans-core-ui.jar/org/netbeans/core/ui/about.png"),
                new File("docs/assets/atomgdx_branding.jpg")
        };
        for (File f : candidateFiles) {
            if (f.exists()) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    JLabel imgLabel = new JLabel(new ImageIcon(img.getScaledInstance(580, 160, Image.SCALE_SMOOTH)));
                    bannerPanel.add(imgLabel, BorderLayout.CENTER);
                    return bannerPanel;
                } catch (Exception ignored) {}
            }
        }

        // Fallback banner if image not loaded directly
        JLabel titleLabel = new JLabel("ATOMGDX STUDIO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(SciFiColors.ACCENT_CYAN);
        bannerPanel.add(titleLabel, BorderLayout.CENTER);

        return bannerPanel;
    }

    private JComponent createDetailsPanel() {
        JPanel details = new JPanel(new GridLayout(6, 2, 10, 8));
        details.setOpaque(false);
        details.setBorder(new EmptyBorder(10, 10, 10, 10));

        addDetailRow(details, "Application Version:", "0.1.40 (Build 2026.08)");
        addDetailRow(details, "NetBeans Platform:", "Apache NetBeans 30.0 Core");
        addDetailRow(details, "Java Runtime:", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        addDetailRow(details, "Operating System:", System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        addDetailRow(details, "LibGDX Version:", "1.13.1 (Desktop LWJGL3, Android, TeaVM, RoboVM)");
        
        long maxMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        addDetailRow(details, "Memory Allocated:", totalMem + " MB / " + maxMem + " MB Max");

        return details;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(SciFiColors.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(SciFiColors.TEXT_PRIMARY);

        panel.add(lbl);
        panel.add(val);
    }

    private JComponent createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel copyright = new JLabel("© 2026 AtomGdx Studio | Part of the i2c Platform Ecosystem");
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyright.setForeground(SciFiColors.TEXT_MUTED);

        footer.add(copyright, BorderLayout.WEST);
        return footer;
    }
}
