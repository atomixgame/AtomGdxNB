package com.atomgdx.theme.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 3D Skeleton Joint / Bone definition for glTF 2.0 / g3db rigs.
 */
public class SkeletonJoint3D implements Serializable {
    private static final long serialVersionUID = 1L;

    public String jointName;
    public int jointIndex;
    public float posX, posY, posZ;
    public float rotQuatX, rotQuatY, rotQuatZ, rotQuatW = 1.0f;
    public float scaleX = 1f, scaleY = 1f, scaleZ = 1f;
    public final List<SkeletonJoint3D> childJoints = new ArrayList<>();

    public SkeletonJoint3D(String jointName, int jointIndex) {
        this.jointName = jointName;
        this.jointIndex = jointIndex;
    }
}
