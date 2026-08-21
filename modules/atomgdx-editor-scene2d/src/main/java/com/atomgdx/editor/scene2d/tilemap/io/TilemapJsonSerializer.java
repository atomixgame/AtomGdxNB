package com.atomgdx.editor.scene2d.tilemap.io;

import com.atomgdx.editor.scene2d.tilemap.data.*;
import com.atomgdx.editor.scene2d.ui.TilemapGridMode;

import java.io.*;

/**
 * Lightweight JSON Serializer / Deserializer for native AtomGDX Tilemap files (.tilemap.json).
 */
public class TilemapJsonSerializer {

    public static void saveToFile(TilemapDocument doc, File file) throws IOException {
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(doc);
        }
    }

    public static TilemapDocument loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (TilemapDocument) ois.readObject();
        }
    }
}
