package com.atomgdx.theme.timeline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Interactive Dope-Sheet & Animation Scrubber Timeline Panel.
 */
public class TimelinePanel extends JPanel {

    private final AnimationTrackModel model;
    private float currentTime = 0.0f;
    private boolean isPlaying = false;
    private boolean isLooping = true;
    private Timer playbackTimer;

    private final JLabel timeLabel = new JLabel("00:00.00 / 04:00.00");
    private final JButton playPauseBtn = new JButton("▶ Play");
    private final JSlider timeScrubber = new JSlider(0, 400, 0);
    private final JPanel tracksCanvas;

    public TimelinePanel() {
        this(new AnimationTrackModel());
    }

    public TimelinePanel(AnimationTrackModel model) {
        this.model = model != null ? model : new AnimationTrackModel();
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(22, 23, 26));

        // Transport control bar
        JPanel transportBar = new JPanel(new BorderLayout());
        transportBar.setBackground(new Color(30, 32, 36));
        transportBar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftControls.setOpaque(false);

        JButton firstBtn = new JButton("⏮");
        firstBtn.setToolTipText("First Frame");
        firstBtn.addActionListener(e -> setTime(0f));
        leftControls.add(firstBtn);

        JButton prevBtn = new JButton("◀");
        prevBtn.setToolTipText("Step Backward (1 frame)");
        prevBtn.addActionListener(e -> setTime(Math.max(0f, currentTime - (1f / model.getFps()))));
        leftControls.add(prevBtn);

        playPauseBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        playPauseBtn.setBackground(new Color(44, 93, 212));
        playPauseBtn.setForeground(Color.WHITE);
        playPauseBtn.addActionListener(e -> togglePlayback());
        leftControls.add(playPauseBtn);

        JButton nextBtn = new JButton("▶");
        nextBtn.setToolTipText("Step Forward (1 frame)");
        nextBtn.addActionListener(e -> setTime(Math.min(model.getDurationSec(), currentTime + (1f / model.getFps()))));
        leftControls.add(nextBtn);

        JButton lastBtn = new JButton("⏭");
        lastBtn.setToolTipText("Last Frame");
        lastBtn.addActionListener(e -> setTime(model.getDurationSec()));
        leftControls.add(lastBtn);

        JToggleButton loopToggle = new JToggleButton("🔁 Loop", isLooping);
        loopToggle.addActionListener(e -> isLooping = loopToggle.isSelected());
        leftControls.add(loopToggle);

        transportBar.add(leftControls, BorderLayout.WEST);

        // Center Scrubber
        JPanel centerPanel = new JPanel(new BorderLayout(6, 0));
        centerPanel.setOpaque(false);
        timeScrubber.setBackground(new Color(30, 32, 36));
        timeScrubber.addChangeListener(e -> {
            if (timeScrubber.getValueIsAdjusting()) {
                currentTime = (timeScrubber.getValue() / 100f);
                updateLabelsAndRepaint();
            }
        });
        centerPanel.add(timeScrubber, BorderLayout.CENTER);
        transportBar.add(centerPanel, BorderLayout.CENTER);

        // Right Info & Add Track
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightControls.setOpaque(false);

        timeLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        timeLabel.setForeground(new Color(0, 220, 255));
        rightControls.add(timeLabel);

        JButton addTrackBtn = new JButton("+ Track");
        addTrackBtn.addActionListener(e -> addNewTrack());
        rightControls.add(addTrackBtn);

        transportBar.add(rightControls, BorderLayout.EAST);
        add(transportBar, BorderLayout.NORTH);

        // Center Dope-Sheet tracks view
        tracksCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int headerW = 180;
                int rulerH = 26;

                // Backgrounds
                g2.setColor(new Color(24, 25, 28));
                g2.fillRect(0, 0, headerW, h);
                g2.setColor(new Color(16, 17, 20));
                g2.fillRect(headerW, 0, w - headerW, h);

                // Timeline ruler
                g2.setColor(new Color(32, 34, 38));
                g2.fillRect(headerW, 0, w - headerW, rulerH);
                g2.setColor(new Color(60, 63, 68));
                g2.drawLine(0, rulerH, w, rulerH);

                float timelineW = w - headerW - 20;
                float duration = model.getDurationSec();

                // Ruler second tick marks
                g2.setColor(new Color(160, 165, 175));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                for (float t = 0f; t <= duration; t += 0.5f) {
                    int tx = headerW + (int) ((t / duration) * timelineW);
                    int tickH = (t % 1.0f == 0) ? 12 : 6;
                    g2.drawLine(tx, rulerH - tickH, tx, rulerH);
                    if (t % 1.0f == 0) {
                        g2.drawString(String.format("%.1fs", t), tx + 2, 14);
                    }
                }

