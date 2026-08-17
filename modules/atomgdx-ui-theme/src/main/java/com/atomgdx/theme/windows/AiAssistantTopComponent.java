package com.atomgdx.theme.windows;

import com.atomgdx.ai.AiAssistantService;
import com.atomgdx.ai.ui.AiCopilotPanel;
import com.atomgdx.core.settings.AtomGdxSettings;
import org.openide.windows.TopComponent;

import java.awt.*;

public class AiAssistantTopComponent extends TopComponent {

    public AiAssistantTopComponent() {
        setName("AI Copilot");
        setToolTipText("AtomGdx AI Assistant & Scene Designer");
        setLayout(new BorderLayout());
        add(new AiCopilotPanel(new AiAssistantService(new AtomGdxSettings())), BorderLayout.CENTER);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "AiAssistantTopComponent";
    }
}
