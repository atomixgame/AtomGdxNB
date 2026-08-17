package com.atomgdx.editor.skin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single widget style (e.g., TextButtonStyle, LabelStyle, WindowStyle).
 */
public class WidgetStyle implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String widgetType; // e.g. "com.badlogic.gdx.scenes.scene2d.ui.TextButton$TextButtonStyle"
    private final String styleName;  // e.g. "default", "primary", "danger"
    private final Map<String, String> properties = new HashMap<>();

    public WidgetStyle(String widgetType, String styleName) {
        this.widgetType = widgetType;
        this.styleName = styleName;
    }

    public String getWidgetType() {
        return widgetType;
    }

    public String getStyleName() {
        return styleName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }
}
