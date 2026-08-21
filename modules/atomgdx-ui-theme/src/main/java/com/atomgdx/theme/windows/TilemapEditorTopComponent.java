package com.atomgdx.theme.windows;

import com.atomgdx.editor.scene2d.tilemap.ui.TilemapEditorPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for TileMap Studio & 2D Level Construction Suite.
 */
public class TilemapEditorTopComponent extends TopComponent {

    private static TilemapEditorTopComponent instance;

    public TilemapEditorTopComponent() {
        setName("TileMap Studio");
        setToolTipText("LibGDX 2D Tilemap Editor (Orthogonal, Isometric, Hexagonal & Rule Tiles)");
        setLayout(new BorderLayout());
        add(new TilemapEditorPanel(), BorderLayout.CENTER);
    }

    public static synchronized TilemapEditorTopComponent getDefault() {
        if (instance == null) instance = new TilemapEditorTopComponent();
        return instance;
    }

    public static synchronized TilemapEditorTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("TilemapEditorTopComponent");
        if (tc instanceof TilemapEditorTopComponent) return (TilemapEditorTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "TilemapEditorTopComponent"; }
}
