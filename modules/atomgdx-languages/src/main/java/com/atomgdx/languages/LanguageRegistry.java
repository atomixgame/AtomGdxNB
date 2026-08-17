package com.atomgdx.languages;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry of supported programming and markup languages in AtomGdx Studio.
 */
public class LanguageRegistry {
    public static class LanguageSpec {
        private final String name;
        private final String mimeType;
        private final Set<String> extensions;

        public LanguageSpec(String name, String mimeType, Set<String> extensions) {
            this.name = name;
            this.mimeType = mimeType;
            this.extensions = extensions;
        }

        public String getName() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }

        public Set<String> getExtensions() {
            return extensions;
        }
    }

    private static final Map<String, LanguageSpec> SPEC_BY_EXT = new HashMap<>();

    static {
        register(new LanguageSpec("GLSL Vertex Shader", "text/x-glsl-vertex", Set.of(".vert", ".vsh")));
        register(new LanguageSpec("GLSL Fragment Shader", "text/x-glsl-fragment", Set.of(".frag", ".fsh")));
        register(new LanguageSpec("GLSL Shader", "text/x-glsl", Set.of(".glsl")));
        register(new LanguageSpec("Java", "text/x-java", Set.of(".java")));
        register(new LanguageSpec("Kotlin", "text/x-kotlin", Set.of(".kt", ".kts")));
        register(new LanguageSpec("Groovy", "text/x-groovy", Set.of(".groovy", ".gradle")));
        register(new LanguageSpec("JavaScript", "text/javascript", Set.of(".js", ".mjs")));
        register(new LanguageSpec("HTML", "text/html", Set.of(".html", ".htm")));
        register(new LanguageSpec("JSON", "application/json", Set.of(".json", ".skin")));
        register(new LanguageSpec("YAML", "text/x-yaml", Set.of(".yaml", ".yml")));
        register(new LanguageSpec("XML", "text/xml", Set.of(".xml", ".tmx")));
    }

    private static void register(LanguageSpec spec) {
        for (String ext : spec.getExtensions()) {
            SPEC_BY_EXT.put(ext.toLowerCase(), spec);
        }
    }

    public static LanguageSpec getLanguageByExtension(String ext) {
        if (ext == null) return null;
        if (!ext.startsWith(".")) ext = "." + ext;
        return SPEC_BY_EXT.get(ext.toLowerCase());
    }

    public static Map<String, LanguageSpec> getAllSpecs() {
        return Collections.unmodifiableMap(SPEC_BY_EXT);
    }
}
