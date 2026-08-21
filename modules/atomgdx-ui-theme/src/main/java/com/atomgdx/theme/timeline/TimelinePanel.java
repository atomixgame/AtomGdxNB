package com.atomgdx.theme.timeline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Advanced Timeline & Multi-Track Animation Studio supporting Node Properties,
 * 2D Skeletal (Spine/DragonBones), 3D Skeletal Rigs (glTF), and SpriteFrames Ex.
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
    private final JComboBox<AnimationType> animTypeCombo = new JComboBox<>(AnimationType.values());
    private final JComboBox<String> speedCombo = new JComboBox<>(new String[]{"0.25x", "0.5x", "1.0x", "1.5x", "2.0x", "4.0x"});
    private final JComboBox<KeyframeEasing> easingCombo = new JComboBox<>(KeyframeEasing.values());
    private final JPanel tracksCanvas;

    // Selected keyframe for editing easing / value
    private AnimationTrackModel.Track selectedTrack = null;
    private AnimationTrackModel.Keyframe selectedKeyframe = null;

    public TimelinePanel() {
        this(new AnimationTrackModel());
    }

    public TimelinePanel(AnimationTrackModel model) {
        this.model = model != null ? model : new AnimationTrackModel();

        // 1. Initialize canvas first so all listeners can safely reference it
        tracksCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderTimelineCanvas((Graphics2D) g);
            }
        };
        tracksCanvas.setBackground(new Color(16, 17, 20));
        setupCanvasInteractions();

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(20, 21, 24));

        // Top Control Toolbar
        JPanel topToolbars = new JPanel(new GridLayout(2, 1, 0, 2));
        topToolbars.setOpaque(false);

        // Row 1: Mode, Clip Name, Speed, Onion Skin, Add Track/Event
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row1.setBackground(new Color(28, 30, 34));

        row1.add(createHeaderLabel("Anim Mode:"));
        animTypeCombo.setSelectedItem(this.model.getAnimationType());
        animTypeCombo.addActionListener(e -> {
            this.model.setAnimationType((AnimationType) animTypeCombo.getSelectedItem());
            loadAnimationTypeTemplate(this.model.getAnimationType());
            tracksCanvas.repaint();
        });
        row1.add(animTypeCombo);

        row1.add(createHeaderLabel("Clip:"));
        JTextField clipField = new JTextField(this.model.getAnimationName(), 10);
        clipField.addActionListener(e -> this.model.setAnimationName(clipField.getText().trim()));
        row1.add(clipField);

        row1.add(createHeaderLabel("Speed:"));
        speedCombo.setSelectedItem("1.0x");
        speedCombo.addActionListener(e -> {
            String s = (String) speedCombo.getSelectedItem();
            if (s != null) {
                float mult = Float.parseFloat(s.replace("x", ""));
                this.model.setPlaybackSpeed(mult);
            }
        });
        row1.add(speedCombo);

        JToggleButton onionBtn = new JToggleButton("🧅 Onion Skin", this.model.isOnionSkinning());
        onionBtn.setToolTipText("Enable Ghost Onion Skinning for previous/next frames");
        onionBtn.addActionListener(e -> this.model.setOnionSkinning(onionBtn.isSelected()));
        row1.add(onionBtn);

        JToggleButton snapBtn = new JToggleButton("🧲 Snap", this.model.isSnapToGrid());
        snapBtn.setToolTipText("Snap keyframes to FPS frames");
        snapBtn.addActionListener(e -> this.model.setSnapToGrid(snapBtn.isSelected()));
        row1.add(snapBtn);

        row1.add(createHeaderLabel("Key Easing:"));
        easingCombo.addActionListener(e -> {
            if (selectedKeyframe != null) {
                selectedKeyframe.easing = (KeyframeEasing) easingCombo.getSelectedItem();
                tracksCanvas.repaint();
            }
        });
        row1.add(easingCombo);

        topToolbars.add(row1);

        // Row 2: Transport bar
        JPanel row2 = new JPanel(new BorderLayout());
        row2.setBackground(new Color(32, 34, 38));
        row2.setBorder(new EmptyBorder(2, 6, 2, 6));

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        leftControls.setOpaque(false);

        JButton firstBtn = new JButton("⏮");
        firstBtn.setToolTipText("First Frame (0s)");
        firstBtn.addActionListener(e -> setTime(0f));
        leftControls.add(firstBtn);

        JButton prevBtn = new JButton("◀");
        prevBtn.setToolTipText("Step Backward (1 frame)");
        prevBtn.addActionListener(e -> setTime(Math.max(0f, currentTime - (1f / this.model.getFps()))));
        leftControls.add(prevBtn);

        playPauseBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        playPauseBtn.setBackground(new Color(44, 93, 212));
        playPauseBtn.setForeground(Color.WHITE);
        playPauseBtn.addActionListener(e -> togglePlayback());
        leftControls.add(playPauseBtn);

        JButton nextBtn = new JButton("▶");
        nextBtn.setToolTipText("Step Forward (1 frame)");
        nextBtn.addActionListener(e -> setTime(Math.min(this.model.getDurationSec(), currentTime + (1f / this.model.getFps()))));
        leftControls.add(nextBtn);

        JButton lastBtn = new JButton("⏭");
        lastBtn.setToolTipText("Last Frame");
        lastBtn.addActionListener(e -> setTime(this.model.getDurationSec()));
        leftControls.add(lastBtn);

        JToggleButton loopToggle = new JToggleButton("🔁 Loop", isLooping);
        loopToggle.addActionListener(e -> isLooping = loopToggle.isSelected());
        leftControls.add(loopToggle);

        row2.add(leftControls, BorderLayout.WEST);

        // Center Scrubber Slider
        timeScrubber.setBackground(new Color(32, 34, 38));
        timeScrubber.addChangeListener(e -> {
            if (timeScrubber.getValueIsAdjusting()) {
                currentTime = (timeScrubber.getValue() / 100f);
                updateLabelsAndRepaint();
            }
        });
        row2.add(timeScrubber, BorderLayout.CENTER);

        // Right side: Info readout and + Add buttons
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightActions.setOpaque(false);

        timeLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        timeLabel.setForeground(new Color(0, 220, 255));
        rightActions.add(timeLabel);

        JButton addEventBtn = new JButton("+ Event");
        addEventBtn.setBackground(new Color(255, 121, 198));
        addEventBtn.setForeground(Color.BLACK);
        addEventBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addEventBtn.addActionListener(e -> {
            TimelineEvent ev = new TimelineEvent(currentTime, "onCustomEvent");
            EventEditorDialog dlg = new EventEditorDialog(null, ev);
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                this.model.getEvents().add(ev);
                tracksCanvas.repaint();
            }
        });
        rightActions.add(addEventBtn);

        JButton addTrackBtn = new JButton("+ Track");
        addTrackBtn.addActionListener(e -> addNewTrack());
        rightActions.add(addTrackBtn);

        row2.add(rightActions, BorderLayout.EAST);
        topToolbars.add(row2);

        add(topToolbars, BorderLayout.NORTH);
        add(new JScrollPane(tracksCanvas), BorderLayout.CENTER);

        // 60 FPS playback timer with speed multiplier
        playbackTimer = new Timer(16, e -> {
            if (isPlaying) {
                float dt = (0.016f) * model.getPlaybackSpeed();
                currentTime += dt;
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

    private void renderTimelineCanvas(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = tracksCanvas.getWidth();
        int h = tracksCanvas.getHeight();
        int headerW = 200;
        int eventH = 22;
        int rulerH = 24;
        int topOffset = eventH + rulerH;

        // Backgrounds
        g2.setColor(new Color(24, 25, 28));
        g2.fillRect(0, 0, headerW, h);
        g2.setColor(new Color(15, 16, 19));
        g2.fillRect(headerW, 0, w - headerW, h);

        float timelineW = Math.max(100, w - headerW - 24);
        float duration = model.getDurationSec();

        // 1. Events Header & Track (Pink accent)
        g2.setColor(new Color(36, 26, 38));
        g2.fillRect(0, 0, headerW, eventH);
        g2.setColor(new Color(255, 121, 198));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString("⚡ EVENTS", 10, 15);

        g2.setColor(new Color(24, 18, 26));
        g2.fillRect(headerW, 0, (int) timelineW + 10, eventH);
        g2.setColor(new Color(65, 45, 68));
        g2.drawLine(0, eventH, w, eventH);

        // Render Events markers
        for (TimelineEvent ev : model.getEvents()) {
            int ex = headerW + (int) ((ev.timeSec / duration) * timelineW);
            g2.setColor(new Color(255, 121, 198));
            Polygon flag = new Polygon(
                    new int[]{ex, ex + 6, ex - 6},
                    new int[]{2, 14, 14},
                    3
            );
            g2.fill(flag);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.drawString(ev.eventName, ex + 8, 14);
        }

        // 2. Time Ruler
        g2.setColor(new Color(32, 34, 38));
        g2.fillRect(headerW, eventH, w - headerW, rulerH);
        g2.setColor(new Color(55, 58, 65));
        g2.drawLine(0, topOffset, w, topOffset);

        g2.setColor(new Color(160, 165, 175));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        for (float t = 0f; t <= duration; t += 0.5f) {
            int tx = headerW + (int) ((t / duration) * timelineW);
            int tickH = (t % 1.0f == 0) ? 12 : 6;
            g2.drawLine(tx, topOffset - tickH, tx, topOffset);
            if (t % 1.0f == 0) {
                g2.drawString(String.format("%.1fs", t), tx + 2, eventH + 14);
            }
        }

        // 3. Render Tracks
        int rowH = 32;
        int y = topOffset;

        for (AnimationTrackModel.Track track : model.getTracks()) {
            // Track header
            boolean isSel = (track == selectedTrack);
            g2.setColor(isSel ? new Color(38, 42, 50) : new Color(26, 28, 32));
            g2.fillRect(0, y, headerW, rowH);
            g2.setColor(new Color(42, 45, 50));
            g2.drawLine(0, y + rowH, w, y + rowH);

            // Track dot
            g2.setColor(new Color(track.getType().getColorRgb()));
            g2.fillOval(8, y + 10, 10, 10);

            // Track Name
            g2.setColor(track.isMuted() ? Color.GRAY : Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(track.getName(), 24, y + 20);

            // Track lane
            g2.setColor(new Color(20, 21, 25));
            g2.fillRect(headerW, y + 2, (int) timelineW + 10, rowH - 4);

            // Keyframe Diamonds ◆
            for (AnimationTrackModel.Keyframe kf : track.getKeyframes()) {
                int kx = headerW + (int) ((kf.timeSec / duration) * timelineW);
                int ky = y + (rowH / 2);
                boolean isKeySel = (kf == selectedKeyframe);

                g2.setColor(new Color(track.getType().getColorRgb()));
                Polygon diamond = new Polygon(
                        new int[]{kx, kx + 6, kx, kx - 6},
                        new int[]{ky - 6, ky, ky + 6, ky},
                        4
                );
                g2.fill(diamond);

                if (isKeySel) {
                    g2.setColor(new Color(0, 220, 255));
                    g2.setStroke(new BasicStroke(2.0f));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.0f));
                }
                g2.draw(diamond);
            }

            y += rowH;
        }

        // 4. Red Playhead Scrubber Line
        int playheadX = headerW + (int) ((currentTime / duration) * timelineW);
        g2.setColor(new Color(255, 60, 60));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(playheadX, 0, playheadX, h);

        // Playhead handle
        Polygon head = new Polygon(
                new int[]{playheadX - 7, playheadX + 7, playheadX + 7, playheadX, playheadX - 7},
                new int[]{eventH, eventH, eventH + 8, eventH + 15, eventH + 8},
                5
        );
        g2.fill(head);
    }

    private void setupCanvasInteractions() {
        tracksCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int headerW = 200;
                int eventH = 22;
                int topOffset = eventH + 24;

                // Check Event marker click
                if (e.getY() <= eventH && e.getX() >= headerW) {
                    float duration = model.getDurationSec();
                    float timelineW = Math.max(100, tracksCanvas.getWidth() - headerW - 24);
                    for (TimelineEvent ev : model.getEvents()) {
                        int ex = headerW + (int) ((ev.timeSec / duration) * timelineW);
                        if (Math.abs(e.getX() - ex) <= 8) {
                            if (e.getClickCount() == 2) {
                                EventEditorDialog dlg = new EventEditorDialog(null, ev);
                                dlg.setVisible(true);
                                tracksCanvas.repaint();
                            }
                            return;
                        }
                    }
                }

                // Check Track & Keyframe click
                if (e.getY() > topOffset) {
                    int trackIdx = (e.getY() - topOffset) / 32;
                    if (trackIdx >= 0 && trackIdx < model.getTracks().size()) {
                        selectedTrack = model.getTracks().get(trackIdx);
                        if (e.getX() >= headerW) {
                            float duration = model.getDurationSec();
                            float timelineW = Math.max(100, tracksCanvas.getWidth() - headerW - 24);
                            selectedKeyframe = null;
                            for (AnimationTrackModel.Keyframe kf : selectedTrack.getKeyframes()) {
                                int kx = headerW + (int) ((kf.timeSec / duration) * timelineW);
                                if (Math.abs(e.getX() - kx) <= 8) {
                                    selectedKeyframe = kf;
                                    easingCombo.setSelectedItem(kf.easing);
                                    break;
                                }
                            }
                        }
                    }
                }

                scrubToMouse(e.getX());
            }
        });

        tracksCanvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                scrubToMouse(e.getX());
            }
        });
    }

    private void scrubToMouse(int mx) {
        int headerW = 200;
        int timelineW = tracksCanvas.getWidth() - headerW - 24;
        if (timelineW > 0 && mx >= headerW) {
            float frac = (float) (mx - headerW) / (float) timelineW;
            float rawTime = frac * model.getDurationSec();
            if (model.isSnapToGrid()) {
                float frameStep = 1f / model.getFps();
                rawTime = Math.round(rawTime / frameStep) * frameStep;
            }
            currentTime = Math.max(0f, Math.min(model.getDurationSec(), rawTime));
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
        int currentFrame = (int) (currentTime * model.getFps());
        int totalFrames = (int) (model.getDurationSec() * model.getFps());
        timeLabel.setText(String.format("%02d:%05.2f / %02d:%05.2f (F%d/%d)",
                (int) (currentTime / 60), currentTime % 60,
                (int) (model.getDurationSec() / 60), model.getDurationSec() % 60,
                currentFrame, totalFrames));
        tracksCanvas.repaint();
    }

    private void addNewTrack() {
        String name = JOptionPane.showInputDialog(this, "Enter Track Target (e.g. Hero.Bones.Arm_L.Rotation):", "Add Animation Track", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            AnimationTrackModel.Track t = new AnimationTrackModel.Track(name.trim(), AnimationTrackModel.TrackType.POSITION_X);
            t.addKeyframe(0f, 0f);
            t.addKeyframe(model.getDurationSec(), 100f);
            model.getTracks().add(t);
            tracksCanvas.repaint();
        }
    }

    private void loadAnimationTypeTemplate(AnimationType type) {
        model.getTracks().clear();
        switch (type) {
            case SKELETAL_2D:
                model.setAnimationName("Spine_Walk_Loop");
                addDefaultTrack("Root.Bone_Translation", AnimationTrackModel.TrackType.BONE_TRANSLATION, 0f, 10f);
                addDefaultTrack("Torso.Bone_Rotation", AnimationTrackModel.TrackType.BONE_ROTATION, -5f, 15f);
                addDefaultTrack("Arm_Left.Bone_Rotation", AnimationTrackModel.TrackType.BONE_ROTATION, -30f, 40f);
                addDefaultTrack("Arm_Right.Bone_Rotation", AnimationTrackModel.TrackType.BONE_ROTATION, 40f, -30f);
                addDefaultTrack("Leg_Left.Bone_Rotation", AnimationTrackModel.TrackType.BONE_ROTATION, 45f, -45f);
                addDefaultTrack("Leg_Right.Bone_Rotation", AnimationTrackModel.TrackType.BONE_ROTATION, -45f, 45f);
                addDefaultTrack("Weapon_Slot.Attachment", AnimationTrackModel.TrackType.ATTACHMENT_SLOT, 0f, 1f);
                break;
            case SKELETON_3D:
                model.setAnimationName("g3db_Combat_Combo");
                addDefaultTrack("Hips.Position_Y", AnimationTrackModel.TrackType.POSITION_Y, 0.9f, 1.2f);
                addDefaultTrack("Spine.Rotation", AnimationTrackModel.TrackType.ROTATION, 0f, 45f);
                addDefaultTrack("RightShoulder.Rotation", AnimationTrackModel.TrackType.ROTATION, -20f, 90f);
                addDefaultTrack("Face_Morph.Smile", AnimationTrackModel.TrackType.MORPH_TARGET, 0f, 1.0f);
                addDefaultTrack("RootMotion.Forward_Z", AnimationTrackModel.TrackType.POSITION_Z, 0f, 3.5f);
                break;
            case SPRITE_FRAMES_EX:
                model.setAnimationName("Paperdoll_Slash_Ex");
                addDefaultTrack("Torso.Sprite_Frame", AnimationTrackModel.TrackType.SPRITE_FRAME, 0f, 7f);
                addDefaultTrack("Head.Sprite_Frame", AnimationTrackModel.TrackType.SPRITE_FRAME, 0f, 7f);
                addDefaultTrack("Weapon_Overlay.Sprite_Frame", AnimationTrackModel.TrackType.SPRITE_FRAME, 0f, 7f);
                addDefaultTrack("FX_Slash_Glow.Opacity", AnimationTrackModel.TrackType.OPACITY, 0f, 1.0f);
                break;
            case NODE_PROPERTIES:
            default:
                model.setAnimationName("Transform_Interpolation");
                addDefaultTrack("Player.Position_X", AnimationTrackModel.TrackType.POSITION_X, 100f, 500f);
                addDefaultTrack("Player.Position_Y", AnimationTrackModel.TrackType.POSITION_Y, 200f, 350f);
                addDefaultTrack("Player.Rotation", AnimationTrackModel.TrackType.ROTATION, 0f, 360f);
                addDefaultTrack("Player.Opacity", AnimationTrackModel.TrackType.OPACITY, 1.0f, 0.2f);
                break;
        }
    }

    private void addDefaultTrack(String name, AnimationTrackModel.TrackType type, float startVal, float endVal) {
        AnimationTrackModel.Track t = new AnimationTrackModel.Track(name, type);
        t.addKeyframe(0.0f, startVal, KeyframeEasing.EASE_IN_OUT);
        t.addKeyframe(model.getDurationSec() * 0.5f, (startVal + endVal) * 0.5f, KeyframeEasing.BOUNCE);
        t.addKeyframe(model.getDurationSec(), endVal, KeyframeEasing.LINEAR);
        model.getTracks().add(t);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(190, 195, 205));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }
}
