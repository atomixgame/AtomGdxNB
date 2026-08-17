package com.atomgdx.android;

import java.io.Serializable;

/**
 * Represents a connected physical Android device or running/available AVD emulator.
 */
public class AndroidDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum DeviceType {
        PHYSICAL,
        EMULATOR
    }

    private final String serialNumber;
    private final String modelName;
    private final DeviceType type;
    private final String apiLevel;
    private boolean isOnline;

    public AndroidDevice(String serialNumber, String modelName, DeviceType type, String apiLevel, boolean isOnline) {
        this.serialNumber = serialNumber;
        this.modelName = modelName;
        this.type = type;
        this.apiLevel = apiLevel;
        this.isOnline = isOnline;
    }

    public String getSerialNumber() { return serialNumber; }
    public String getModelName() { return modelName; }
    public DeviceType getType() { return type; }
    public String getApiLevel() { return apiLevel; }
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    @Override
    public String toString() {
        return modelName + " [" + serialNumber + "] (API " + apiLevel + ")";
    }
}
