package com.atomgdx.theme.windows;

import com.atomgdx.theme.graph.AnimatorControllerPanel;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;

public class AnimatorControllerTopComponent extends TopComponent {
    public AnimatorControllerTopComponent() {
        setName("Animator State Machine & Blend Tree");
        setToolTipText("Animation State Machine, Transitions & 2D Blend Trees (Unity Animator style)");
        setLayout(new BorderLayout());
        add(new AnimatorControllerPanel(), BorderLayout.CENTER);
    }
    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "AnimatorControllerTopComponent"; }
}
