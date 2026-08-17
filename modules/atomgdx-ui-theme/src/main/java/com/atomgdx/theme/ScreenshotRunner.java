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
import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.SceneStructureTopComponent;
import com.atomgdx.tools.texturepacker.NinePatchEditorModel;
import com.atomgdx.tools.texturepacker.ui.NinePatchEditorPanel;
import com.atomgdx.viewer.media.ui.MediaViewerPanel;
import com.atomgdx.viewer3d.Model3DDescriptor;
import com.atomgdx.viewer3d.ui.Model3DViewerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Headless Visual Proof Screenshot Generator for AtomGdx Studio.
 */
public class ScreenshotRunner {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        SciFiTheme.setup();

        String outDir = args.length > 0 ? args[0] : "docs/assets";
        new File(outDir).mkdirs();

        try {
            renderComponent(new AboutDialogPanel(), 640, 440, new File(outDir, "about_dialog_proof.png"));
            renderComponent(new AtomGdxPreferencesPanel(new AtomGdxSettings()), 650, 520, new File(outDir, "preferences_dialog_proof.png"));
            renderComponent(new SceneStructureTopComponent(), 320, 520, new File(outDir, "scene_structure_proof.png"));
            renderComponent(new InspectorTopComponent(), 320, 520, new File(outDir, "inspector_proof.png"));
            renderComponent(new Particle2DEditorPanel(new Particle2DEffectModel("PlasmaStorm")), 800, 500, new File(outDir, "particle_editor_proof.png"));
            renderComponent(new GlslShaderEditorPanel(null), 800, 500, new File(outDir, "shader_editor_proof.png"));
            renderComponent(new SkinComposerPanel(new SkinModel()), 800, 500, new File(outDir, "skin_composer_proof.png"));
            renderComponent(new NinePatchEditorPanel(new NinePatchEditorModel(12, 12, 12, 12)), 700, 450, new File(outDir, "ninepatch_editor_proof.png"));
            renderComponent(new Scene2DEditorPanel(new Scene2DModel("CyberLevel")), 880, 520, new File(outDir, "scene2d_editor_proof.png"));
            renderComponent(new Model3DViewerPanel(new Model3DDescriptor(new File("ship.gltf"))), 800, 500, new File(outDir, "model3d_viewer_proof.png"));
            renderComponent(new MediaViewerPanel(), 800, 480, new File(outDir, "media_viewer_proof.png"));
            renderComponent(new AiCopilotPanel(new AiAssistantService(new AtomGdxSettings())), 420, 560, new File(outDir, "ai_copilot_proof.png"));

            System.out.println("ALL_PROOFS_RENDERED_SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void renderComponent(JComponent comp, int width, int height, File outputFile) throws Exception {
        comp.setSize(width, height);
        comp.doLayout();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        comp.paint(g2);
        g2.dispose();

        ImageIO.write(img, "png", outputFile);
        System.out.println("Rendered proof: " + outputFile.getAbsolutePath());
    }
}
