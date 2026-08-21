package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.AnimatorControllerPanel;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class AnimatorControllerTopComponent extends TopComponent {
    private static AnimatorControllerTopComponent instance;

    public AnimatorControllerTopComponent() {
        setName("Animator State Machine & Blend Tree");
        setToolTipText("Animation State Machine, Transitions & 2D Blend Trees (Unity Animator style)");
        setLayout(new BorderLayout());
        add(new AnimatorControllerPanel(), BorderLayout.CENTER);
    }

    public static synchronized AnimatorControllerTopComponent getDefault() {
        if (instance == null) instance = new AnimatorControllerTopComponent();
        return instance;
    }

    public static synchronized AnimatorControllerTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("AnimatorControllerTopComponent");
        if (tc instanceof AnimatorControllerTopComponent) return (AnimatorControllerTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "AnimatorControllerTopComponent"; }
}
