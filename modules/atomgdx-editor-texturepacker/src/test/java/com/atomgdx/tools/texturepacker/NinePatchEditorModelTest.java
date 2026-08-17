package com.atomgdx.tools.texturepacker;

import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.assertj.core.api.Assertions.assertThat;

class NinePatchEditorModelTest {

    @Test
    void testNinePatchPatchCalculation() {
        NinePatchEditorModel model = new NinePatchEditorModel(10, 10, 10, 10);
        assertThat(model.isValid(100, 100)).isTrue();
        assertThat(model.isValid(15, 15)).isFalse();

        Rectangle[] patches = model.calculateDestPatches(200, 100);
        assertThat(patches).hasSize(9);

        // Check Top-Left patch
        assertThat(patches[0].width).isEqualTo(10);
        assertThat(patches[0].height).isEqualTo(10);

        // Check Center patch
        assertThat(patches[4].width).isEqualTo(180);
        assertThat(patches[4].height).isEqualTo(80);
    }
}
