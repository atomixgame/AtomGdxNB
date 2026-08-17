package com.atomgdx.gradle;

/**
 * Standard Gradle execution tasks in LibGDX projects.
 */
public enum GradleTask {
    RUN_DESKTOP("lwjgl3:run", "Run Desktop (LWJGL3) Game"),
    DEBUG_DESKTOP("lwjgl3:debug", "Debug Desktop (LWJGL3) Game"),
    RUN_ANDROID("android:installDebug", "Deploy & Install Debug APK to Android Device/Emulator"),
    RUN_WEB_TEAVM("teavm:run", "Start TeaVM Web DevServer"),
    BUILD_WEB_TEAVM("teavm:build", "Build Production TeaVM WebAssembly/JS Bundle"),
    BUILD_ALL("build", "Build All Modules"),
    CLEAN("clean", "Clean Project Build Output"),
    PACK_TEXTURES("core:packTextures", "Pack Assets to Texture Atlas");

    private final String taskName;
    private final String description;

    GradleTask(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }
}
