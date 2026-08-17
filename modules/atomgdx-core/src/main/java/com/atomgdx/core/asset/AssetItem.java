package com.atomgdx.core.asset;

import java.io.File;

/**
 * Represents an individual game asset inside a LibGDX project's assets directory.
 */
public class AssetItem {
    private final File file;
    private final String relativePath;
    private final AssetType type;
    private long lastModified;
    private long sizeBytes;

    public AssetItem(File file, String relativePath, AssetType type) {
        this.file = file;
        this.relativePath = relativePath;
        this.type = type;
        this.lastModified = file.exists() ? file.lastModified() : 0;
        this.sizeBytes = file.exists() ? file.length() : 0;
    }

    public File getFile() {
        return file;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public AssetType getType() {
        return type;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void updateMetadata() {
        if (file.exists()) {
            this.lastModified = file.lastModified();
            this.sizeBytes = file.length();
        }
    }

    @Override
    public String toString() {
        return relativePath + " (" + type.getDescription() + ")";
    }
}
