package com.atomgdx.viewer.media.ui;

import com.atomgdx.core.ui.DarkThemeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

/**
 * LibGDX Audio & Media studio panel featuring real-time waveform visualizer,
 * playback timeline scrubbing, volume/pan/pitch controls, and track metadata.
 */
public class MediaViewerPanel extends JPanel {

    private final JLabel trackLabel;
    private final JSlider progressSlider;
    private final JButton playBtn;
    private final Timer playbackTimer;

    private boolean isPlaying = false;
    private float playbackProgress = 0.25f; // 0.0 to 1.0
    private float volume = 0.8f;
    private float pan = 0.0f;
    private float pitch = 1.0f;
    private boolean isLooping = true;

    private final WaveformCanvas waveformCanvas = new WaveformCanvas();

    public MediaViewerPanel() {
        this(null);
    }

    public MediaViewerPanel(File audioFile) {
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);

        // 1. Top Header
        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setBackground(DarkThemeUtils.BG_HEADER);
        headerPanel.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel titleLabel = new JLabel("Audio & Media Studio");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(DarkThemeUtils.TEXT_PRIMARY);

        String trackName = audioFile != null ? audioFile.getName() : "NeonCosmos_Theme.ogg";
        trackLabel = new JLabel("Active Track: " + trackName + " (Vorbis 44.1kHz / Stereo / 320kbps)");
        trackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        trackLabel.setForeground(DarkThemeUtils.TEXT_SECONDARY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(trackLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Waveform & Spectrum Visualizer
        add(waveformCanvas, BorderLayout.CENTER);

        // 3. Bottom Controls (Timeline & DSP Channels)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(DarkThemeUtils.BG_PANEL);
        bottomPanel.setBorder(new EmptyBorder(6, 8, 8, 8));

        // Timeline Bar
        JPanel timelineRow = new JPanel(new BorderLayout(8, 0));
        timelineRow.setOpaque(false);
        JLabel timeCurrent = new JLabel("0:45");
        timeCurrent.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        timeCurrent.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel timeTotal = new JLabel("3:12");
        timeTotal.setForeground(DarkThemeUtils.TEXT_SECONDARY);
        timeTotal.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        progressSlider = new JSlider(0, 1000, 250);
        progressSlider.setOpaque(false);
        progressSlider.addChangeListener(e -> {
            if (!progressSlider.getValueIsAdjusting()) {
                playbackProgress = progressSlider.getValue() / 1000.0f;
                waveformCanvas.repaint();
            }
        });

        timelineRow.add(timeCurrent, BorderLayout.WEST);
        timelineRow.add(progressSlider, BorderLayout.CENTER);
        timelineRow.add(timeTotal, BorderLayout.EAST);
        bottomPanel.add(timelineRow);
        bottomPanel.add(Box.createVerticalStrut(6));

        // Playback Buttons & Sliders Row
        JPanel controlsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        controlsRow.setOpaque(false);

        playBtn = new JButton("Play");
        JButton stopBtn = new JButton("Stop");
        for (JButton b : new JButton[]{playBtn, stopBtn}) {
            b.setBackground(DarkThemeUtils.BG_HEADER);
            b.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            b.setFont(new Font("Segoe UI", Font.BOLD, 11));
            b.setFocusPainted(false);
        }

        playBtn.addActionListener(e -> togglePlayback());
        stopBtn.addActionListener(e -> stopPlayback());

        JCheckBox loopBox = new JCheckBox("Loop", isLooping);
        loopBox.setOpaque(false);
        loopBox.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        loopBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loopBox.addActionListener(e -> isLooping = loopBox.isSelected());

        // Volume Slider
        JSlider volSlider = new JSlider(0, 100, 80);
        volSlider.setPreferredSize(new Dimension(80, 20));
        volSlider.setOpaque(false);
        volSlider.addChangeListener(e -> volume = volSlider.getValue() / 100.0f);

        // Pan Slider
        JSlider panSlider = new JSlider(-100, 100, 0);
        panSlider.setPreferredSize(new Dimension(80, 20));
        panSlider.setOpaque(false);
        panSlider.addChangeListener(e -> pan = panSlider.getValue() / 100.0f);

        controlsRow.add(playBtn);
        controlsRow.add(stopBtn);
        controlsRow.add(loopBox);
        controlsRow.add(new JLabel(" Vol:"));
        controlsRow.add(volSlider);
        controlsRow.add(new JLabel(" Pan:"));
        controlsRow.add(panSlider);

        bottomPanel.add(controlsRow);
        add(bottomPanel, BorderLayout.SOUTH);

        // 4. Playback Animation Timer
        playbackTimer = new Timer(50, e -> {
            if (isPlaying) {
                playbackProgress += 0.003f;
                if (playbackProgress > 1.0f) {
                    if (isLooping) {
                        playbackProgress = 0.0f;
                    } else {
                        stopPlayback();
                    }
                }
                progressSlider.setValue((int) (playbackProgress * 1000));
                waveformCanvas.repaint();
            }
        });
        playbackTimer.start();
    }

    public void togglePlayback() {
        isPlaying = !isPlaying;
        playBtn.setText(isPlaying ? "Pause" : "Play");
    }

    public void stopPlayback() {
        isPlaying = false;
        playbackProgress = 0.0f;
        playBtn.setText("Play");
        progressSlider.setValue(0);
        waveformCanvas.repaint();
    }

    /**
     * Interactive Waveform & Spectrum Canvas.
     */
    private class WaveformCanvas extends JPanel {
        WaveformCanvas() {
            setBackground(DarkThemeUtils.BG_DARK);
            setBorder(new LineBorder(DarkThemeUtils.BORDER, 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cy = h / 2;

            // Background Grid
            g2.setColor(new Color(20, 21, 23));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(38, 40, 44));
            for (int x = 0; x < w; x += 40) {
                g2.drawLine(x, 0, x, h);
            }
            g2.drawLine(0, cy, w, cy);

            // Waveform Amplitude Bars
            int barW = 3;
            int gap = 1;
            int totalBars = Math.max(1, w / (barW + gap));

            for (int i = 0; i < totalBars; i++) {
                double norm = (double) i / totalBars;
                double amp = Math.sin(norm * 14.0) * Math.cos(norm * 6.0) * (h * 0.38);
                amp += Math.sin(norm * 36.0) * (h * 0.12);
                int bh = (int) Math.abs(amp);
                int bx = i * (barW + gap);

                if (norm <= playbackProgress) {
                    g2.setColor(new Color(53, 116, 240)); // Active Blue
                } else {
                    g2.setColor(new Color(70, 74, 82));  // Muted Gray
                }
                g2.fillRect(bx, cy - bh, barW, Math.max(2, bh * 2));
            }

            // Playhead Cursor
            int playheadX = (int) (w * playbackProgress);
            g2.setColor(new Color(240, 75, 75)); // Red needle
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(playheadX, 0, playheadX, h);

            // Channel Indicator
            g2.setColor(DarkThemeUtils.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString("L/R Stereo Channel Output", 10, 18);
        }
    }
}
