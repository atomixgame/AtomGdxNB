package com.atomgdx.theme.windows;

import com.atomgdx.viewer.media.ui.MediaViewerPanel;
import org.openide.windows.TopComponent;

import java.awt.*;

public class MediaViewerTopComponent extends TopComponent {

    public MediaViewerTopComponent() {
        setName("Audio Media Studio");
        setToolTipText("LibGDX Audio, Sound & Music Player");
        setLayout(new BorderLayout());
        add(new MediaViewerPanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "MediaViewerTopComponent";
    }
}
