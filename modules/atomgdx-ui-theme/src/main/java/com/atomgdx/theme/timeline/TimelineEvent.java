package com.atomgdx.theme.timeline;

import java.io.Serializable;

/**
 * Timeline Event Marker with typed parameters and audio cues.
 */
public class TimelineEvent implements Serializable, Comparable<TimelineEvent> {
    private static final long serialVersionUID = 1L;

    public enum PayloadType {
        STRING,
        FLOAT,
        INT,
        AUDIO_CUE
    }

    public float timeSec;
    public String eventName = "onCustomEvent";
    public PayloadType payloadType = PayloadType.STRING;
    public String stringParam = "";
    public float floatParam = 0.0f;
    public int intParam = 0;
    public String audioClipPath = "audio/sfx/footstep_metal.ogg";

    public TimelineEvent(float timeSec, String eventName) {
        this.timeSec = timeSec;
        this.eventName = eventName;
    }

    public TimelineEvent(float timeSec, String eventName, PayloadType type, String stringParam) {
        this.timeSec = timeSec;
        this.eventName = eventName;
        this.payloadType = type;
        this.stringParam = stringParam;
    }

    @Override
    public int compareTo(TimelineEvent o) {
        return Float.compare(this.timeSec, o.timeSec);
    }
}
