package com.atomgdx.theme.timeline;

/**
 * 4 Core Animation Architectures supported in AtomGDX Studio.
 */
public enum AnimationType {
    NODE_PROPERTIES("Node Properties", "Animate transform, rotation, scale, color, opacity & custom properties"),
    SKELETAL_2D("Skeletal 2D (Spine/DragonBones)", "Bone hierarchies, rotations, IK constraints, and slot attachment swapping"),
    SKELETON_3D("Skeleton 3D (glTF Rig)", "3D bone rigs, joint quaternions, blend shapes/morph targets, and root motion"),
    SPRITE_FRAMES_EX("SpriteFrames & SpriteFrames Ex", "Nested multi-part sprite paperdoll hierarchies and frame-by-frame sequences");

    private final String label;
    private final String description;

    AnimationType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() { return label; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return label; }
}
