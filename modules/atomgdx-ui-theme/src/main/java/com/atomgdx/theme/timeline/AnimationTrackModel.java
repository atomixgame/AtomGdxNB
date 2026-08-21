package com.atomgdx.theme.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Advanced Dope-Sheet & Animation Timeline Data Model supporting 4 animation types:
 * Node Properties, Skeletal 2D (Spine/DragonBones), Skeleton 3D (glTF rig), and SpriteFrames Ex.
 */
public class AnimationTrackModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TrackType {
        SPRITE_FRAME("Sprite Frame", 0xFF00E5FF),
        POSITION_X("Position X", 0xFFFF5555),
        POSITION_Y("Position Y", 0xFF50FA7B),
        POSITION_Z("Position Z", 0xFF8BE9FD),
        ROTATION("Rotation", 0xFFFFB86C),
        SCALE_X("Scale X", 0xFFBD93F9),
        SCALE_Y("Scale Y", 0xFFBD93F9),
        OPACITY("Opacity", 0xFFF1FA8C),
        BONE_ROTATION("Bone Rotation", 0xFFFF79C6),
        BONE_TRANSLATION("Bone Translation", 0xFFFFB86C),
        IK_WEIGHT("IK Weight", 0xFF50FA7B),
        MORPH_TARGET("Morph Target", 0xFF8BE9FD),
        ATTACHMENT_SLOT("Slot Attachment", 0xFF00E5FF),
        CUSTOM_FLOAT("Custom Float", 0xFFE0E0E0);

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
        public String stringValue = "";
        public KeyframeEasing easing = KeyframeEasing.LINEAR;

        public Keyframe(float timeSec, float value) {
            this.timeSec = timeSec;
            this.value = value;
        }

