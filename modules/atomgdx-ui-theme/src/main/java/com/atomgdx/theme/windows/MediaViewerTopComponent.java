package com.atomgdx.theme.windows;

import com.atomgdx.viewer.media.ui.MediaViewerPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class MediaViewerTopComponent extends TopComponent {
    private static MediaViewerTopComponent instance;

    public MediaViewerTopComponent() {
        setName("Audio Media Studio");
        setToolTipText("LibGDX Audio Waveform & Media Player");
        setLayout(new BorderLayout());
        add(new MediaViewerPanel(), BorderLayout.CENTER);
    }

    public static synchronized MediaViewerTopComponent getDefault() {
        if (instance == null) instance = new MediaViewerTopComponent();
        return instance;
    }

    public static synchronized MediaViewerTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("MediaViewerTopComponent");
        if (tc instanceof MediaViewerTopComponent) return (MediaViewerTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "MediaViewerTopComponent"; }
}
