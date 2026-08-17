package com.atomgdx.core.project;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents a LibGDX game project within AtomGdx Studio.
 */
public class LibGdxProject {
    private final String name;
    private final String packageName;
    private final String mainClass;
    private final File rootDirectory;
    private final Set<PlatformType> targetPlatforms;
    private final Set<ExtensionType> extensions;
    private String gdxVersion = "1.13.1";
    private String javaVersion = "21";

    public LibGdxProject(String name, String packageName, String mainClass, File rootDirectory) {
        this.name = name;
        this.packageName = packageName;
        this.mainClass = mainClass;
        this.rootDirectory = rootDirectory;
        this.targetPlatforms = EnumSet.of(PlatformType.CORE, PlatformType.DESKTOP_LWJGL3);
        this.extensions = EnumSet.noneOf(ExtensionType.class);
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getMainClass() {
        return mainClass;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getAssetsDirectory() {
        return new File(rootDirectory, "assets");
    }

    public File getCoreModuleDirectory() {
        return new File(rootDirectory, "core");
    }

    public Set<PlatformType> getTargetPlatforms() {
        return Collections.unmodifiableSet(targetPlatforms);
    }

    public void addTargetPlatform(PlatformType platform) {
        targetPlatforms.add(platform);
    }

    public void removeTargetPlatform(PlatformType platform) {
        if (platform != PlatformType.CORE) {
            targetPlatforms.remove(platform);
        }
    }

    public Set<ExtensionType> getExtensions() {
        return Collections.unmodifiableSet(extensions);
    }

    public void addExtension(ExtensionType extension) {
        extensions.add(extension);
    }

    public void removeExtension(ExtensionType extension) {
        extensions.remove(extension);
    }

    public String getGdxVersion() {
        return gdxVersion;
    }

    public void setGdxVersion(String gdxVersion) {
        this.gdxVersion = gdxVersion;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public boolean hasPlatform(PlatformType platform) {
        return targetPlatforms.contains(platform);
    }

    public boolean hasExtension(ExtensionType extension) {
        return extensions.contains(extension);
    }

    @Override
    public String toString() {
        return "LibGdxProject{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", rootDirectory=" + rootDirectory +
                ", platforms=" + targetPlatforms +
                ", extensions=" + extensions +
                '}';
    }
}
