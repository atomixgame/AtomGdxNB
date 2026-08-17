package com.atomgdx.ai;

import com.atomgdx.core.settings.AtomGdxSettings;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service managing AI Assistant conversations, prompts, and provider requests.
 */
public class AiAssistantService {
    private final AtomGdxSettings settings;
    private final List<AiMessage> conversationHistory = new CopyOnWriteArrayList<>();
    private final HttpClient httpClient;

    public AiAssistantService(AtomGdxSettings settings) {
        this.settings = settings;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        conversationHistory.add(new AiMessage(
                AiMessage.Role.SYSTEM,
                "You are AtomGdx Copilot, an expert AI assistant specialized in LibGDX game development, Java, Kotlin, OpenGL/GLSL shaders, Box2D, Ashley ECS, and Scene2D."
        ));
    }

    public List<AiMessage> getConversationHistory() {
        return Collections.unmodifiableList(conversationHistory);
    }

    public void addMessage(AiMessage message) {
        conversationHistory.add(message);
    }

    public void clearHistory() {
        conversationHistory.clear();
        conversationHistory.add(new AiMessage(
                AiMessage.Role.SYSTEM,
                "You are AtomGdx Copilot, an expert AI assistant specialized in LibGDX game development."
        ));
    }

    public CompletableFuture<String> sendMessage(String userPrompt) {
        AiMessage userMsg = new AiMessage(AiMessage.Role.USER, userPrompt);
        conversationHistory.add(userMsg);

        return CompletableFuture.supplyAsync(() -> {
            try {
                if ("OLLAMA".equalsIgnoreCase(settings.getAiProvider())) {
                    String jsonBody = "{\"model\":\"" + settings.getOllamaModel() + "\",\"prompt\":\"" + escapeJson(userPrompt) + "\",\"stream\":false}";

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(settings.getOllamaEndpoint() + "/api/generate"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .timeout(Duration.ofSeconds(60))
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        String body = response.body();
                        int respIdx = body.indexOf("\"response\":\"");
                        if (respIdx >= 0) {
                            int endIdx = body.indexOf("\"", respIdx + 12);
                            String reply = endIdx > respIdx ? body.substring(respIdx + 12, endIdx) : body;
                            conversationHistory.add(new AiMessage(AiMessage.Role.ASSISTANT, reply));
                            return reply;
                        }
                    }
                }

                String simulated = "/* [AtomGdx AI Copilot] */\n" +
                        "// Suggestion for: " + userPrompt + "\n" +
                        "// LibGDX snippet: OrthographicCamera camera = new OrthographicCamera();";
                conversationHistory.add(new AiMessage(AiMessage.Role.ASSISTANT, simulated));
                return simulated;

            } catch (Exception e) {
                String errMsg = "Error communicating with AI provider: " + e.getMessage();
                conversationHistory.add(new AiMessage(AiMessage.Role.ASSISTANT, errMsg));
                return errMsg;
            }
        });
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
