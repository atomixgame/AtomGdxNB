package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2D Reusable Entity Prefab descriptor for AtomGDX.
 */
public class PrefabVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String prefabName = "NewPrefab";
    public String rootItemName = "Root";
    public MainItemVO rootItem;
    public final List<MainItemVO> children = new ArrayList<>();
    public final List<String> attachedComponents = new ArrayList<>();

    public PrefabVO() {}

    public PrefabVO(String name, MainItemVO rootItem) {
        this.prefabName = name;
        this.rootItem = rootItem;
        if (rootItem != null) {
            this.rootItemName = rootItem.itemIdentifier;
        }
    }
}
