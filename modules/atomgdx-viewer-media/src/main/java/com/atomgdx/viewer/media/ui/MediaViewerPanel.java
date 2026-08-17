package com.atomgdx.viewer.media.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * LibGDX Audio Media studio panel (Sound Effects & Music playback and waveform visualizer).
 */
public class MediaViewerPanel extends JPanel {

    private final JLabel trackLabel;
    private final JSlider progressSlider;
    private final JButton playBtn;
    private boolean isPlaying = false;

    public MediaViewerPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(15, 23, 42));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(56, 189, 248), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLabel = new JLabel("LibGDX Audio & Media Studio");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(56, 189, 248));

        trackLabel = new JLabel("Active Track: NeonCosmos_Theme.ogg (Vorbis 44.1kHz / Stereo / 320kbps)");
        trackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trackLabel.setForeground(new Color(148, 163, 184));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(trackLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Waveform Canvas
        JPanel waveformPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int cy = h / 2;

                // Background
                g2.setColor(new Color(10, 15, 30));
                g2.fillRect(0, 0, w, h);

                // Grid lines
                g2.setColor(new Color(30, 41, 59));
                for (int x = 0; x < w; x += 40) {
                    g2.drawLine(x, 0, x, h);
                }
                g2.drawLine(0, cy, w, cy);

                // Waveform bars
                g2.setColor(new Color(56, 189, 248));
                int bars = Math.max(1, w / 4);
                for (int i = 0; i < bars; i++) {
                    double norm = (double) i / bars;
                    double amp = Math.sin(norm * 12.0) * Math.cos(norm * 4.0) * (h * 0.35);
                    amp += Math.sin(norm * 30.0) * (h * 0.1);
                    int bh = (int) Math.abs(amp);
                    int bx = i * 4;
                    g2.fillRect(bx, cy - bh, 3, bh * 2);
                }

                // Playhead
                int playheadX = (int) (w * (progressSlider.getValue() / 100.0));
                g2.setColor(new Color(244, 63, 94));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(playheadX, 0, playheadX, h);
            }
        };
        waveformPanel.setBorder(new LineBorder(new Color(51, 65, 85), 1));
        add(waveformPanel, BorderLayout.CENTER);

        // Bottom Controls
        JPanel controlsPanel = new JPanel(new BorderLayout(10, 10));
        controlsPanel.setBackground(new Color(30, 41, 59));
        controlsPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(71, 85, 105)), " Playback & Audio Channel Controls ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(148, 163, 184)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        progressSlider = new JSlider(0, 100, 35);
        progressSlider.setOpaque(false);
        progressSlider.addChangeListener(e -> waveformPanel.repaint());
        controlsPanel.add(progressSlider, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        JButton stopBtn = createStyledButton("Stop", new Color(71, 85, 105));
        playBtn = createStyledButton("Play / Pause", new Color(14, 165, 233));
        JButton loopBtn = createStyledButton("Loop: ON", new Color(16, 185, 129));

        playBtn.addActionListener(e -> {
            isPlaying = !isPlaying;
            playBtn.setText(isPlaying ? "Pause" : "Play");
        });

        btnPanel.add(stopBtn);
        btnPanel.add(playBtn);
        btnPanel.add(loopBtn);

        controlsPanel.add(btnPanel, BorderLayout.SOUTH);
        add(controlsPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}
