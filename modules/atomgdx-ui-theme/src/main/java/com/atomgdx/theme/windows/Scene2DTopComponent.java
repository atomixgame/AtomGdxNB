package com.atomgdx.theme.windows;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.editor.scene2d.data.vo.HyperLap2DSerializer;
import com.atomgdx.editor.scene2d.data.vo.MainItemVO;
import com.atomgdx.editor.scene2d.data.vo.SceneVO;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NetBeans TopComponent for the dedicated Center HyperLap2D Scene Editor.
 * Loads and edits real .dt scene files with dynamic live sync to SceneStructure and Inspector.
 */
@TopComponent.Description(
        preferredID = "Scene2DTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.atomgdx.theme.windows.Scene2DTopComponent")
@ActionReferences({
        @ActionReference(path = "Menu/Window", position = 280),
        @ActionReference(path = "Menu/Window/2D", position = 200)
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_Scene2DAction",
        preferredID = "Scene2DTopComponent"
)
@Messages({
        "CTL_Scene2DAction=2D Scene Editor",
        "CTL_Scene2DTopComponent=2D Scene Editor",
        "HINT_Scene2DTopComponent=Interactive HyperLap2D Composite Scene2D Editor"
})
public class Scene2DTopComponent extends TopComponent implements org.openide.util.LookupListener {

    private final InstanceContent instanceContent = new InstanceContent();
    private final Scene2DEditorPanel editorPanel;
    private final SceneVO scene;
    private File sceneFile;
    private SceneSavable savable = null;
    private boolean isModified = false;
    private org.openide.util.Lookup.Result<MainItemVO> lookupResult;

    public Scene2DTopComponent() {
        this((File) null);
    }

    public Scene2DTopComponent(File file) {
        this.sceneFile = file;
        this.scene = loadSceneFromFileOrDefault(file);
        setName(this.scene.sceneName + " - Scene (2D)");
        setToolTipText(file != null ? file.getAbsolutePath() : "HyperLap2D Composite Scene2D Editor");
        setLayout(new BorderLayout());
        setBackground(DarkThemeUtils.BG_DARK);

        // Associate NetBeans Lookup with this TopComponent
        associateLookup(new AbstractLookup(instanceContent));

        this.editorPanel = new Scene2DEditorPanel(this.scene);
        this.editorPanel.setItemSelectionListener(item -> {
            updateLookup(item);
        });
        this.editorPanel.setMultiSelectionListener(items -> {
            updateMultiLookup(items);
        });
        this.editorPanel.setSaveHandler(() -> {
            try {
                doSave();
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error saving scene: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });

        add(editorPanel, BorderLayout.CENTER);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (editorPanel != null && editorPanel.getGdxViewport() != null) {
                    editorPanel.getGdxViewport().stretchCanvasToFit();
                }
            }
        });
        updateLookup(null);
    }

    public Scene2DTopComponent(SceneVO scene) {
        this.sceneFile = null;
        this.scene = scene != null ? scene : new SceneVO("MainScene");
        setName(this.scene.sceneName + " - Scene (2D)");
        setToolTipText("HyperLap2D Composite Scene2D Editor");
        setLayout(new BorderLayout());
        setBackground(DarkThemeUtils.BG_DARK);

        associateLookup(new AbstractLookup(instanceContent));

        this.editorPanel = new Scene2DEditorPanel(this.scene);
        this.editorPanel.setItemSelectionListener(item -> {
            updateLookup(item);
        });
        this.editorPanel.setMultiSelectionListener(items -> {
            updateMultiLookup(items);
        });
        this.editorPanel.setSaveHandler(() -> {
            try {
                doSave();
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error saving scene: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });

        add(editorPanel, BorderLayout.CENTER);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (editorPanel != null && editorPanel.getGdxViewport() != null) {
                    editorPanel.getGdxViewport().stretchCanvasToFit();
                }
            }
        });
        updateLookup(null);
    }

    public void markModified() {
        if (!isModified) {
            isModified = true;
            if (savable == null) {
                savable = new SceneSavable();
            }
            instanceContent.add(savable);
            String baseName = sceneFile != null ? sceneFile.getName() : (scene != null ? scene.sceneName : "Scene");
            setHtmlDisplayName("<html><b>" + baseName + " *</b></html>");
        }
    }

    public void doSave() throws java.io.IOException {
        if (sceneFile == null) {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setSelectedFile(new File(scene.sceneName + ".dt"));
            if (chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                sceneFile = chooser.getSelectedFile();
            } else {
                return;
            }
        }
        HyperLap2DSerializer.saveSceneToFile(scene, sceneFile);
        if (savable != null) {
            savable.markSaved();
        } else {
            isModified = false;
            setHtmlDisplayName(null);
        }
    }

    private class SceneSavable extends org.netbeans.spi.actions.AbstractSavable {
        SceneSavable() {
            register();
        }

        @Override
        protected String findDisplayName() {
            return sceneFile != null ? sceneFile.getName() : (scene != null ? scene.sceneName + ".dt" : "Scene2D");
        }

        @Override
        protected void handleSave() throws java.io.IOException {
            doSave();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof SceneSavable) {
                return ((SceneSavable) other).getTopComponent() == Scene2DTopComponent.this;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Scene2DTopComponent.this.hashCode();
        }

        Scene2DTopComponent getTopComponent() {
            return Scene2DTopComponent.this;
        }

        void markSaved() {
            unregister();
            savable = null;
            isModified = false;
            setHtmlDisplayName(null);
        }
    }

    private void updateLookup(MainItemVO selectedItem) {
        List<Object> items = new ArrayList<>();
        items.add(this);
        if (savable != null) {
            items.add(savable);
        }
        if (scene != null) {
            items.add(scene);
        }
        if (sceneFile != null) {
            items.add(sceneFile);
        }
        if (selectedItem != null) {
            items.add(selectedItem);
        }
        instanceContent.set(items, null);
    }

    private void updateMultiLookup(List<MainItemVO> selectedItems) {
        List<Object> items = new ArrayList<>();
        items.add(this);
        if (savable != null) {
            items.add(savable);
        }
        if (scene != null) items.add(scene);
        if (sceneFile != null) items.add(sceneFile);
        if (selectedItems != null) items.addAll(selectedItems);
        instanceContent.set(items, null);
    }

    private static SceneVO loadSceneFromFileOrDefault(File file) {
        if (file != null && file.exists()) {
            try {
                return HyperLap2DSerializer.loadSceneFromFile(file);
            } catch (Exception ex) {
                System.err.println("Error loading scene from " + file.getAbsolutePath() + ": " + ex.getMessage());
            }
        }
        return loadDefaultOrWorkspaceScene();
    }

    private static SceneVO loadDefaultOrWorkspaceScene() {
        File[] candidatePaths = new File[]{
                new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/scenes/MainScene.dt"),
                new File("Workspace/NeonCosmos/assets/scenes/MainScene.dt"),
                new File("assets/scenes/MainScene.dt")
        };
        for (File candidate : candidatePaths) {
            if (candidate.exists()) {
                try {
                    return HyperLap2DSerializer.loadSceneFromFile(candidate);
                } catch (Exception ignored) {}
            }
        }
        return new SceneVO("MainScene");
    }

    public File getSceneFile() {
        return sceneFile;
    }

    public Scene2DEditorPanel getEditorPanel() {
        return editorPanel;
    }

    public SceneVO getScene() {
        return scene;
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        lookupResult = org.openide.util.Utilities.actionsGlobalContext().lookupResult(MainItemVO.class);
        lookupResult.addLookupListener(this);
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (editorPanel != null && editorPanel.getViewportListener() != null) {
                editorPanel.getViewportListener().fitSceneToViewport();
            }
        });
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        if (lookupResult != null) {
            lookupResult.removeLookupListener(this);
            lookupResult = null;
        }
    }

    @Override
    public void resultChanged(org.openide.util.LookupEvent ev) {
        if (lookupResult != null) {
            for (MainItemVO item : lookupResult.allInstances()) {
                if (item != null && editorPanel != null) {
                    editorPanel.selectItem(item);
                    break;
                }
            }
        }
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Scene2DTopComponent";
    }
}

