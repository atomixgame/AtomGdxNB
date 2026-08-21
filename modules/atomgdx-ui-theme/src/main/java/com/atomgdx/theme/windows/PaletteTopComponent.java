package com.atomgdx.theme.windows;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.ui.Palette3DPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import java.awt.*;

/**
 * NetBeans TopComponent for 3D Asset Palette (Primitives, Prefabs, Materials).
 * Docks in the palette/explorer area to enable drag-and-drop and instantiation into 3D scenes.
 */
@TopComponent.Description(
        preferredID = "PaletteTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "com.atomgdx.theme.windows.PaletteTopComponent")
@ActionReference(path = "Menu/Window", position = 320)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PaletteAction",
        preferredID = "PaletteTopComponent"
)
@Messages({
        "CTL_PaletteAction=Palette",
        "CTL_PaletteTopComponent=Palette 3D",
        "HINT_PaletteTopComponent=2D/3D Shapes, Prefabs and Material Palette"
})
public class PaletteTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private final Palette3DPanel palettePanel;

    public PaletteTopComponent() {
        setName(Bundle.CTL_PaletteTopComponent());
        setToolTipText(Bundle.HINT_PaletteTopComponent());
        setLayout(new BorderLayout());
        setBackground(DarkThemeUtils.BG_DARK);

        associateLookup(new AbstractLookup(instanceContent));

        palettePanel = new Palette3DPanel();
        palettePanel.setItemSelectedListener(item -> {
            instanceContent.set(item != null ? java.util.Collections.singleton(item) : java.util.Collections.emptyList(), null);
        });

        add(palettePanel, BorderLayout.CENTER);
    }

    public Palette3DPanel getPalettePanel() {
        return palettePanel;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "PaletteTopComponent";
    }
}
