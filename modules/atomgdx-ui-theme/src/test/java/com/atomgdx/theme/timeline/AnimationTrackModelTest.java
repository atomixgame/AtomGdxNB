package com.atomgdx.theme.timeline;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class AnimationTrackModelTest {

    @Test
    public void testAnimationTypesAndModelProperties() {
        AnimationTrackModel model = new AnimationTrackModel();
        model.setAnimationType(AnimationType.SKELETAL_2D);
        assertEquals(AnimationType.SKELETAL_2D, model.getAnimationType());

        model.setAnimationType(AnimationType.SKELETON_3D);
        assertEquals(AnimationType.SKELETON_3D, model.getAnimationType());

        model.setAnimationType(AnimationType.SPRITE_FRAMES_EX);
        assertEquals(AnimationType.SPRITE_FRAMES_EX, model.getAnimationType());

        model.setDurationSec(5.0f);
        assertEquals(5.0f, model.getDurationSec(), 0.001f);
        model.setPlaybackSpeed(2.0f);
        assertEquals(2.0f, model.getPlaybackSpeed(), 0.001f);
    }

    @Test
    public void testKeyframeEasingInterpolations() {
        // Linear
        assertEquals(0.5f, KeyframeEasing.LINEAR.interpolate(0.5f, 0f, 1f), 0.01f);
        // Step
        assertEquals(0.0f, KeyframeEasing.STEP.interpolate(0.5f, 0f, 1f), 0.01f);
        assertEquals(1.0f, KeyframeEasing.STEP.interpolate(1.0f, 0f, 1f), 0.01f);
        // Ease In
        assertEquals(0.25f, KeyframeEasing.EASE_IN.interpolate(0.5f, 0f, 1f), 0.01f);
        // Ease Out
        assertEquals(0.75f, KeyframeEasing.EASE_OUT.interpolate(0.5f, 0f, 1f), 0.01f);
        // Ease In Out
        assertEquals(0.5f, KeyframeEasing.EASE_IN_OUT.interpolate(0.5f, 0f, 1f), 0.01f);
    }

    @Test
    public void testTimelineEventsSortingAndPayloads() {
        AnimationTrackModel model = new AnimationTrackModel();
        model.getEvents().clear();

        TimelineEvent e1 = new TimelineEvent(2.5f, "spawnVFX");
        TimelineEvent e2 = new TimelineEvent(0.5f, "footstep", TimelineEvent.PayloadType.AUDIO_CUE, "sfx/step.ogg");
        TimelineEvent e3 = new TimelineEvent(4.0f, "die");

        model.getEvents().add(e1);
        model.getEvents().add(e2);
        model.getEvents().add(e3);
        java.util.Collections.sort(model.getEvents());

        assertEquals("footstep", model.getEvents().get(0).eventName);
        assertEquals(0.5f, model.getEvents().get(0).timeSec, 0.001f);
        assertEquals(TimelineEvent.PayloadType.AUDIO_CUE, model.getEvents().get(0).payloadType);
        assertEquals("sfx/step.ogg", model.getEvents().get(0).stringParam);
    }

    @Test
    public void testSkeletalAndSpriteNodesHierarchy() {
        // 2D Skeletal Bone
        SkeletalBone2D root = new SkeletalBone2D("Root", null);
        SkeletalBone2D spine = new SkeletalBone2D("Spine", "Root");
        SkeletalBone2D armL = new SkeletalBone2D("Arm_L", "Spine");
        spine.children.add(armL);
        root.children.add(spine);

        assertEquals("Root", root.name);
        assertEquals(1, root.children.size());
        assertEquals("Spine", root.children.get(0).name);
        assertEquals("Arm_L", root.children.get(0).children.get(0).name);

        // 3D Skeleton Joint
        SkeletonJoint3D hips = new SkeletonJoint3D("Hips", 0);
        SkeletonJoint3D leg = new SkeletonJoint3D("Leg_R", 1);
        hips.childJoints.add(leg);
        assertEquals(1, hips.childJoints.size());

        // SpriteFrames Ex Paperdoll Node
        SpriteFrameExNode torsoNode = new SpriteFrameExNode("Torso", "hero_torso_");
        SpriteFrameExNode headNode = new SpriteFrameExNode("Head", "hero_head_");
        torsoNode.childNodes.add(headNode);
        assertEquals(1, torsoNode.childNodes.size());
        assertEquals("Head", torsoNode.childNodes.get(0).nodeName);
    }

    @Test
    public void testTrackSampleWithEasing() {
        AnimationTrackModel.Track track = new AnimationTrackModel.Track("Opacity", AnimationTrackModel.TrackType.OPACITY);
        track.addKeyframe(0.0f, 0.0f, KeyframeEasing.LINEAR);
        track.addKeyframe(2.0f, 100.0f, KeyframeEasing.LINEAR);

        float val0 = track.sampleValue(0.0f);
        float valMid = track.sampleValue(1.0f);
        float valEnd = track.sampleValue(2.0f);

        assertEquals(0.0f, val0, 0.01f);
        assertEquals(50.0f, valMid, 0.05f);
        assertEquals(100.0f, valEnd, 0.01f);
    }
}
