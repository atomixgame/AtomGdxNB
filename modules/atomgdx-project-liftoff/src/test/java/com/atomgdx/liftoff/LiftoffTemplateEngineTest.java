package com.atomgdx.liftoff;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.project.PlatformType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class LiftoffTemplateEngineTest {

    @Test
    public void testGenerateCompleteProject(@TempDir File tempDir) throws IOException {
        LiftoffProjectDescriptor descriptor = new LiftoffProjectDescriptor();
        descriptor.setProjectName("GalaxyRacer");
        descriptor.setPackageName("com.galaxy.racer");
        descriptor.setMainClassName("GalaxyRacerGame");
        descriptor.setDestinationDir(tempDir);
        descriptor.getPlatforms().add(PlatformType.ANDROID);
        descriptor.getPlatforms().add(PlatformType.WEB_GWT);
        descriptor.getExtensions().add(ExtensionType.VIS_UI);
        descriptor.getExtensions().add(ExtensionType.ASHLEY);
        descriptor.getExtensions().add(ExtensionType.BOX2D);

        LibGdxProject project = LiftoffTemplateEngine.generateProject(descriptor);

        assertNotNull(project);
        assertEquals("GalaxyRacer", project.getName());

        File rootDir = new File(tempDir, "GalaxyRacer");
        assertTrue(rootDir.exists(), "Root project directory must exist");
        assertTrue(new File(rootDir, "build.gradle").exists(), "Root build.gradle must exist");
        assertTrue(new File(rootDir, "settings.gradle").exists(), "Root settings.gradle must exist");
        assertTrue(new File(rootDir, "gradle.properties").exists(), "gradle.properties must exist");
        assertTrue(new File(rootDir, "assets").exists(), "Assets directory must exist");
        assertTrue(new File(rootDir, "core/build.gradle").exists(), "Core build.gradle must exist");
        assertTrue(new File(rootDir, "core/src/main/java/com/galaxy/racer/GalaxyRacerGame.java").exists(), "MainGame class must exist");
        assertTrue(new File(rootDir, "lwjgl3/build.gradle").exists(), "Lwjgl3 build.gradle must exist");
        assertTrue(new File(rootDir, "lwjgl3/src/main/java/com/galaxy/racer/lwjgl3/Lwjgl3Launcher.java").exists(), "Lwjgl3Launcher must exist");
        assertTrue(new File(rootDir, "android/build.gradle").exists(), "Android build.gradle must exist");
        assertTrue(new File(rootDir, "android/src/main/java/com/galaxy/racer/android/AndroidLauncher.java").exists(), "AndroidLauncher must exist");
        assertTrue(new File(rootDir, "html/build.gradle").exists(), "Html build.gradle must exist");
    }
}
