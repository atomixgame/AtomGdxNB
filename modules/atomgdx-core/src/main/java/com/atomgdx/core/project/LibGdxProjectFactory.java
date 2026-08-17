package com.atomgdx.core.project;

import java.io.File;

/**
 * Factory for detecting and creating LibGDX project representations from workspace folders.
 */
public class LibGdxProjectFactory {

    public static boolean isLibGdxProject(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return false;
        }

        File buildGradle = new File(directory, "build.gradle");
        File buildGradleKts = new File(directory, "build.gradle.kts");
        File settingsGradle = new File(directory, "settings.gradle");
        File settingsGradleKts = new File(directory, "settings.gradle.kts");
        File assetsDir = new File(directory, "assets");
        File coreDir = new File(directory, "core");

        boolean hasGradle = buildGradle.exists() || buildGradleKts.exists() || settingsGradle.exists() || settingsGradleKts.exists();
        boolean hasGdxStructure = assetsDir.exists() || coreDir.exists();

        return hasGradle && hasGdxStructure;
    }

    public static LibGdxProject createFromDirectory(File directory) {
        if (!isLibGdxProject(directory)) {
            return null;
        }

        String name = directory.getName();
        String packageName = "com.mygdx.game";
        String mainClass = "MainGame";

        LibGdxProject project = new LibGdxProject(name, packageName, mainClass, directory);

        // Detect platforms based on directories
        if (new File(directory, "lwjgl3").isDirectory() || new File(directory, "desktop").isDirectory()) {
            project.addTargetPlatform(PlatformType.DESKTOP_LWJGL3);
        }
        if (new File(directory, "android").isDirectory()) {
            project.addTargetPlatform(PlatformType.ANDROID);
        }
        if (new File(directory, "teavm").isDirectory()) {
            project.addTargetPlatform(PlatformType.WEB_TEAVM);
        }
        if (new File(directory, "html").isDirectory()) {
            project.addTargetPlatform(PlatformType.WEB_GWT);
        }
        if (new File(directory, "ios").isDirectory()) {
            project.addTargetPlatform(PlatformType.IOS_ROBOVM);
        }

        return project;
    }
}
