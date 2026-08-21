package com.atomgdx.theme.timeline;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class AnimationTrackModelTest {

    @Test
    public void testTrackKeyframeInsertionAndSorting() {
        AnimationTrackModel model = new AnimationTrackModel();
        model.setAnimationName("Hero_Run");
        model.setDurationSec(2.0f);
        assertEquals("Hero_Run", model.getAnimationName());
        assertEquals(2.0f, model.getDurationSec(), 0.001f);

        AnimationTrackModel.Track track = new AnimationTrackModel.Track("Position X", AnimationTrackModel.TrackType.POSITION_X);
        track.addKeyframe(0.5f, 100.0f);
        track.addKeyframe(0.0f, 0.0f);
        track.addKeyframe(1.0f, 200.0f);

        model.getTracks().add(track);

        List<AnimationTrackModel.Keyframe> kfs = track.getKeyframes();
        assertEquals(3, kfs.size());
        assertEquals(0.0f, kfs.get(0).timeSec, 0.001f);
        assertEquals(0.5f, kfs.get(1).timeSec, 0.001f);
        assertEquals(1.0f, kfs.get(2).timeSec, 0.001f);
    }
}
