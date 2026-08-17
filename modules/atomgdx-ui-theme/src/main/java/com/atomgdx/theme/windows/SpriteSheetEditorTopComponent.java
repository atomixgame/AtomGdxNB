package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.ui.SpriteSheetEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.io.File;

/**
 * NetBeans TopComponent for the SpriteSheet Editor & Image Viewer.
 */
public class SpriteSheetEditorTopComponent extends TopComponent {

    private final SpriteSheetEditorPanel editorPanel;

    public SpriteSheetEditorTopComponent() {
        this(null);
    }

    public SpriteSheetEditorTopComponent(File imageFile) {
        setName(imageFile != null ? imageFile.getName() + " - SpriteSheet" : "SpriteSheet Editor");
        setToolTipText("LibGDX SpriteSheet Slicer, Animator & Image Viewer");
        setLayout(new BorderLayout());

        this.editorPanel = new SpriteSheetEditorPanel(imageFile);
        add(editorPanel, BorderLayout.CENTER);
    }

    public SpriteSheetEditorPanel getEditorPanel() {
        return editorPanel;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "SpriteSheetEditorTopComponent";
    }
}
