package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Value Object representing a complete 2D composite scene definition in HyperLap2D format.
 */
public class SceneVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String sceneName = "MainScene";
    public ColorDataVO ambientColor = new ColorDataVO(1f, 1f, 1f, 1f);
    public boolean physicsPropertiesLoaded = false;
    public boolean lightsPropertiesLoaded = false;
    public float pixelsPerMU = 80f;

    public CompositeDataObject composite = new CompositeDataObject();

    public SceneVO() {}

    public SceneVO(String sceneName) {
        this.sceneName = sceneName;
    }
}
