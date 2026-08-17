package com.atomgdx.core.asset;

/**
 * Event fired when an asset in the project is added, modified, or deleted.
 */
public class AssetChangeEvent {
    public enum Kind {
        ADDED,
        MODIFIED,
        DELETED
    }

    private final AssetItem asset;
    private final Kind kind;

    public AssetChangeEvent(AssetItem asset, Kind kind) {
        this.asset = asset;
        this.kind = kind;
    }

    public AssetItem getAsset() {
        return asset;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return "AssetChangeEvent{" + kind + ": " + asset.getRelativePath() + "}";
    }
}
