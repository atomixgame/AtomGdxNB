package com.atomgdx.theme.windows;

import com.atomgdx.theme.timeline.TimelinePanel;
import org.openide.windows.TopComponent;

import java.awt.BorderLayout;

/**
 * NetBeans TopComponent for Dope-Sheet Keyframe Timeline & Animation Player.
 */
public class TimelineTopComponent extends TopComponent {

    public TimelineTopComponent() {
        setName("Animation Timeline");
        setToolTipText("Dope-Sheet Keyframe Track Editor & Animation Player");
        setLayout(new BorderLayout());
        add(new TimelinePanel(), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "TimelineTopComponent";
    }
}
