package com.atomgdx.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages Android ADB devices, AVD emulators, and Logcat streaming.
 */
public class AndroidPlatformManager {

    public static List<AndroidDevice> scanDevices(String sdkPath) {
        List<AndroidDevice> devices = new ArrayList<>();
        String adbPath = getAdbPath(sdkPath);

        try {
            ProcessBuilder pb = new ProcessBuilder(adbPath, "devices", "-l");
            Process p = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("List of devices")) continue;

                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        String serial = parts[0];
                        String state = parts[1];
                        boolean isOnline = "device".equalsIgnoreCase(state);
                        AndroidDevice.DeviceType type = serial.startsWith("emulator-")
                                ? AndroidDevice.DeviceType.EMULATOR
                                : AndroidDevice.DeviceType.PHYSICAL;

                        String model = "Android Device";
                        for (String part : parts) {
                            if (part.startsWith("model:")) {
                                model = part.substring("model:".length());
                            }
                        }

                        devices.add(new AndroidDevice(serial, model, type, "34", isOnline));
                    }
                }
            }
            p.waitFor();
        } catch (Exception e) {
            // Return empty list if ADB is not accessible
        }

        return Collections.unmodifiableList(devices);
    }

    public static String getAdbPath(String sdkPath) {
        if (sdkPath != null && !sdkPath.isBlank()) {
            File adb = new File(sdkPath, "platform-tools/adb.exe");
            if (adb.exists()) return adb.getAbsolutePath();
            File adbUnix = new File(sdkPath, "platform-tools/adb");
            if (adbUnix.exists()) return adbUnix.getAbsolutePath();
        }
        return "adb";
    }
}
