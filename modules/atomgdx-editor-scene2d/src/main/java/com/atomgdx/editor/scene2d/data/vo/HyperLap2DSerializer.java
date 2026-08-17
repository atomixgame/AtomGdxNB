package com.atomgdx.editor.scene2d.data.vo;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Standard HyperLap2D JSON serializer for .dt / .h2d project and scene files.
 */
public class HyperLap2DSerializer {

    private static Json createJson() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        return json;
    }

    public static String serializeScene(SceneVO scene) {
        Json json = createJson();
        return json.prettyPrint(scene);
    }

    public static SceneVO deserializeScene(String jsonText) {
        Json json = createJson();
        return json.fromJson(SceneVO.class, jsonText);
    }

    public static void saveSceneToFile(SceneVO scene, File file) throws IOException {
        String data = serializeScene(scene);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        }
    }

    public static SceneVO loadSceneFromFile(File file) throws IOException {
        Json json = createJson();
        try (FileReader reader = new FileReader(file)) {
            return json.fromJson(SceneVO.class, reader);
        }
    }

    public static String serializeProject(ProjectVO project) {
        Json json = createJson();
        return json.prettyPrint(project);
    }

    public static ProjectVO deserializeProject(String jsonText) {
        Json json = createJson();
        return json.fromJson(ProjectVO.class, jsonText);
    }

    public static void saveProjectToFile(ProjectVO project, File file) throws IOException {
        String data = serializeProject(project);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        }
    }

    public static ProjectVO loadProjectFromFile(File file) throws IOException {
        Json json = createJson();
        try (FileReader reader = new FileReader(file)) {
            return json.fromJson(ProjectVO.class, reader);
        }
    }
}
