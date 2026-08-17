package com.atomgdx.ai;

import com.atomgdx.core.settings.AtomGdxSettings;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class AiAssistantServiceTest {

    @Test
    void testAiAssistantHistoryAndMessage() throws ExecutionException, InterruptedException {
        AtomGdxSettings settings = new AtomGdxSettings();
        settings.setAiProvider("SIMULATED");

        AiAssistantService service = new AiAssistantService(settings);
        assertThat(service.getConversationHistory()).hasSize(1); // System prompt

        String reply = service.sendMessage("How to implement 2D camera lerp in LibGDX?").get();
        assertThat(reply).isNotEmpty();
        assertThat(service.getConversationHistory()).hasSize(3); // System + User + Assistant
    }
}
