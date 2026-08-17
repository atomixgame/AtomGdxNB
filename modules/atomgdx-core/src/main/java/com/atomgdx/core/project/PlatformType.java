package com.atomgdx.core.project;

/**
 * Target deployment platforms supported by AtomGdx Studio.
 */
public enum PlatformType {
    CORE("core", "Shared Core Game Logic", true),
    DESKTOP_LWJGL3("lwjgl3", "Desktop (LWJGL3 / OpenGL 3.2+)", true),
    ANDROID("android", "Android Mobile (OpenGL ES 2.0 / 3.0)", false),
    WEB_TEAVM("teavm", "WebAssembly / WebGL (TeaVM Backend)", false),
    WEB_GWT("html", "Web HTML5 / WebGL (GWT Backend)", false),
    IOS_ROBOVM("ios", "iOS (RoboVM / Apple Metal / GLES)", false);

    private final String moduleName;
    private final String displayName;
    private final boolean enabledByDefault;

    PlatformType(String moduleName, String displayName, boolean enabledByDefault) {
        this.moduleName = moduleName;
        this.displayName = displayName;
        this.enabledByDefault = enabledByDefault;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }
}
