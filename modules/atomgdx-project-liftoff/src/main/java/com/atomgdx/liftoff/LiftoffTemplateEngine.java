package com.atomgdx.liftoff;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.project.PlatformType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Generates modern multi-module Gradle LibGDX projects with selected platforms and extensions.
 */
public class LiftoffTemplateEngine {

    public static LibGdxProject generateProject(LiftoffProjectDescriptor descriptor) throws IOException {
        File rootDir = new File(descriptor.getDestinationDir(), descriptor.getProjectName());
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        // 1. Generate root settings.gradle.kts
        StringBuilder settings = new StringBuilder();
        settings.append("rootProject.name = \"").append(descriptor.getProjectName()).append("\"\n\n");
        settings.append("include(\"core\")\n");
        for (PlatformType platform : descriptor.getPlatforms()) {
            if (platform != PlatformType.CORE) {
                settings.append("include(\"").append(platform.getModuleName()).append("\")\n");
            }
        }
        Files.writeString(new File(rootDir, "settings.gradle.kts").toPath(), settings.toString(), StandardCharsets.UTF_8);

        // 2. Generate root build.gradle.kts
        StringBuilder rootBuild = new StringBuilder();
        rootBuild.append("plugins {\n    `java-library`\n}\n\n");
        rootBuild.append("allprojects {\n");
        rootBuild.append("    group = \"").append(descriptor.getPackageName()).append("\"\n");
        rootBuild.append("    version = \"0.0.1-SNAPSHOT\"\n\n");
        rootBuild.append("    repositories {\n");
        rootBuild.append("        mavenCentral()\n");
        rootBuild.append("        maven { url = uri(\"https://oss.sonatype.org/content/repositories/snapshots/\") }\n");
        rootBuild.append("    }\n");
        rootBuild.append("}\n\n");
        rootBuild.append("val gdxVersion = \"").append(descriptor.getGdxVersion()).append("\"\n");
        Files.writeString(new File(rootDir, "build.gradle.kts").toPath(), rootBuild.toString(), StandardCharsets.UTF_8);

        // 3. Generate assets directory
        File assetsDir = new File(rootDir, "assets");
        assetsDir.mkdirs();

        // 4. Generate core module
        File coreDir = new File(rootDir, "core");
        String packagePath = descriptor.getPackageName().replace('.', '/');
        File coreSrcDir = new File(coreDir, "src/main/java/" + packagePath);
        coreSrcDir.mkdirs();

        // Core build.gradle.kts
        StringBuilder coreBuild = new StringBuilder();
        coreBuild.append("plugins {\n    `java-library`\n}\n\n");
        coreBuild.append("dependencies {\n");
        coreBuild.append("    api(\"com.badlogicgames.gdx:gdx:$gdxVersion\")\n");
        for (ExtensionType ext : descriptor.getExtensions()) {
            coreBuild.append("    api(\"").append(ext.getMavenCoordinate()).append(":$gdxVersion\")\n");
        }
        coreBuild.append("}\n");
        Files.writeString(new File(coreDir, "build.gradle.kts").toPath(), coreBuild.toString(), StandardCharsets.UTF_8);

        // MainGame.java
        StringBuilder mainGame = new StringBuilder();
        mainGame.append("package ").append(descriptor.getPackageName()).append(";\n\n");
        mainGame.append("import com.badlogic.gdx.ApplicationAdapter;\n");
        mainGame.append("import com.badlogic.gdx.Gdx;\n");
        mainGame.append("import com.badlogic.gdx.graphics.GL20;\n");
        mainGame.append("import com.badlogic.gdx.graphics.Texture;\n");
        mainGame.append("import com.badlogic.gdx.graphics.g2d.SpriteBatch;\n");
        mainGame.append("import com.badlogic.gdx.utils.ScreenUtils;\n\n");
        mainGame.append("public class ").append(descriptor.getMainClassName()).append(" extends ApplicationAdapter {\n");
        mainGame.append("    private SpriteBatch batch;\n");
        mainGame.append("    private Texture image;\n\n");
        mainGame.append("    @Override\n");
        mainGame.append("    public void create() {\n");
        mainGame.append("        batch = new SpriteBatch();\n");
        mainGame.append("    }\n\n");
        mainGame.append("    @Override\n");
        mainGame.append("    public void render() {\n");
        mainGame.append("        ScreenUtils.clear(0.05f, 0.08f, 0.12f, 1f);\n");
        mainGame.append("        batch.begin();\n");
        mainGame.append("        batch.end();\n");
        mainGame.append("    }\n\n");
        mainGame.append("    @Override\n");
        mainGame.append("    public void dispose() {\n");
        mainGame.append("        batch.dispose();\n");
        if (descriptor.getExtensions().contains(ExtensionType.VIS_UI)) {
            mainGame.append("        // VisUI cleanup\n");
        }
        mainGame.append("    }\n");
        mainGame.append("}\n");
        Files.writeString(new File(coreSrcDir, descriptor.getMainClassName() + ".java").toPath(), mainGame.toString(), StandardCharsets.UTF_8);

        // 5. Generate Desktop (LWJGL3) Module
        if (descriptor.getPlatforms().contains(PlatformType.DESKTOP_LWJGL3)) {
            File desktopDir = new File(rootDir, "lwjgl3");
            File desktopSrcDir = new File(desktopDir, "src/main/java/" + packagePath + "/lwjgl3");
            desktopSrcDir.mkdirs();

            StringBuilder desktopBuild = new StringBuilder();
            desktopBuild.append("plugins {\n    application\n}\n\n");
            desktopBuild.append("dependencies {\n");
            desktopBuild.append("    implementation(project(\":core\"))\n");
            desktopBuild.append("    implementation(\"com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion\")\n");
            desktopBuild.append("    implementation(\"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop\")\n");
            desktopBuild.append("}\n\n");
            desktopBuild.append("application {\n");
            desktopBuild.append("    mainClass.set(\"").append(descriptor.getPackageName()).append(".lwjgl3.Lwjgl3Launcher\")\n");
            desktopBuild.append("}\n");
            Files.writeString(new File(desktopDir, "build.gradle.kts").toPath(), desktopBuild.toString(), StandardCharsets.UTF_8);

            StringBuilder launcher = new StringBuilder();
            launcher.append("package ").append(descriptor.getPackageName()).append(".lwjgl3;\n\n");
            launcher.append("import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;\n");
            launcher.append("import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;\n");
            launcher.append("import ").append(descriptor.getPackageName()).append(".").append(descriptor.getMainClassName()).append(";\n\n");
            launcher.append("public class Lwjgl3Launcher {\n");
            launcher.append("    public static void main(String[] args) {\n");
            launcher.append("        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();\n");
            launcher.append("        config.setTitle(\"").append(descriptor.getProjectName()).append("\");\n");
            launcher.append("        config.setWindowedMode(1280, 720);\n");
            launcher.append("        config.useVsync(true);\n");
            launcher.append("        new Lwjgl3Application(new ").append(descriptor.getMainClassName()).append("(), config);\n");
            launcher.append("    }\n");
            launcher.append("}\n");
            Files.writeString(new File(desktopSrcDir, "Lwjgl3Launcher.java").toPath(), launcher.toString(), StandardCharsets.UTF_8);
        }

        LibGdxProject project = new LibGdxProject(
                descriptor.getProjectName(),
                descriptor.getPackageName(),
                descriptor.getMainClassName(),
                rootDir
        );
        for (PlatformType p : descriptor.getPlatforms()) {
            project.addTargetPlatform(p);
        }
        for (ExtensionType e : descriptor.getExtensions()) {
            project.addExtension(e);
        }
        return project;
    }
}
