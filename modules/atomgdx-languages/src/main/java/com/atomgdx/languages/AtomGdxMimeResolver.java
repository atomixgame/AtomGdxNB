package com.atomgdx.languages;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * MIME Resolver and File Type mapping for LibGDX and AtomGdx Studio file formats.
 */
public class AtomGdxMimeResolver {
    public static final String MIME_PARTICLE_2D = "application/x-atomgdx-particle2d";
    public static final String MIME_PARTICLE_3D = "application/x-atomgdx-particle3d";
    public static final String MIME_GLSL_VERT   = "text/x-glsl-vertex";
    public static final String MIME_GLSL_FRAG   = "text/x-glsl-fragment";
    public static final String MIME_GLSL        = "text/x-glsl";
    public static final String MIME_SKIN        = "application/x-atomgdx-skin";
    public static final String MIME_NINE_PATCH  = "image/x-atomgdx-ninepatch";
    public static final String MIME_SCENE_2D    = "application/x-atomgdx-scene2d";
    public static final String MIME_MODEL_3D    = "model/gltf+json";
    public static final String MIME_FONT_FNT    = "text/x-atomgdx-font";
    public static final String MIME_AUDIO       = "audio/x-atomgdx-audio";

    private static final Map<String, String> EXT_TO_MIME = new HashMap<>();

    static {
        // Particle
        EXT_TO_MIME.put(".p", MIME_PARTICLE_2D);
        EXT_TO_MIME.put(".party", MIME_PARTICLE_2D);
        EXT_TO_MIME.put(".particle2d", MIME_PARTICLE_2D);
        EXT_TO_MIME.put(".p3d", MIME_PARTICLE_3D);
        EXT_TO_MIME.put(".flame", MIME_PARTICLE_3D);

        // GLSL Shaders
        EXT_TO_MIME.put(".vert", MIME_GLSL_VERT);
        EXT_TO_MIME.put(".vsh", MIME_GLSL_VERT);
        EXT_TO_MIME.put(".frag", MIME_GLSL_FRAG);
        EXT_TO_MIME.put(".fsh", MIME_GLSL_FRAG);
        EXT_TO_MIME.put(".glsl", MIME_GLSL);

        // UI & 2D Scene
        EXT_TO_MIME.put(".skin", MIME_SKIN);
        EXT_TO_MIME.put(".9.png", MIME_NINE_PATCH);
        EXT_TO_MIME.put(".scene2d", MIME_SCENE_2D);
        EXT_TO_MIME.put(".h2d", MIME_SCENE_2D);

        // 3D Models
        EXT_TO_MIME.put(".gltf", MIME_MODEL_3D);
        EXT_TO_MIME.put(".glb", "model/gltf-binary");
        EXT_TO_MIME.put(".g3db", "application/x-atomgdx-g3db");
        EXT_TO_MIME.put(".g3dj", "application/x-atomgdx-g3dj");

        // Fonts & Audio
        EXT_TO_MIME.put(".fnt", MIME_FONT_FNT);
        EXT_TO_MIME.put(".hiero", MIME_FONT_FNT);
        EXT_TO_MIME.put(".wav", "audio/wav");
        EXT_TO_MIME.put(".ogg", "audio/ogg");
        EXT_TO_MIME.put(".mp3", "audio/mpeg");
    }

    public static String resolveMimeType(File file) {
        if (file == null) return "content/unknown";
        String name = file.getName().toLowerCase();
        if (name.endsWith(".9.png")) {
            return MIME_NINE_PATCH;
        }

        int dotIdx = name.lastIndexOf('.');
        if (dotIdx >= 0) {
            String ext = name.substring(dotIdx);
            return EXT_TO_MIME.getOrDefault(ext, "content/unknown");
        }
        return "content/unknown";
    }
}
