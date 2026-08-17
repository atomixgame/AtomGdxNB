package com.atomgdx.liftoff;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.PlatformType;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

/**
 * Descriptor for generating a new LibGDX project via the integrated Liftoff engine.
 */
public class LiftoffProjectDescriptor {
    private String projectName = "MyGdxGame";
    private String packageName = "com.mygdx.game";
    private String mainClassName = "MainGame";
    private File destinationDir;
    private String gdxVersion = "1.13.1";
    private String javaVersion = "21";

    private final Set<PlatformType> platforms = EnumSet.of(PlatformType.CORE, PlatformType.DESKTOP_LWJGL3);
    private final Set<ExtensionType> extensions = EnumSet.noneOf(ExtensionType.class);

    public LiftoffProjectDescriptor() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public File getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
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

    public Set<PlatformType> getPlatforms() {
        return platforms;
    }

    public Set<ExtensionType> getExtensions() {
        return extensions;
    }
}
