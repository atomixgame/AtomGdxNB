package com.atomgdx.theme.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2D Skeletal Bone node for Spine / DragonBones style skeletal animations.
 */
public class SkeletalBone2D implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String parentName;
    public float length = 40f;
    public float localX = 0f;
    public float localY = 0f;
    public float localRotation = 0f;
    public float scaleX = 1f;
    public float scaleY = 1f;
    public String activeSlotAttachment = "arm_sprite.png";
    public final List<SkeletalBone2D> children = new ArrayList<>();

    public SkeletalBone2D(String name, String parentName) {
        this.name = name;
        this.parentName = parentName;
    }
}
