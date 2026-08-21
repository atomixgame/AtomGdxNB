package com.atomgdx.editor.scene2d.tilemap.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Key-Value typed custom property container for tilemaps, layers, tiles, and objects.
 */
public class PropertyMap implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum PropertyType {
        STRING,
        INT,
        FLOAT,
        BOOLEAN,
        COLOR,
        FILE_REF
    }

    public static class PropertyValue implements Serializable {
        public String name;
        public PropertyType type;
        public String value;

        public PropertyValue(String name, PropertyType type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public int asInt(int def) {
            try { return Integer.parseInt(value); } catch (Exception e) { return def; }
        }

        public float asFloat(float def) {
            try { return Float.parseFloat(value); } catch (Exception e) { return def; }
        }

        public boolean asBoolean(boolean def) {
            try { return Boolean.parseBoolean(value); } catch (Exception e) { return def; }
        }
    }

    private final Map<String, PropertyValue> properties = new LinkedHashMap<>();

    public Map<String, PropertyValue> getProperties() {
        return properties;
    }

    public void put(String name, PropertyType type, String value) {
        properties.put(name, new PropertyValue(name, type, value));
    }

    public void putString(String name, String value) {
        put(name, PropertyType.STRING, value);
    }

    public void putInt(String name, int value) {
        put(name, PropertyType.INT, String.valueOf(value));
    }

    public void putFloat(String name, float value) {
        put(name, PropertyType.FLOAT, String.valueOf(value));
    }

    public void putBoolean(String name, boolean value) {
        put(name, PropertyType.BOOLEAN, String.valueOf(value));
    }

    public PropertyValue get(String name) {
        return properties.get(name);
    }

    public boolean has(String name) {
        return properties.containsKey(name);
    }

    public void remove(String name) {
        properties.remove(name);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }
}
