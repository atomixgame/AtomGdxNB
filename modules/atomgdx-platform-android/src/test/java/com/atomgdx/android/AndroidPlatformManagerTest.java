package com.atomgdx.android;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidPlatformManagerTest {

    @Test
    void testAdbPathResolution() {
        String defaultAdb = AndroidPlatformManager.getAdbPath("");
        assertThat(defaultAdb).isEqualTo("adb");

        AndroidDevice device = new AndroidDevice("emulator-5554", "Pixel_7_API_34", AndroidDevice.DeviceType.EMULATOR, "34", true);
        assertThat(device.getType()).isEqualTo(AndroidDevice.DeviceType.EMULATOR);
        assertThat(device.isOnline()).isTrue();
    }
}
