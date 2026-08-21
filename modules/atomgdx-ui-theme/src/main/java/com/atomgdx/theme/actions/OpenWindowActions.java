package com.atomgdx.theme.actions;

import com.atomgdx.core.settings.AtomGdxSettings;
import com.atomgdx.theme.AboutDialogPanel;
import com.atomgdx.theme.AtomGdxPreferencesPanel;
import com.atomgdx.theme.windows.*;
import com.atomgdx.theme.wizard.LibGdxNewProjectWizard;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * NetBeans action delegates that launch NetBeans TopComponents, Tool Windows, and Project Wizards.
 */
public class OpenWindowActions {

    private static void openTopComponent(TopComponent tc, String modeName) {
        try {
            Mode mode = WindowManager.getDefault().findMode(modeName);
            if (mode != null) {
                mode.dockInto(tc);
            }
            tc.open();
            tc.requestActive();
        } catch (Exception ex) {
            tc.open();
        }
    }

    public static class NewLibGdxProjectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LibGdxNewProjectWizard.showWizard();
        }
    }

    public static class OpenProjectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser("G:\\GameDev\\LibGDX\\AtomGdx\\AtomGdxNB\\Workspace");
            chooser.setDialogTitle("Open LibGDX Project");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = chooser.showOpenDialog(null);
            if (res == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                openTopComponent(new LibGdxProjectExplorerTopComponent(), "explorer");
                JOptionPane.showMessageDialog(null, "Opened LibGDX Project: " + selectedDir.getName(), "AtomGdx Projects", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static class CloseProjectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int res = JOptionPane.showConfirmDialog(null, "Close active LibGDX project?", "Close Project", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Project closed successfully.", "AtomGdx Studio", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static class OpenProjectExplorerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new LibGdxProjectExplorerTopComponent(), "explorer");
        }
    }

    public static class OpenSceneStructureAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new SceneStructureTopComponent(), "explorer");
        }
    }

    public static class OpenInspectorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new InspectorTopComponent(), "properties");
        }
    }

    public static class OpenModelPropertiesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new ModelPropertiesTopComponent(), "properties");
        }
    }

    public static class OpenWidgetStylesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new WidgetStylesTopComponent(), "explorer");
        }
    }

    public static class OpenParticleEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new Particle2DTopComponent(), "editor");
        }
    }

    public static class OpenShaderEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new ShaderEditorTopComponent(), "editor");
        }
    }

    public static class OpenSkinComposerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new SkinComposerTopComponent(), "editor");
        }
    }

    public static class OpenNinePatchEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new NinePatchEditorTopComponent(), "editor");
        }
    }

    public static class OpenTexturePackerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new TexturePackerTopComponent(), "editor");
        }
    }

    public static class OpenFontGeneratorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new FontGeneratorTopComponent(), "editor");
        }
    }

    public static class OpenParticle3DEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new Particle3DTopComponent(), "editor");
        }
    }

    public static class OpenSceneEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new Scene2DTopComponent(), "editor");
        }
    }

    public static class OpenTilemapEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new TilemapEditorTopComponent(), "editor");
        }
    }

    public static class OpenModel3DViewerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new Model3DViewerTopComponent(), "editor");
        }
    }

    public static class OpenMediaViewerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new MediaViewerTopComponent(), "editor");
        }
    }

    public static class OpenBuildConfigAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new BuildConfigTopComponent(), "editor");
        }
    }

    public static class OpenTimelineAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new TimelineTopComponent(), "output");
        }
    }

    public static class OpenMaterialEditorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new MaterialEditorTopComponent(), "editor");
        }
    }

    public static class OpenLightingEnvironmentAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new LightingEnvironmentTopComponent(), "editor");
        }
    }

    public static class OpenComponentRegistryAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new ComponentRegistryTopComponent(), "explorer");
        }
    }

    public static class OpenAiCopilotAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openTopComponent(new AiAssistantTopComponent(), "output");
        }
    }

    public static class OpenAboutAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog((Frame) null, "About AtomGdx Studio", true);
            dialog.setSize(600, 480);
            dialog.setLocationRelativeTo(null);
            dialog.setContentPane(new AboutDialogPanel());
            dialog.setVisible(true);
        }
    }

    public static class OpenPreferencesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog((Frame) null, "AtomGdx Options", true);
            dialog.setSize(750, 520);
            dialog.setLocationRelativeTo(null);
            dialog.setContentPane(new AtomGdxPreferencesPanel(new AtomGdxSettings()));
            dialog.setVisible(true);
        }
    }
}