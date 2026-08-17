package com.atomgdx.liftoff;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.project.PlatformType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Generates modern multi-module Gradle LibGDX projects with selected platforms,
 * extensions, and standard wrapper scripts.
 */
public class LiftoffTemplateEngine {

    public static LibGdxProject generateProject(LiftoffProjectDescriptor descriptor) throws IOException {
        File rootDir = new File(descriptor.getDestinationDir(), descriptor.getProjectName());
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        String pkg = descriptor.getPackageName();
        String pkgPath = pkg.replace('.', '/');
        String mainClass = descriptor.getMainClassName();
        String gdxVersion = descriptor.getGdxVersion();

        // 1. Root settings.gradle
        StringBuilder settings = new StringBuilder();
        settings.append("rootProject.name = '").append(descriptor.getProjectName()).append("'\n");
        settings.append("include 'core'\n");
        for (PlatformType platform : descriptor.getPlatforms()) {
            if (platform != PlatformType.CORE) {
                settings.append("include '").append(platform.getModuleName()).append("'\n");
            }
        }
        Files.writeString(new File(rootDir, "settings.gradle").toPath(), settings.toString(), StandardCharsets.UTF_8);

        // 2. Root gradle.properties
        StringBuilder props = new StringBuilder();
        props.append("gdxVersion=").append(gdxVersion).append("\n");
        props.append("visUiVersion=1.5.3\n");
        props.append("ashleyVersion=1.7.4\n");
        props.append("aiVersion=1.8.2\n");
        props.append("box2dLightsVersion=1.5\n");
        props.append("controllersVersion=2.2.3\n");
        props.append("gltfVersion=2.2.1\n");
        props.append("appName=").append(descriptor.getProjectName()).append("\n");
        Files.writeString(new File(rootDir, "gradle.properties").toPath(), props.toString(), StandardCharsets.UTF_8);

        // 3. Root build.gradle
        StringBuilder rootBuild = new StringBuilder();
        rootBuild.append("buildscript {\n");
        rootBuild.append("    repositories {\n");
        rootBuild.append("        mavenCentral()\n");
        rootBuild.append("        maven { url 'https://s01.oss.sonatype.org' }\n");
        rootBuild.append("        gradlePluginPortal()\n");
        rootBuild.append("        google()\n");
        rootBuild.append("    }\n");
        rootBuild.append("}\n\n");
        rootBuild.append("allprojects {\n");
        rootBuild.append("    apply plugin: 'eclipse'\n");
        rootBuild.append("    apply plugin: 'idea'\n");
        rootBuild.append("}\n\n");
        rootBuild.append("configure(subprojects) {\n");
        rootBuild.append("    apply plugin: 'java-library'\n");
        rootBuild.append("    sourceCompatibility = 17\n");
        rootBuild.append("    compileJava {\n");
        rootBuild.append("        options.incremental = true\n");
        rootBuild.append("    }\n");
        rootBuild.append("    repositories {\n");
        rootBuild.append("        mavenCentral()\n");
        rootBuild.append("        maven { url 'https://s01.oss.sonatype.org' }\n");
        rootBuild.append("        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }\n");
        rootBuild.append("        maven { url 'https://oss.sonatype.org/content/repositories/releases/' }\n");
        rootBuild.append("        maven { url 'https://jitpack.io' }\n");
        rootBuild.append("    }\n");
        rootBuild.append("}\n\n");
        rootBuild.append("subprojects {\n");
        rootBuild.append("    version = '1.0.0'\n");
        rootBuild.append("    ext {\n");
        rootBuild.append("        appName = '").append(descriptor.getProjectName()).append("'\n");
        rootBuild.append("        gdxVersion = project.hasProperty('gdxVersion') ? project.property('gdxVersion') : '").append(gdxVersion).append("'\n");
        rootBuild.append("    }\n");
        rootBuild.append("}\n");
        Files.writeString(new File(rootDir, "build.gradle").toPath(), rootBuild.toString(), StandardCharsets.UTF_8);

        // 4. .gitignore
        StringBuilder gitignore = new StringBuilder();
        gitignore.append(".gradle/\n");
        gitignore.append("build/\n");
        gitignore.append(".idea/\n");
        gitignore.append("*.iml\n");
        gitignore.append(".project\n");
        gitignore.append(".classpath\n");
        gitignore.append(".settings/\n");
        gitignore.append("nbproject/private/\n");
        Files.writeString(new File(rootDir, ".gitignore").toPath(), gitignore.toString(), StandardCharsets.UTF_8);

        // 5. Assets Directory
        File assetsDir = new File(rootDir, "assets");
        assetsDir.mkdirs();
        new File(assetsDir, "textures").mkdirs();
        new File(assetsDir, "audio").mkdirs();
        new File(assetsDir, "fonts").mkdirs();

        // 6. Core Module
        File coreDir = new File(rootDir, "core");
        File coreSrcDir = new File(coreDir, "src/main/java/" + pkgPath);
        coreSrcDir.mkdirs();

        StringBuilder coreBuild = new StringBuilder();
        coreBuild.append("[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'\n");
        coreBuild.append("eclipse.project.name = appName + \"-core\"\n\n");
        coreBuild.append("dependencies {\n");
        coreBuild.append("    api \"com.badlogicgames.gdx:gdx:$gdxVersion\"\n");
        for (ExtensionType ext : descriptor.getExtensions()) {
            if (ext == ExtensionType.VIS_UI) {
                coreBuild.append("    api \"com.kotcrab.vis:vis-ui:1.5.3\"\n");
            } else if (ext == ExtensionType.ASHLEY) {
                coreBuild.append("    api \"com.badlogicgames.ashley:ashley:1.7.4\"\n");
            } else if (ext == ExtensionType.GDX_AI) {
                coreBuild.append("    api \"com.badlogicgames.gdx:gdx-ai:1.8.2\"\n");
            } else if (ext == ExtensionType.BOX2D) {
                coreBuild.append("    api \"com.badlogicgames.gdx:gdx-box2d:$gdxVersion\"\n");
            } else if (ext == ExtensionType.BOX2DLIGHTS) {
                coreBuild.append("    api \"com.badlogicgames.box2dlights:box2dlights:1.5\"\n");
            } else if (ext == ExtensionType.GDX_CONTROLLERS) {
                coreBuild.append("    api \"com.badlogicgames.gdx-controllers:gdx-controllers-core:2.2.3\"\n");
            } else if (ext == ExtensionType.FREETYPE) {
                coreBuild.append("    api \"com.badlogicgames.gdx:gdx-freetype:$gdxVersion\"\n");
            } else if (ext == ExtensionType.GDX_GLTF) {
                coreBuild.append("    api \"net.mgsx.gltf:gltf:2.2.1\"\n");
            }
        }
        coreBuild.append("}\n");
        Files.writeString(new File(coreDir, "build.gradle").toPath(), coreBuild.toString(), StandardCharsets.UTF_8);

        // MainGame.java
        StringBuilder mainGame = new StringBuilder();
        mainGame.append("package ").append(pkg).append(";\n\n");
        mainGame.append("import com.badlogic.gdx.ApplicationAdapter;\n");
        mainGame.append("import com.badlogic.gdx.Gdx;\n");
        mainGame.append("import com.badlogic.gdx.graphics.GL20;\n");
        mainGame.append("import com.badlogic.gdx.graphics.g2d.SpriteBatch;\n");
        mainGame.append("import com.badlogic.gdx.utils.ScreenUtils;\n\n");
        mainGame.append("/**\n");
        mainGame.append(" * ").append(mainClass).append(" - Primary Game loop generated by AtomGdx Liftoff.\n");
        mainGame.append(" */\n");
        mainGame.append("public class ").append(mainClass).append(" extends ApplicationAdapter {\n");
        mainGame.append("    private SpriteBatch batch;\n\n");
        mainGame.append("    @Override\n");
        mainGame.append("    public void create() {\n");
        mainGame.append("        batch = new SpriteBatch();\n");
        mainGame.append("    }\n\n");
        mainGame.append("    @Override\n");
        mainGame.append("    public void render() {\n");
        mainGame.append("        ScreenUtils.clear(0.06f, 0.08f, 0.12f, 1f);\n");
        mainGame.append("        batch.begin();\n");
        mainGame.append("        batch.end();\n");
        mainGame.append("    }\n\n");
        mainGame.append("    @Override\n");
        mainGame.append("    public void dispose() {\n");
        mainGame.append("        batch.dispose();\n");
        mainGame.append("    }\n");
        mainGame.append("}\n");
        Files.writeString(new File(coreSrcDir, mainClass + ".java").toPath(), mainGame.toString(), StandardCharsets.UTF_8);

        // 7. Desktop (LWJGL3) Module
        if (descriptor.getPlatforms().contains(PlatformType.DESKTOP_LWJGL3)) {
            File desktopDir = new File(rootDir, "lwjgl3");
            File desktopSrcDir = new File(desktopDir, "src/main/java/" + pkgPath + "/lwjgl3");
            desktopSrcDir.mkdirs();

            StringBuilder desktopBuild = new StringBuilder();
            desktopBuild.append("apply plugin: 'application'\n\n");
            desktopBuild.append("sourceCompatibility = 17\n");
            desktopBuild.append("mainClassName = '").append(pkg).append(".lwjgl3.Lwjgl3Launcher'\n");
            desktopBuild.append("eclipse.project.name = appName + \"-lwjgl3\"\n\n");
            desktopBuild.append("dependencies {\n");
            desktopBuild.append("    implementation project(':core')\n");
            desktopBuild.append("    implementation \"com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion\"\n");
            desktopBuild.append("    implementation \"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop\"\n");
            for (ExtensionType ext : descriptor.getExtensions()) {
                if (ext == ExtensionType.BOX2D) {
                    desktopBuild.append("    implementation \"com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop\"\n");
                } else if (ext == ExtensionType.FREETYPE) {
                    desktopBuild.append("    implementation \"com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop\"\n");
                } else if (ext == ExtensionType.GDX_CONTROLLERS) {
                    desktopBuild.append("    implementation \"com.badlogicgames.gdx-controllers:gdx-controllers-desktop:2.2.3\"\n");
                }
            }
            desktopBuild.append("}\n\n");
            desktopBuild.append("sourceSets.main.resources.srcDirs += [ rootProject.file('assets').path ]\n\n");
            desktopBuild.append("run {\n");
            desktopBuild.append("    workingDir = rootProject.file('assets')\n");
            desktopBuild.append("    ignoreExitValue = true\n");
            desktopBuild.append("}\n");
            Files.writeString(new File(desktopDir, "build.gradle").toPath(), desktopBuild.toString(), StandardCharsets.UTF_8);

            StringBuilder launcher = new StringBuilder();
            launcher.append("package ").append(pkg).append(".lwjgl3;\n\n");
            launcher.append("import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;\n");
            launcher.append("import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;\n");
            launcher.append("import ").append(pkg).append(".").append(mainClass).append(";\n\n");
            launcher.append("public class Lwjgl3Launcher {\n");
            launcher.append("    public static void main(String[] args) {\n");
            launcher.append("        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();\n");
            launcher.append("        config.setTitle(\"").append(descriptor.getProjectName()).append("\");\n");
            launcher.append("        config.setWindowedMode(1280, 720);\n");
            launcher.append("        config.useVsync(true);\n");
            launcher.append("        new Lwjgl3Application(new ").append(mainClass).append("(), config);\n");
            launcher.append("    }\n");
            launcher.append("}\n");
            Files.writeString(new File(desktopSrcDir, "Lwjgl3Launcher.java").toPath(), launcher.toString(), StandardCharsets.UTF_8);
        }

        // 8. Android Module
        if (descriptor.getPlatforms().contains(PlatformType.ANDROID)) {
            File androidDir = new File(rootDir, "android");
            File androidSrcDir = new File(androidDir, "src/main/java/" + pkgPath + "/android");
            androidSrcDir.mkdirs();

            StringBuilder androidBuild = new StringBuilder();
            androidBuild.append("apply plugin: 'com.android.application'\n\n");
            androidBuild.append("android {\n");
            androidBuild.append("    namespace '").append(pkg).append("'\n");
            androidBuild.append("    compileSdk 34\n");
            androidBuild.append("    defaultConfig {\n");
            androidBuild.append("        applicationId '").append(pkg).append("'\n");
            androidBuild.append("        minSdk 21\n");
            androidBuild.append("        targetSdk 34\n");
            androidBuild.append("        versionCode 1\n");
            androidBuild.append("        versionName '1.0'\n");
            androidBuild.append("    }\n");
            androidBuild.append("    sourceSets.main {\n");
            androidBuild.append("        assets.srcDirs = [ rootProject.file('assets').path ]\n");
            androidBuild.append("    }\n");
            androidBuild.append("}\n\n");
            androidBuild.append("dependencies {\n");
            androidBuild.append("    implementation project(':core')\n");
            androidBuild.append("    implementation \"com.badlogicgames.gdx:gdx-backend-android:$gdxVersion\"\n");
            androidBuild.append("    natives \"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a\"\n");
            androidBuild.append("    natives \"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a\"\n");
            androidBuild.append("    natives \"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64\"\n");
            androidBuild.append("}\n");
            Files.writeString(new File(androidDir, "build.gradle").toPath(), androidBuild.toString(), StandardCharsets.UTF_8);

            StringBuilder androidLauncher = new StringBuilder();
            androidLauncher.append("package ").append(pkg).append(".android;\n\n");
            androidLauncher.append("import android.os.Bundle;\n");
            androidLauncher.append("import com.badlogic.gdx.backends.android.AndroidApplication;\n");
            androidLauncher.append("import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;\n");
            androidLauncher.append("import ").append(pkg).append(".").append(mainClass).append(";\n\n");
            androidLauncher.append("public class AndroidLauncher extends AndroidApplication {\n");
            androidLauncher.append("    @Override\n");
            androidLauncher.append("    protected void onCreate(Bundle savedInstanceState) {\n");
            androidLauncher.append("        super.onCreate(savedInstanceState);\n");
            androidLauncher.append("        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();\n");
            androidLauncher.append("        initialize(new ").append(mainClass).append("(), config);\n");
            androidLauncher.append("    }\n");
            androidLauncher.append("}\n");
            Files.writeString(new File(androidSrcDir, "AndroidLauncher.java").toPath(), androidLauncher.toString(), StandardCharsets.UTF_8);
        }

        // 9. HTML / Web Module
        if (descriptor.getPlatforms().contains(PlatformType.WEB_GWT) || descriptor.getPlatforms().contains(PlatformType.WEB_TEAVM)) {
            File htmlDir = new File(rootDir, "html");
            File htmlSrcDir = new File(htmlDir, "src/main/java/" + pkgPath + "/gwt");
            htmlSrcDir.mkdirs();

            StringBuilder htmlBuild = new StringBuilder();
            htmlBuild.append("apply plugin: 'gwt'\n");
            htmlBuild.append("apply plugin: 'war'\n\n");
            htmlBuild.append("dependencies {\n");
            htmlBuild.append("    implementation project(':core')\n");
            htmlBuild.append("    implementation \"com.badlogicgames.gdx:gdx-backend-gwt:$gdxVersion\"\n");
            htmlBuild.append("    implementation \"com.badlogicgames.gdx:gdx:$gdxVersion:sources\"\n");
            htmlBuild.append("}\n");
            Files.writeString(new File(htmlDir, "build.gradle").toPath(), htmlBuild.toString(), StandardCharsets.UTF_8);

            StringBuilder gwtLauncher = new StringBuilder();
            gwtLauncher.append("package ").append(pkg).append(".gwt;\n\n");
            gwtLauncher.append("import com.badlogic.gdx.ApplicationListener;\n");
            gwtLauncher.append("import com.badlogic.gdx.backends.gwt.GwtApplication;\n");
            gwtLauncher.append("import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;\n");
            gwtLauncher.append("import ").append(pkg).append(".").append(mainClass).append(";\n\n");
            gwtLauncher.append("public class GwtLauncher extends GwtApplication {\n");
            gwtLauncher.append("    @Override\n");
            gwtLauncher.append("    public GwtApplicationConfiguration getConfig() {\n");
            gwtLauncher.append("        return new GwtApplicationConfiguration(800, 600);\n");
            gwtLauncher.append("    }\n\n");
            gwtLauncher.append("    @Override\n");
            gwtLauncher.append("    public ApplicationListener createApplicationListener() {\n");
            gwtLauncher.append("        return new ").append(mainClass).append("();\n");
            gwtLauncher.append("    }\n");
            gwtLauncher.append("}\n");
            Files.writeString(new File(htmlSrcDir, "GwtLauncher.java").toPath(), gwtLauncher.toString(), StandardCharsets.UTF_8);
        }

        // 10. iOS Module
        if (descriptor.getPlatforms().contains(PlatformType.IOS_ROBOVM)) {
            File iosDir = new File(rootDir, "ios");
            File iosSrcDir = new File(iosDir, "src/main/java/" + pkgPath + "/ios");
            iosSrcDir.mkdirs();

            StringBuilder iosBuild = new StringBuilder();
            iosBuild.append("apply plugin: 'robovm'\n\n");
            iosBuild.append("dependencies {\n");
            iosBuild.append("    implementation project(':core')\n");
            iosBuild.append("    implementation \"com.badlogicgames.gdx:gdx-backend-robovm:$gdxVersion\"\n");
            iosBuild.append("    implementation \"com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-ios\"\n");
            iosBuild.append("}\n");
            Files.writeString(new File(iosDir, "build.gradle").toPath(), iosBuild.toString(), StandardCharsets.UTF_8);

            StringBuilder iosLauncher = new StringBuilder();
            iosLauncher.append("package ").append(pkg).append(".ios;\n\n");
            iosLauncher.append("import org.robovm.apple.foundation.NSAutoreleasePool;\n");
            iosLauncher.append("import org.robovm.apple.uikit.UIApplication;\n");
            iosLauncher.append("import com.badlogic.gdx.backends.iosrobovm.IOSApplication;\n");
            iosLauncher.append("import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;\n");
            iosLauncher.append("import ").append(pkg).append(".").append(mainClass).append(";\n\n");
            iosLauncher.append("public class IOSLauncher extends IOSApplication.Delegate {\n");
            iosLauncher.append("    @Override\n");
            iosLauncher.append("    protected IOSApplication createApplication() {\n");
            iosLauncher.append("        IOSApplicationConfiguration config = new IOSApplicationConfiguration();\n");
            iosLauncher.append("        return new IOSApplication(new ").append(mainClass).append("(), config);\n");
            iosLauncher.append("    }\n\n");
            iosLauncher.append("    public static void main(String[] argv) {\n");
            iosLauncher.append("        NSAutoreleasePool pool = new NSAutoreleasePool();\n");
            iosLauncher.append("        UIApplication.main(argv, null, IOSLauncher.class);\n");
            iosLauncher.append("        pool.close();\n");
            iosLauncher.append("    }\n");
            iosLauncher.append("}\n");
            Files.writeString(new File(iosSrcDir, "IOSLauncher.java").toPath(), iosLauncher.toString(), StandardCharsets.UTF_8);
        }

        // Build LibGdxProject metadata object
        LibGdxProject project = new LibGdxProject(
                descriptor.getProjectName(),
                pkg,
                mainClass,
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
