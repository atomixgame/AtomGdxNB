package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.Scene2DModel;
import com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.*;

public class Scene2DTopComponent extends TopComponent {

    public Scene2DTopComponent() {
        setName("HyperLap2D Scene");
        setToolTipText("HyperLap2D Scene2D Level & Layout Designer");
        setLayout(new BorderLayout());
        add(new Scene2DEditorPanel(new Scene2DModel("NeonCosmosMainStage")), BorderLayout.CENTER);
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
