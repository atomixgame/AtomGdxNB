package com.atomgdx.ai.ui;

import com.atomgdx.ai.AiAssistantService;
import com.atomgdx.ai.AiMessage;
import com.atomgdx.core.settings.AtomGdxSettings;
import com.atomgdx.core.SciFiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Dockable AI Assistant / Copilot sidebar panel for code generation, shader debugging, and MCP tasks.
 */
public class AiCopilotPanel extends JPanel {
    private final AiAssistantService aiService;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JComboBox<String> modelCombo;

    public AiCopilotPanel(AiAssistantService aiService) {
        this.aiService = aiService != null ? aiService : new AiAssistantService(new AtomGdxSettings());
        setLayout(new BorderLayout(8, 8));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header: Model Selector & Quick Prompts
        JPanel header = new JPanel(new BorderLayout(5, 5));
        header.setOpaque(false);
        modelCombo = new JComboBox<>(new String[]{"Ollama: deepseek-coder", "Gemini 2.0 Flash", "Claude 3.5 Sonnet", "GPT-4o"});
        header.add(modelCombo, BorderLayout.CENTER);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            this.aiService.clearHistory();
            refreshChat();
        });
        header.add(clearBtn, BorderLayout.EAST);

        // Chat History Area
        chatArea = new JTextArea();
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chatArea.setBackground(SciFiColors.BG_DARK);
        chatArea.setForeground(SciFiColors.TEXT_PRIMARY);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Input & Send Section
        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputPanel.setOpaque(false);
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inputField.setBackground(SciFiColors.BG_CARD);
        inputField.setForeground(SciFiColors.TEXT_PRIMARY);
        inputField.setCaretColor(SciFiColors.ACCENT_CYAN);

        JButton sendBtn = new JButton("Send ↵");
        sendBtn.setBackground(SciFiColors.ACCENT_CYAN);
        sendBtn.setForeground(SciFiColors.BG_DARKEST);
        sendBtn.addActionListener(e -> handleSend());
        inputField.addActionListener(e -> handleSend());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        // Quick Suggestions
        JPanel suggestions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        suggestions.setOpaque(false);
        addQuickPrompt(suggestions, "Explain Shader");
        addQuickPrompt(suggestions, "Gen Particle Preset");
        addQuickPrompt(suggestions, "Box2D Car Physics");
        inputPanel.add(suggestions, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        refreshChat();
    }

    private void addQuickPrompt(JPanel panel, String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btn.addActionListener(e -> {
            inputField.setText(text);
            handleSend();
        });
        panel.add(btn);
    }

    private void handleSend() {
        String query = inputField.getText().trim();
        if (query.isEmpty()) return;
        inputField.setText("");

        aiService.sendMessage(query).thenAccept(reply -> SwingUtilities.invokeLater(this::refreshChat));
        refreshChat();
    }

    private void refreshChat() {
        StringBuilder sb = new StringBuilder();
        for (AiMessage msg : aiService.getConversationHistory()) {
            if (msg.getRole() == AiMessage.Role.SYSTEM) continue;
            sb.append("[").append(msg.getRole()).append("]:\n")
              .append(msg.getContent()).append("\n\n");
        }
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
