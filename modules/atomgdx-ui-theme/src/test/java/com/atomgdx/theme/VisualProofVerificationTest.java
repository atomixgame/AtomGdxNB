package com.atomgdx.theme;

import com.atomgdx.ai.AiAssistantService;
import com.atomgdx.ai.ui.AiCopilotPanel;
import com.atomgdx.core.settings.AtomGdxSettings;
import com.atomgdx.editor.particle2d.Particle2DEffectModel;
import com.atomgdx.editor.particle2d.ui.Particle2DEditorPanel;
import com.atomgdx.editor.scene2d.Scene2DModel;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import com.atomgdx.editor.skin.SkinModel;
import com.atomgdx.editor.skin.ui.SkinComposerPanel;
import com.atomgdx.languages.glsl.ui.GlslShaderEditorPanel;
import com.atomgdx.viewer3d.Model3DDescriptor;
import com.atomgdx.viewer3d.ui.Model3DViewerPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class VisualProofVerificationTest {

    @BeforeAll
    static void setupTheme() {
        System.setProperty("java.awt.headless", "true");
        SciFiTheme.setup();
    }

    private static void renderComponentToImage(JComponent comp, int width, int height, File outputFile) throws IOException {
        comp.setSize(width, height);
        comp.doLayout();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        comp.paint(g2);
        g2.dispose();

        if (outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        ImageIO.write(image, "png", outputFile);
    }

    @Test
    void testRenderAboutDialogProof() throws IOException {
        AboutDialogPanel about = new AboutDialogPanel();
        File proof = new File("docs/assets/about_dialog_proof.png");
        renderComponentToImage(about, 640, 420, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderPreferencesDialogProof() throws IOException {
        AtomGdxPreferencesPanel prefs = new AtomGdxPreferencesPanel(new AtomGdxSettings());
        File proof = new File("docs/assets/preferences_dialog_proof.png");
        renderComponentToImage(prefs, 650, 520, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderParticleEditorProof() throws IOException {
        Particle2DEditorPanel editor = new Particle2DEditorPanel(new Particle2DEffectModel("PlasmaStorm"));
        File proof = new File("docs/assets/particle_editor_proof.png");
        renderComponentToImage(editor, 800, 500, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderShaderEditorProof() throws IOException {
        GlslShaderEditorPanel shaderEditor = new GlslShaderEditorPanel(null);
        File proof = new File("docs/assets/shader_editor_proof.png");
        renderComponentToImage(shaderEditor, 800, 500, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderSkinComposerProof() throws IOException {
        SkinComposerPanel skinComposer = new SkinComposerPanel(new SkinModel());
        File proof = new File("docs/assets/skin_composer_proof.png");
        renderComponentToImage(skinComposer, 800, 500, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderScene2DEditorProof() throws IOException {
        Scene2DEditorPanel sceneEditor = new Scene2DEditorPanel(new Scene2DModel("CyberLevel"));
        File proof = new File("docs/assets/scene2d_editor_proof.png");
        renderComponentToImage(sceneEditor, 850, 520, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderModel3DViewerProof() throws IOException {
        Model3DViewerPanel viewer3d = new Model3DViewerPanel(new Model3DDescriptor(new File("ship.gltf")));
        File proof = new File("docs/assets/model3d_viewer_proof.png");
        renderComponentToImage(viewer3d, 800, 500, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }

    @Test
    void testRenderAiCopilotProof() throws IOException {
        AiCopilotPanel copilot = new AiCopilotPanel(new AiAssistantService(new AtomGdxSettings()));
        File proof = new File("docs/assets/ai_copilot_proof.png");
        renderComponentToImage(copilot, 400, 550, proof);
        assertThat(proof).exists();
        assertThat(proof.length()).isGreaterThan(0);
    }
}
