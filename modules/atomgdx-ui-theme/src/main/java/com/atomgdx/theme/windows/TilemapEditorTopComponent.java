package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.tilemap.ui.TilemapEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for TileMap Studio & 2D Level Construction Suite.
 */
public class TilemapEditorTopComponent extends TopComponent {

    public TilemapEditorTopComponent() {
        setName("TileMap Studio");
        setToolTipText("LibGDX 2D Tilemap Editor (Orthogonal, Isometric, Hexagonal & Rule Tiles)");
        setLayout(new BorderLayout());
        add(new TilemapEditorPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "TilemapEditorTopComponent";
    }
}
