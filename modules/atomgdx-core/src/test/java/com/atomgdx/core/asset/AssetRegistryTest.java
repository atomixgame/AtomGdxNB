package com.atomgdx.core.asset;

import com.atomgdx.core.project.LibGdxProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class AssetRegistryTest {

    @Test
    void testAssetScanningAndCategorization(@TempDir File tempDir) throws IOException {
        File assetsDir = new File(tempDir, "assets");
        assetsDir.mkdirs();

        // Create sample assets
        new File(assetsDir, "hero.png").createNewFile();
        new File(assetsDir, "sprites.atlas").createNewFile();
        new File(assetsDir, "button.9.png").createNewFile();
        new File(assetsDir, "explosion.p").createNewFile();
        new File(assetsDir, "laser.wav").createNewFile();
        new File(assetsDir, "shader.vert").createNewFile();
        new File(assetsDir, "skin.json").createNewFile();
        new File(assetsDir, "model.gltf").createNewFile();

        LibGdxProject project = new LibGdxProject("TestGame", "com.test", "Main", tempDir);
        AssetRegistry registry = new AssetRegistry(project);

        assertThat(registry.getAssets()).hasSize(8);
        assertThat(registry.getAssetsByType(AssetType.TEXTURE)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.ATLAS)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.NINE_PATCH)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.PARTICLE_2D)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.AUDIO)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.SHADER)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.SKIN)).hasSize(1);
        assertThat(registry.getAssetsByType(AssetType.MODEL_3D)).hasSize(1);

        // Test Listener
        AtomicBoolean listenerCalled = new AtomicBoolean(false);
        registry.addListener(event -> {
            if (event.getKind() == AssetChangeEvent.Kind.MODIFIED) {
                listenerCalled.set(true);
            }
        });

        AssetItem hero = registry.findByRelativePath("hero.png");
        assertThat(hero).isNotNull();
        registry.notifyAssetChanged(hero, AssetChangeEvent.Kind.MODIFIED);
        assertThat(listenerCalled.get()).isTrue();
    }
}
