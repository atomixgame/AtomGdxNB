package com.atomgdx.theme.windows;

import com.atomgdx.tools.texturepacker.NinePatchEditorModel;
import com.atomgdx.tools.texturepacker.ui.NinePatchEditorPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class NinePatchEditorTopComponent extends TopComponent {
    private static NinePatchEditorTopComponent instance;

    public NinePatchEditorTopComponent() {
        setName("9-Patch Slicer");
        setToolTipText("LibGDX 9-Patch Texture Slice & Scale Tool");
        setLayout(new BorderLayout());
        add(new NinePatchEditorPanel(new NinePatchEditorModel(12, 12, 12, 12)), BorderLayout.CENTER);
    }

    public static synchronized NinePatchEditorTopComponent getDefault() {
        if (instance == null) instance = new NinePatchEditorTopComponent();
        return instance;
    }

    public static synchronized NinePatchEditorTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("NinePatchEditorTopComponent");
        if (tc instanceof NinePatchEditorTopComponent) return (NinePatchEditorTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "NinePatchEditorTopComponent"; }
}