        public Keyframe(float timeSec, float value, KeyframeEasing easing) {
            this.timeSec = timeSec;
            this.value = value;
            this.easing = easing != null ? easing : KeyframeEasing.LINEAR;
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
        private boolean isLocked = false;

        public Track(String name, TrackType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TrackType getType() { return type; }
        public List<Keyframe> getKeyframes() { return keyframes; }
        public boolean isMuted() { return isMuted; }
        public void setMuted(boolean muted) { isMuted = muted; }
        public boolean isLocked() { return isLocked; }
        public void setLocked(boolean locked) { isLocked = locked; }

        public void addKeyframe(float timeSec, float value) {
            addKeyframe(timeSec, value, KeyframeEasing.LINEAR);
        }

        public void addKeyframe(float timeSec, float value, KeyframeEasing easing) {
            keyframes.removeIf(k -> Math.abs(k.timeSec - timeSec) < 0.001f);
            keyframes.add(new Keyframe(timeSec, value, easing));
            Collections.sort(keyframes);
        }

        public float sampleValue(float timeSec) {
            if (keyframes.isEmpty()) return 0f;
            if (keyframes.size() == 1) return keyframes.get(0).value;
            if (timeSec <= keyframes.get(0).timeSec) return keyframes.get(0).value;
            if (timeSec >= keyframes.get(keyframes.size() - 1).timeSec) return keyframes.get(keyframes.size() - 1).value;

            for (int i = 0; i < keyframes.size() - 1; i++) {
                Keyframe k1 = keyframes.get(i);
                Keyframe k2 = keyframes.get(i + 1);
                if (timeSec >= k1.timeSec && timeSec <= k2.timeSec) {
                    float t = (timeSec - k1.timeSec) / (k2.timeSec - k1.timeSec);
                    return k1.easing.interpolate(t, k1.value, k2.value);
                }
            }
            return keyframes.get(0).value;
        }
    }

    private AnimationType animationType = AnimationType.NODE_PROPERTIES;
    private String animationName = "Hero_Action_Clip";
    private float durationSec = 4.0f;
    private int fps = 60;
    private float playbackSpeed = 1.0f;
    private boolean onionSkinning = false;
    private boolean snapToGrid = true;

    private final List<Track> tracks = new ArrayList<>();
    private final List<TimelineEvent> events = new ArrayList<>();
    private final List<SkeletalBone2D> skeletalBones = new ArrayList<>();
    private final List<SkeletonJoint3D> skeletonJoints = new ArrayList<>();
    private final List<SpriteFrameExNode> spriteNodes = new ArrayList<>();

    public AnimationTrackModel() {
        initDefaultSampleTracks();
    }

    public void initDefaultSampleTracks() {
        tracks.clear();
        events.clear();

        // Sample Events
        events.add(new TimelineEvent(0.8f, "playFootstep", TimelineEvent.PayloadType.AUDIO_CUE, "audio/sfx/footstep_metal.ogg"));
        events.add(new TimelineEvent(2.2f, "spawnHitbox", TimelineEvent.PayloadType.STRING, "Slash_Collider_01"));
        events.add(new TimelineEvent(3.8f, "onAnimComplete", TimelineEvent.PayloadType.INT, "1"));

        // Default Node Properties tracks
        Track t1 = new Track("Hero.Position_X", TrackType.POSITION_X);
        t1.addKeyframe(0.0f, 100f, KeyframeEasing.EASE_IN_OUT);
        t1.addKeyframe(1.5f, 350f, KeyframeEasing.EASE_IN_OUT);
        t1.addKeyframe(3.0f, 600f, KeyframeEasing.EASE_OUT);
        t1.addKeyframe(4.0f, 100f, KeyframeEasing.LINEAR);
        tracks.add(t1);

        Track t2 = new Track("Hero.Position_Y", TrackType.POSITION_Y);
        t2.addKeyframe(0.0f, 250f, KeyframeEasing.LINEAR);
        t2.addKeyframe(1.0f, 400f, KeyframeEasing.EASE_OUT);
        t2.addKeyframe(2.0f, 250f, KeyframeEasing.BOUNCE);
        t2.addKeyframe(4.0f, 250f, KeyframeEasing.LINEAR);
        tracks.add(t2);

        Track t3 = new Track("Hero.Rotation", TrackType.ROTATION);
        t3.addKeyframe(0.0f, 0f, KeyframeEasing.EASE_IN_OUT);
        t3.addKeyframe(2.0f, 180f, KeyframeEasing.EASE_IN_OUT);
        t3.addKeyframe(4.0f, 360f, KeyframeEasing.LINEAR);
        tracks.add(t3);

        Track t4 = new Track("Weapon.Bone_Rotation", TrackType.BONE_ROTATION);
        t4.addKeyframe(0.0f, -20f, KeyframeEasing.LINEAR);
        t4.addKeyframe(2.2f, 85f, KeyframeEasing.ELASTIC);
        t4.addKeyframe(4.0f, -20f, KeyframeEasing.EASE_IN_OUT);
        tracks.add(t4);
    }

    public AnimationType getAnimationType() { return animationType; }
    public void setAnimationType(AnimationType type) {
        this.animationType = type != null ? type : AnimationType.NODE_PROPERTIES;
    }

    public String getAnimationName() { return animationName; }
    public void setAnimationName(String name) { this.animationName = name; }
    public float getDurationSec() { return durationSec; }
    public void setDurationSec(float durationSec) { this.durationSec = Math.max(0.1f, durationSec); }
    public int getFps() { return fps; }
    public void setFps(int fps) { this.fps = Math.max(1, fps); }
    public float getPlaybackSpeed() { return playbackSpeed; }
    public void setPlaybackSpeed(float speed) { this.playbackSpeed = speed; }
    public boolean isOnionSkinning() { return onionSkinning; }
    public void setOnionSkinning(boolean enable) { this.onionSkinning = enable; }
    public boolean isSnapToGrid() { return snapToGrid; }
    public void setSnapToGrid(boolean snap) { this.snapToGrid = snap; }

    public List<Track> getTracks() { return tracks; }
    public List<TimelineEvent> getEvents() { return events; }
    public List<SkeletalBone2D> getSkeletalBones() { return skeletalBones; }
    public List<SkeletonJoint3D> getSkeletonJoints() { return skeletonJoints; }
    public List<SpriteFrameExNode> getSpriteNodes() { return spriteNodes; }

    public void addEvent(float timeSec, String eventName) {
        events.add(new TimelineEvent(timeSec, eventName));
        Collections.sort(events);
    }

    public void removeEvent(TimelineEvent e) {
        events.remove(e);
    }
}