                // Render Tracks
                int rowH = 32;
                int y = rulerH;

                for (AnimationTrackModel.Track track : model.getTracks()) {
                    // Track header
                    g2.setColor(new Color(28, 30, 34));
                    g2.fillRect(0, y, headerW, rowH);
                    g2.setColor(new Color(45, 47, 52));
                    g2.drawLine(0, y + rowH, w, y + rowH);

                    // Track type indicator dot
                    g2.setColor(new Color(track.getType().getColorRgb()));
                    g2.fillOval(8, y + 10, 10, 10);

                    // Track name
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.drawString(track.getName(), 24, y + 19);

                    // Track lane
                    g2.setColor(new Color(20, 21, 24));
                    g2.fillRect(headerW, y + 2, (int) timelineW + 10, rowH - 4);

                    // Render Keyframe diamonds ◆
                    for (AnimationTrackModel.Keyframe kf : track.getKeyframes()) {
                        int kx = headerW + (int) ((kf.timeSec / duration) * timelineW);
                        int ky = y + (rowH / 2);

                        g2.setColor(new Color(track.getType().getColorRgb()));
                        Polygon diamond = new Polygon(
                                new int[]{kx, kx + 6, kx, kx - 6},
                                new int[]{ky - 6, ky, ky + 6, ky},
                                4
                        );
                        g2.fill(diamond);
                        g2.setColor(Color.WHITE);
                        g2.draw(diamond);
                    }

                    y += rowH;
                }

                // Red Playhead scrubber line
                int playheadX = headerW + (int) ((currentTime / duration) * timelineW);
                g2.setColor(new Color(255, 60, 60));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(playheadX, 0, playheadX, h);

                // Playhead top handle
                Polygon head = new Polygon(
                        new int[]{playheadX - 6, playheadX + 6, playheadX + 6, playheadX, playheadX - 6},
                        new int[]{0, 0, 10, 16, 10},
                        5
                );
                g2.fill(head);

                g2.dispose();
            }
        };

        tracksCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                scrubToMouse(e.getX());
            }
        });
        tracksCanvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                scrubToMouse(e.getX());
            }
        });

        add(new JScrollPane(tracksCanvas), BorderLayout.CENTER);

        // 60 FPS playback loop timer
        playbackTimer = new Timer(16, e -> {
            if (isPlaying) {
                currentTime += 0.016f;
                if (currentTime >= model.getDurationSec()) {
                    if (isLooping) {
                        currentTime = 0f;
                    } else {
                        currentTime = model.getDurationSec();
                        pausePlayback();
                    }
                }
                updateLabelsAndRepaint();
            }
        });

        updateLabelsAndRepaint();
    }

    private void scrubToMouse(int mx) {
        int headerW = 180;
        int timelineW = tracksCanvas.getWidth() - headerW - 20;
        if (timelineW > 0 && mx >= headerW) {
            float frac = (float) (mx - headerW) / (float) timelineW;
            currentTime = Math.max(0f, Math.min(model.getDurationSec(), frac * model.getDurationSec()));
            updateLabelsAndRepaint();
        }
    }

    private void togglePlayback() {
        if (isPlaying) pausePlayback();
        else startPlayback();
    }

    private void startPlayback() {
        isPlaying = true;
        playPauseBtn.setText("⏸ Pause");
        playPauseBtn.setBackground(new Color(220, 120, 30));
        playbackTimer.start();
    }

    private void pausePlayback() {
        isPlaying = false;
        playPauseBtn.setText("▶ Play");
        playPauseBtn.setBackground(new Color(44, 93, 212));
        playbackTimer.stop();
    }

    private void setTime(float time) {
        currentTime = time;
        updateLabelsAndRepaint();
    }

    private void updateLabelsAndRepaint() {
        timeScrubber.setValue(Math.round(currentTime * 100f));
        timeLabel.setText(String.format("%02d:%05.2f / %02d:%05.2f",
                (int) (currentTime / 60), currentTime % 60,
                (int) (model.getDurationSec() / 60), model.getDurationSec() % 60));
        tracksCanvas.repaint();
    }

    private void addNewTrack() {
        String name = JOptionPane.showInputDialog(this, "Enter Track Name (e.g. enemy.Position_X):", "Add Animation Track", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            AnimationTrackModel.Track t = new AnimationTrackModel.Track(name.trim(), AnimationTrackModel.TrackType.POSITION_X);
            t.addKeyframe(0f, 0f);
            t.addKeyframe(model.getDurationSec(), 100f);
            model.getTracks().add(t);
            tracksCanvas.repaint();
        }
    }
}
