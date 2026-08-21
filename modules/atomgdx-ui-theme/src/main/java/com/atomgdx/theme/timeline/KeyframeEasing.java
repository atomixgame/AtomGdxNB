package com.atomgdx.theme.timeline;

/**
 * Keyframe easing and interpolation curves.
 */
public enum KeyframeEasing {
    LINEAR("Linear"),
    STEP("Constant / Step"),
    EASE_IN("Ease In (Quadratic)"),
    EASE_OUT("Ease Out (Quadratic)"),
    EASE_IN_OUT("Ease In-Out (Cubic Bezier)"),
    BOUNCE("Bounce"),
    ELASTIC("Elastic");

    private final String label;

    KeyframeEasing(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public float interpolate(float t, float v1, float v2) {
        float f;
        switch (this) {
            case STEP:
                return t >= 1.0f ? v2 : v1;
            case EASE_IN:
                f = t * t;
                break;
            case EASE_OUT:
                f = t * (2 - t);
                break;
            case EASE_IN_OUT:
                f = t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
                break;
            case BOUNCE:
                if (t < (1 / 2.75f)) {
                    f = 7.5625f * t * t;
                } else if (t < (2 / 2.75f)) {
                    f = 7.5625f * (t -= (1.5f / 2.75f)) * t + 0.75f;
                } else if (t < (2.5 / 2.75f)) {
                    f = 7.5625f * (t -= (2.25f / 2.75f)) * t + 0.9375f;
                } else {
                    f = 7.5625f * (t -= (2.625f / 2.75f)) * t + 0.984375f;
                }
                break;
            case ELASTIC:
                if (t <= 0) return v1;
                if (t >= 1) return v2;
                f = (float) (-Math.pow(2, 10 * (t - 1)) * Math.sin((t - 1.1f) * 5 * Math.PI));
                break;
            case LINEAR:
            default:
                f = t;
                break;
        }
        return v1 + (v2 - v1) * Math.max(0f, Math.min(1f, f));
    }

    @Override
    public String toString() { return label; }
}
