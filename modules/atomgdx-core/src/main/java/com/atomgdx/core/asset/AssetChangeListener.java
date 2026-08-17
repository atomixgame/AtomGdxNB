package com.atomgdx.core.asset;

/**
 * Listener interface for asset change notifications.
 */
@FunctionalInterface
public interface AssetChangeListener {
    void onAssetChanged(AssetChangeEvent event);
}
