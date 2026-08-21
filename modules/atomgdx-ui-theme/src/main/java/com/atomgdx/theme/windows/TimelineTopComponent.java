package com.atomgdx.theme.windows;

import com.atomgdx.theme.timeline.TimelinePanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for Dope-Sheet Keyframe Timeline & Animation Player.
 */
public class TimelineTopComponent extends TopComponent {
    private static TimelineTopComponent instance;

    public TimelineTopComponent() {
        setName("Animation Timeline");
        setToolTipText("Dope-Sheet Keyframe Track Editor & Animation Player");
        setLayout(new BorderLayout());
        add(new TimelinePanel(), BorderLayout.CENTER);
    }

    public static synchronized TimelineTopComponent getDefault() {
        if (instance == null) instance = new TimelineTopComponent();
        return instance;
    }

    public static synchronized TimelineTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("TimelineTopComponent");
        if (tc instanceof TimelineTopComponent) return (TimelineTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "TimelineTopComponent"; }
}
