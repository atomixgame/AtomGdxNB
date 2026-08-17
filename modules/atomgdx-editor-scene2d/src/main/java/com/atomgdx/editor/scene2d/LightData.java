package com.atomgdx.editor.scene2d;

import java.io.Serializable;

/**
 * Lighting data for Box2DLights (PointLight, ConeLight, DirectionalLight).
 */
public class LightData implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum LightType {
        POINT_LIGHT,
        CONE_LIGHT,
        DIRECTIONAL_LIGHT
    }

    private LightType lightType = LightType.POINT_LIGHT;
    private String colorHex = "00f0ff";
    private float distance = 300.0f;
    private float coneAngle = 45.0f;
    private float directionDegree = 0.0f;
    private int rayCount = 128;
    private boolean isStatic = false;
    private boolean isXray = false;

    public LightData() {
    }

    public LightType getLightType() { return lightType; }
    public void setLightType(LightType lightType) { this.lightType = lightType; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }

    public float getConeAngle() { return coneAngle; }
    public void setConeAngle(float coneAngle) { this.coneAngle = coneAngle; }

    public float getDirectionDegree() { return directionDegree; }
    public void setDirectionDegree(float directionDegree) { this.directionDegree = directionDegree; }

    public int getRayCount() { return rayCount; }
    public void setRayCount(int rayCount) { this.rayCount = rayCount; }

    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean aStatic) { isStatic = aStatic; }

    public boolean isXray() { return isXray; }
    public void setXray(boolean xray) { isXray = xray; }
}
