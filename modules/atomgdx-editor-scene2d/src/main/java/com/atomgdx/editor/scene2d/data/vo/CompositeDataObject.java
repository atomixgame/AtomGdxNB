package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite container storing all child items and layers in a scene or nested composite.
 */
public class CompositeDataObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<LayerItemVO> layers = new ArrayList<>();
    public List<SimpleImageVO> sImages = new ArrayList<>();
    public List<CompositeItemVO> sComposites = new ArrayList<>();
    public List<ParticleEffectVO> sParticleEffects = new ArrayList<>();
    public List<LightVO> sLights = new ArrayList<>();
    public List<LabelVO> sLabels = new ArrayList<>();
    public List<NinePatchVO> sNinePatches = new ArrayList<>();

    public CompositeDataObject() {
        layers.add(new LayerItemVO("Default Layer"));
    }

    public LayerItemVO getLayerByName(String name) {
        for (LayerItemVO l : layers) {
            if (l.layerName.equals(name)) return l;
        }
        return null;
    }
}
