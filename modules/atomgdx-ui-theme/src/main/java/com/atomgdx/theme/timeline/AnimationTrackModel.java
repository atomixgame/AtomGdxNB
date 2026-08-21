package com.atomgdx.theme.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data model for Dope-Sheet Animation Keyframe Tracks.
 */
public class AnimationTrackModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TrackType {
        SPRITE_FRAME("Sprite Frame", 0xFF00E5FF),
        POSITION_X("Position X", 0xFFFF5555),
        POSITION_Y("Position Y", 0xFF50FA7B),
        ROTATION("Rotation", 0xFFFFB86C),
        SCALE("Scale", 0xFFBD93F9),
        OPACITY("Opacity", 0xFFF1FA8C),
        EVENT("Trigger Event", 0xFFFF79C6);

        private final String label;
        private final int colorRgb;

        TrackType(String label, int colorRgb) {
            this.label = label;
            this.colorRgb = colorRgb;
        }

        public String getLabel() { return label; }
        public int getColorRgb() { return colorRgb; }
    }

    public static class Keyframe implements Serializable, Comparable<Keyframe> {
        public float timeSec;
        public float value;
        public String eventName;

        public Keyframe(float timeSec, float value) {
            this.timeSec = timeSec;
            this.value = value;
            this.eventName = "";
        }

        public Keyframe(float timeSec, String eventName) {
            this.timeSec = timeSec;
            this.value = 0f;
            this.eventName = eventName;
        }

        @Override
        public int compareTo(Keyframe o) {
            return Float.compare(this.timeSec, o.timeSec);
        }
    }

    public static class Track implements Serializable {
        private String name;
        private TrackType type;
        private final List<Keyframe> keyframes = new ArrayList<>();
        private boolean isMuted = false;

        public Track(String name, TrackType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public TrackType getType() { return type; }
        public List<Keyframe> getKeyframes() { return keyframes; }
        public boolean isMuted() { return isMuted; }
        public void setMuted(boolean muted) { isMuted = muted; }

        public void addKeyframe(float timeSec, float value) {
            keyframes.add(new Keyframe(timeSec, value));
            Collections.sort(keyframes);
        }
    }

    private final List<Track> tracks = new ArrayList<>();
    private float durationSec = 4.0f;
    private int fps = 60;
    private String animationName = "Walk_Cycle";

    public AnimationTrackModel() {
        // Sample default tracks
        Track t1 = new Track("player_ship.Position_X", TrackType.POSITION_X);
        t1.addKeyframe(0.0f, 100f);
        t1.addKeyframe(1.0f, 250f);
        t1.addKeyframe(2.5f, 500f);
        t1.addKeyframe(4.0f, 100f);
        tracks.add(t1);

        Track t2 = new Track("player_ship.Position_Y", TrackType.POSITION_Y);
        t2.addKeyframe(0.0f, 300f);
        t2.addKeyframe(2.0f, 450f);
        t2.addKeyframe(4.0f, 300f);
        tracks.add(t2);

        Track t3 = new Track("player_ship.Rotation", TrackType.ROTATION);
        t3.addKeyframe(0.0f, 0f);
        t3.addKeyframe(1.5f, 45f);
        t3.addKeyframe(3.0f, -45f);
        t3.addKeyframe(4.0f, 0f);
        tracks.add(t3);

        Track t4 = new Track("engine_burn.Sprite_Frame", TrackType.SPRITE_FRAME);
        t4.addKeyframe(0.0f, 0f);
        t4.addKeyframe(0.5f, 1f);
        t4.addKeyframe(1.0f, 2f);
        t4.addKeyframe(1.5f, 3f);
        t4.addKeyframe(2.0f, 0f);
        tracks.add(t4);
    }

    public List<Track> getTracks() { return tracks; }
    public float getDurationSec() { return durationSec; }
    public void setDurationSec(float durationSec) { this.durationSec = durationSec; }
    public int getFps() { return fps; }
    public void setFps(int fps) { this.fps = fps; }
    public String getAnimationName() { return animationName; }
    public void setAnimationName(String name) { this.animationName = name; }
}
