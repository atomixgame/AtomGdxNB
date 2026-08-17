package com.atomgdx.core.viewport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Automatically extracts native LWJGL and LibGDX shared libraries (.dll, .so, .dylib)
 * and configures org.lwjgl.librarypath for NetBeans Platform OSGi/Module classloaders.
 */
public class LwjglNativesLoader {

    private static boolean loaded = false;

    public static synchronized void load() {
        if (loaded) return;

        try {
            File nativesDir = new File(System.getProperty("java.io.tmpdir"), "atomgdx-natives");
            if (!nativesDir.exists()) {
                nativesDir.mkdirs();
            }

            // Extract from classpath jar resources
            String[] nativeLibs = {
                    "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll",
                    "gdx.dll", "gdx64.dll", "jinput-raw.dll", "jinput-raw_64.dll",
                    "jinput-dx8.dll", "jinput-dx8_64.dll"
            };

            for (String lib : nativeLibs) {
                File target = new File(nativesDir, lib);
                if (!target.exists() || target.length() == 0) {
                    try (InputStream in = LwjglNativesLoader.class.getResourceAsStream("/" + lib)) {
                        if (in != null) {
                            try (FileOutputStream out = new FileOutputStream(target)) {
                                byte[] buf = new byte[8192];
                                int len;
                                while ((len = in.read(buf)) != -1) {
                                    out.write(buf, 0, len);
                                }
                            }
                        }
                    }
                }
            }

            // Also check release/modules/ext/ jar files in suite
            File suiteExtDir = new File(System.getProperty("netbeans.user", "."), "modules/ext");
            extractJarNatives(nativesDir);

            System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
            System.setProperty("net.java.games.input.librarypath", nativesDir.getAbsolutePath());
            loaded = true;
        } catch (Throwable t) {
            System.err.println("AtomGDX NativesLoader error: " + t.getMessage());
        }
    }

    private static void extractJarNatives(File targetDir) {
        String classPath = System.getProperty("java.class.path", "");
        String[] entries = classPath.split(File.pathSeparator);
        for (String entry : entries) {
            if (entry.contains("natives") && entry.endsWith(".jar")) {
                extractAllFromJar(new File(entry), targetDir);
            }
        }
    }

    public static void extractAllFromJar(File jarFile, File targetDir) {
        if (!jarFile.exists()) return;
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                if (!entry.isDirectory() && (entry.getName().endsWith(".dll") || entry.getName().endsWith(".so") || entry.getName().endsWith(".dylib"))) {
                    String fileName = new File(entry.getName()).getName();
                    File dest = new File(targetDir, fileName);
                    if (!dest.exists() || dest.length() == 0) {
                        try (InputStream in = jar.getInputStream(entry);
                             FileOutputStream out = new FileOutputStream(dest)) {
                            byte[] buf = new byte[8192];
                            int len;
                            while ((len = in.read(buf)) != -1) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
