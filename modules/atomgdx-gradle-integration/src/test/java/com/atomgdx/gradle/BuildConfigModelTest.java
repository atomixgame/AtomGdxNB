package com.atomgdx.gradle;

import com.atomgdx.gradle.config.BuildConfigModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildConfigModelTest {

    @Test
    public void testDefaultProfiles() {
        BuildConfigModel model = new BuildConfigModel();
        assertFalse(model.getProfiles().isEmpty());

        BuildConfigModel.Profile first = model.getSelectedProfile();
        assertNotNull(first);
        assertEquals(BuildConfigModel.Platform.DESKTOP_LWJGL3, first.getPlatform());
        assertEquals("lwjgl3:run", first.getTasks());
    }

    @Test
    public void testAddAndRemoveProfile() {
        BuildConfigModel model = new BuildConfigModel();
        int initialCount = model.getProfiles().size();

        BuildConfigModel.Profile custom = new BuildConfigModel.Profile(
                "Custom Android",
                BuildConfigModel.Platform.ANDROID_DEBUG,
                "android:assembleDebug",
                "-Xmx2048m",
                "--offline",
                true
        );

        model.addProfile(custom);
        assertEquals(initialCount + 1, model.getProfiles().size());
        assertEquals(custom, model.getSelectedProfile());

        model.removeProfile(model.getSelectedProfileIndex());
        assertEquals(initialCount, model.getProfiles().size());
    }
}
