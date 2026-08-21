package com.atomgdx.theme.windows;

import com.atomgdx.ai.AiAssistantService;
import com.atomgdx.ai.ui.AiCopilotPanel;
import com.atomgdx.core.settings.AtomGdxSettings;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.BorderLayout;

public class AiAssistantTopComponent extends TopComponent {
    private static AiAssistantTopComponent instance;

    public AiAssistantTopComponent() {
        setName("AI Copilot");
        setToolTipText("AtomGdx AI Assistant & Scene Designer");
        setLayout(new BorderLayout());
        add(new AiCopilotPanel(new AiAssistantService(new AtomGdxSettings())), BorderLayout.CENTER);
    }

    public static synchronized AiAssistantTopComponent getDefault() {
        if (instance == null) instance = new AiAssistantTopComponent();
        return instance;
    }

    public static synchronized AiAssistantTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("AiAssistantTopComponent");
        if (tc instanceof AiAssistantTopComponent) return (AiAssistantTopComponent) tc;
        return getDefault();
    }

    @Override public int getPersistenceType() { return PERSISTENCE_ALWAYS; }
    @Override protected String preferredID() { return "AiAssistantTopComponent"; }
}
