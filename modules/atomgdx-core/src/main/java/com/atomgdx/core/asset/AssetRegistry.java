package com.atomgdx.core.asset;

import com.atomgdx.core.project.LibGdxProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry and scanner for game assets in a LibGDX project.
 */
public class AssetRegistry {
    private final LibGdxProject project;
    private final List<AssetItem> assets = new CopyOnWriteArrayList<>();
    private final List<AssetChangeListener> listeners = new CopyOnWriteArrayList<>();

    public AssetRegistry(LibGdxProject project) {
        this.project = project;
        scanAssets();
    }

    public synchronized void scanAssets() {
        assets.clear();
        if (project == null) return;

        File assetsDir = project.getAssetsDirectory();
        if (assetsDir.exists() && assetsDir.isDirectory()) {
            scanDirectory(assetsDir, assetsDir);
        }
    }

    private void scanDirectory(File dir, File rootAssetsDir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, rootAssetsDir);
            } else {
                String relativePath = rootAssetsDir.toURI().relativize(file.toURI()).getPath();
                AssetType type = AssetType.fromFileName(file.getName());
                AssetItem item = new AssetItem(file, relativePath, type);
                assets.add(item);
            }
        }
    }

    public List<AssetItem> getAssets() {
        return Collections.unmodifiableList(assets);
    }

    public List<AssetItem> getAssetsByType(AssetType type) {
        List<AssetItem> filtered = new ArrayList<>();
        for (AssetItem item : assets) {
            if (item.getType() == type) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    public AssetItem findByRelativePath(String relativePath) {
        for (AssetItem item : assets) {
            if (item.getRelativePath().equalsIgnoreCase(relativePath)) {
                return item;
            }
        }
        return null;
    }

    public void addListener(AssetChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AssetChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyAssetChanged(AssetItem item, AssetChangeEvent.Kind kind) {
        AssetChangeEvent event = new AssetChangeEvent(item, kind);
        for (AssetChangeListener listener : listeners) {
            listener.onAssetChanged(event);
        }
    }
}
