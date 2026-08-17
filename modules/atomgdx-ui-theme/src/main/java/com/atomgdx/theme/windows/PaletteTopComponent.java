package com.atomgdx.theme.windows;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.viewer3d.data.Material3DVO;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.ui.Palette3DPanel;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

import java.awt.*;

/**
 * NetBeans TopComponent for 3D Asset Palette (Primitives, Prefabs, Materials).
 * Docks in the palette/explorer area to enable drag-and-drop and instantiation into 3D scenes.
 */
public class PaletteTopComponent extends TopComponent {

    private final InstanceContent instanceContent = new InstanceContent();
    private final Palette3DPanel palettePanel;

    public PaletteTopComponent() {
        setName("Palette 3D");
        setToolTipText("3D Shapes, Prefabs and Material Palette");
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
