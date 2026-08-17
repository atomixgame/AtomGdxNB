package com.atomgdx.theme.windows;

import com.atomgdx.tools.texturepacker.NinePatchEditorModel;
import com.atomgdx.tools.texturepacker.ui.NinePatchEditorPanel;
import org.openide.windows.TopComponent;

import java.awt.*;

public class NinePatchEditorTopComponent extends TopComponent {

    public NinePatchEditorTopComponent() {
        setName("9-Patch Slicer");
        setToolTipText("LibGDX 9-Patch Texture Slice & Scale Tool");
        setLayout(new BorderLayout());
        NinePatchEditorModel model = new NinePatchEditorModel(12, 12, 12, 12);
        add(new NinePatchEditorPanel(model), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "NinePatchEditorTopComponent";
    }
}
