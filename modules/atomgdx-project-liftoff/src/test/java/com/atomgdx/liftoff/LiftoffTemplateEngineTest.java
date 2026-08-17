package com.atomgdx.liftoff;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.project.PlatformType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LiftoffTemplateEngineTest {

    @Test
    void testProjectGeneration(@TempDir File tempDir) throws IOException {
        LiftoffProjectDescriptor descriptor = new LiftoffProjectDescriptor();
        descriptor.setProjectName("NeonOdyssey");
        descriptor.setPackageName("com.atom.neon");
        descriptor.setMainClassName("NeonGame");
        descriptor.setDestinationDir(tempDir);
        descriptor.getPlatforms().add(PlatformType.DESKTOP_LWJGL3);
        descriptor.getPlatforms().add(PlatformType.ANDROID);
        descriptor.getExtensions().add(ExtensionType.BOX2D);
        descriptor.getExtensions().add(ExtensionType.VIS_UI);

        LibGdxProject project = LiftoffTemplateEngine.generateProject(descriptor);

        File root = project.getRootDirectory();
        assertThat(root).exists();
        assertThat(new File(root, "settings.gradle.kts")).exists();
        assertThat(new File(root, "build.gradle.kts")).exists();
        assertThat(new File(root, "assets")).exists();
        assertThat(new File(root, "core/build.gradle.kts")).exists();
        assertThat(new File(root, "core/src/main/java/com/atom/neon/NeonGame.java")).exists();
        assertThat(new File(root, "lwjgl3/src/main/java/com/atom/neon/lwjgl3/Lwjgl3Launcher.java")).exists();
    }
}
