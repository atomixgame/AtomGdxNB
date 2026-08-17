package com.atomgdx.theme.windows;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.Scene3DVO;
import com.atomgdx.viewer3d.ui.Scene3DEditorPanel;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import java.awt.*;

/**
 * NetBeans TopComponent for the Interactive 3D Scene & Level Editor.
 * Docks in the main editor area, supporting node placement, transform gizmo modes, and lighting/physics editing.
 */
public class Scene3DEditorTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private final Scene3DEditorPanel editorPanel;

    public Scene3DEditorTopComponent() {
        setName("3D Scene Editor - MainScene3D");
        setToolTipText("LibGDX Interactive 3D Scene & Level Editor");
        setLayout(new BorderLayout());
        setBackground(DarkThemeUtils.BG_DARK);

        associateLookup(new AbstractLookup(instanceContent));

        Scene3DVO scene = new Scene3DVO("MainScene3D");
        editorPanel = new Scene3DEditorPanel(scene);
        add(editorPanel, BorderLayout.CENTER);
    }

    public Scene3DEditorPanel getEditorPanel() {
        return editorPanel;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "Scene3DEditorTopComponent";
    }
}
