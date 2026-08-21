package com.atomgdx.gradle.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a multi-platform Build Configuration Profile in AtomGDX.
 */
public class BuildConfigModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Platform {
        DESKTOP_LWJGL3("Desktop (LWJGL3)", "lwjgl3:run"),
        ANDROID_DEBUG("Android Debug APK", "android:assembleDebug"),
        ANDROID_BUNDLE("Android Release AAB", "android:bundleRelease"),
        WEB_TEAVM("Web (TeaVM)", "teavm:build"),
        WEB_HTML_GWT("Web (HTML / GWT)", "html:dist"),
        IOS_ROBOVM("iOS (RoboVM)", "ios:createIPA");

        private final String displayName;
        private final String defaultTask;

        Platform(String displayName, String defaultTask) {
            this.displayName = displayName;
            this.defaultTask = defaultTask;
        }

        public String getDisplayName() { return displayName; }
        public String getDefaultTask() { return defaultTask; }

        @Override
        public String toString() { return displayName; }
    }

    public static class Profile implements Serializable {
        private String name;
        private Platform platform;
        private String tasks;
        private String jvmArgs;
        private String gradleFlags;
        private boolean isDebug;

        public Profile(String name, Platform platform, String tasks, String jvmArgs, String gradleFlags, boolean isDebug) {
            this.name = name;
            this.platform = platform;
            this.tasks = tasks;
            this.jvmArgs = jvmArgs;
            this.gradleFlags = gradleFlags;
            this.isDebug = isDebug;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Platform getPlatform() { return platform; }
        public void setPlatform(Platform platform) { this.platform = platform; }

        public String getTasks() { return tasks; }
        public void setTasks(String tasks) { this.tasks = tasks; }

        public String getJvmArgs() { return jvmArgs; }
        public void setJvmArgs(String jvmArgs) { this.jvmArgs = jvmArgs; }

        public String getGradleFlags() { return gradleFlags; }
        public void setGradleFlags(String gradleFlags) { this.gradleFlags = gradleFlags; }

        public boolean isDebug() { return isDebug; }
        public void setDebug(boolean debug) { isDebug = debug; }

        @Override
        public String toString() {
            return name + " [" + platform.getDisplayName() + "]";
        }
    }

    private final List<Profile> profiles = new ArrayList<>();
    private int selectedProfileIndex = 0;

    public BuildConfigModel() {
        // Initialize with standard LibGDX platform targets
        profiles.add(new Profile("Desktop LWJGL3", Platform.DESKTOP_LWJGL3, "lwjgl3:run", "-Xmx1024m", "--stacktrace", true));
        profiles.add(new Profile("Android Debug", Platform.ANDROID_DEBUG, "android:assembleDebug", "", "", true));
        profiles.add(new Profile("Android Release", Platform.ANDROID_BUNDLE, "android:bundleRelease", "", "--offline", false));
        profiles.add(new Profile("Web TeaVM", Platform.WEB_TEAVM, "teavm:build", "", "", false));
        profiles.add(new Profile("iOS IPA", Platform.IOS_ROBOVM, "ios:createIPA", "", "", false));
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public Profile getSelectedProfile() {
        if (selectedProfileIndex >= 0 && selectedProfileIndex < profiles.size()) {
            return profiles.get(selectedProfileIndex);
        }
        return profiles.isEmpty() ? null : profiles.get(0);
    }

    public int getSelectedProfileIndex() {
        return selectedProfileIndex;
    }

    public void setSelectedProfileIndex(int index) {
        if (index >= 0 && index < profiles.size()) {
            this.selectedProfileIndex = index;
        }
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
        selectedProfileIndex = profiles.size() - 1;
    }

    public void removeProfile(int index) {
        if (index >= 0 && index < profiles.size()) {
            profiles.remove(index);
            if (selectedProfileIndex >= profiles.size()) {
                selectedProfileIndex = Math.max(0, profiles.size() - 1);
            }
        }
    }
}
