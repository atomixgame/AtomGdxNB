package com.atomgdx.core.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LibGdxProjectTest {

    @Test
    void testProjectCreationAndPlatforms(@TempDir File tempDir) {
        LibGdxProject project = new LibGdxProject("MySciFiGame", "com.space.game", "SpaceGame", tempDir);

        assertThat(project.getName()).isEqualTo("MySciFiGame");
        assertThat(project.getPackageName()).isEqualTo("com.space.game");
        assertThat(project.getMainClass()).isEqualTo("SpaceGame");
        assertThat(project.getRootDirectory()).isEqualTo(tempDir);
        assertThat(project.getAssetsDirectory().getName()).isEqualTo("assets");

        // Default platforms
        assertThat(project.hasPlatform(PlatformType.CORE)).isTrue();
        assertThat(project.hasPlatform(PlatformType.DESKTOP_LWJGL3)).isTrue();

        // Add extensions
        project.addExtension(ExtensionType.BOX2D);
        project.addExtension(ExtensionType.GDX_GLTF);
        assertThat(project.hasExtension(ExtensionType.BOX2D)).isTrue();
        assertThat(project.hasExtension(ExtensionType.GDX_GLTF)).isTrue();
        assertThat(project.hasExtension(ExtensionType.ASHLEY)).isFalse();
    }

    @Test
    void testProjectFactoryDetection(@TempDir File tempDir) throws IOException {
        assertThat(LibGdxProjectFactory.isLibGdxProject(tempDir)).isFalse();

        // Create Gradle file and assets directory
        new File(tempDir, "build.gradle").createNewFile();
        new File(tempDir, "assets").mkdirs();

        assertThat(LibGdxProjectFactory.isLibGdxProject(tempDir)).isTrue();
        LibGdxProject project = LibGdxProjectFactory.createFromDirectory(tempDir);
        assertThat(project).isNotNull();
    }
}
