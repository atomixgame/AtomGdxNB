package com.atomgdx.viewer3d;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class Model3DDescriptorTest {

    @Test
    void testFormatDetectionAndProperties() {
        Model3DDescriptor gltfDesc = new Model3DDescriptor(new File("models/robot.gltf"));
        assertThat(gltfDesc.getFormat()).isEqualTo(Model3DDescriptor.Format.GLTF);

        Model3DDescriptor glbDesc = new Model3DDescriptor(new File("models/spaceship.glb"));
        assertThat(glbDesc.getFormat()).isEqualTo(Model3DDescriptor.Format.GLB);

        gltfDesc.addAnimationName("Walk");
        gltfDesc.addAnimationName("Attack");
        gltfDesc.setAnimationCount(2);
        assertThat(gltfDesc.getAnimationNames()).containsExactly("Walk", "Attack");
    }
}
