package com.atomgdx.theme;

import com.atomgdx.core.settings.AtomGdxSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Preferences / Settings panel for configuring AI Assistant, Android SDK, and LibGDX tool options.
 * Features compact 24px-height input components.
 */
public class AtomGdxPreferencesPanel extends JPanel {
    private final AtomGdxSettings settings;

    private final JComboBox<String> aiProviderCombo;
    private final JTextField ollamaEndpointField;
    private final JTextField ollamaModelField;
    private final JPasswordField apiKeyField;
    private final JTextField androidSdkField;
    private final JCheckBox autoRepackCheckBox;
    private final JCheckBox liveShaderCheckBox;
    private final JCheckBox mcpServerCheckBox;
    private final JSpinner mcpPortSpinner;

    public AtomGdxPreferencesPanel(AtomGdxSettings settings) {
        this.settings = settings != null ? settings : new AtomGdxSettings();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SciFiColors.BG_DARKEST);
        setBorder(new EmptyBorder(10, 15, 10, 15));

        // 1. AI Assistant Section
        JPanel aiSection = createSection("AI Assistant & Model Providers");
        aiProviderCombo = new JComboBox<>(new String[]{"OLLAMA", "GEMINI", "CLAUDE", "OPENAI"});
        aiProviderCombo.setSelectedItem(this.settings.getAiProvider());
        addFormField(aiSection, "Active Provider:", aiProviderCombo);

        ollamaEndpointField = new JTextField(this.settings.getOllamaEndpoint(), 25);
        addFormField(aiSection, "Ollama Endpoint:", ollamaEndpointField);

        ollamaModelField = new JTextField(this.settings.getOllamaModel(), 25);
        addFormField(aiSection, "Ollama Model:", ollamaModelField);

        apiKeyField = new JPasswordField(this.settings.getGeminiApiKey(), 25);
        addFormField(aiSection, "Cloud API Key:", apiKeyField);
        add(aiSection);
        add(Box.createVerticalStrut(10));

        // 2. Android & Build Section
        JPanel buildSection = createSection("Android Platform & Build Tools");
        androidSdkField = new JTextField(this.settings.getAndroidSdkPath(), 25);
        addFormField(buildSection, "Android SDK Path:", androidSdkField);

        autoRepackCheckBox = new JCheckBox("Auto-repack Texture Atlas on Build", this.settings.isAutoRepackAtlasOnBuild());
        autoRepackCheckBox.setOpaque(false);
        autoRepackCheckBox.setForeground(SciFiColors.TEXT_PRIMARY);
        autoRepackCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        buildSection.add(autoRepackCheckBox);

        liveShaderCheckBox = new JCheckBox("Enable Live GLSL Shader Compilation in Viewers", this.settings.isLiveShaderPreviewEnabled());
        liveShaderCheckBox.setOpaque(false);
        liveShaderCheckBox.setForeground(SciFiColors.TEXT_PRIMARY);
        liveShaderCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        buildSection.add(liveShaderCheckBox);
        add(buildSection);
        add(Box.createVerticalStrut(10));

        // 3. MCP Server Section
        JPanel mcpSection = createSection("Model Context Protocol (MCP) Server");
        mcpServerCheckBox = new JCheckBox("Enable Embedded MCP Server", this.settings.isMcpServerEnabled());
        mcpServerCheckBox.setOpaque(false);
        mcpServerCheckBox.setForeground(SciFiColors.TEXT_PRIMARY);
        mcpServerCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mcpSection.add(mcpServerCheckBox);

        mcpPortSpinner = new JSpinner(new SpinnerNumberModel(this.settings.getMcpServerPort(), 1024, 65535, 1));
        addFormField(mcpSection, "MCP Server Port:", mcpPortSpinner);
        add(mcpSection);
        add(Box.createVerticalGlue());
    }

    private JPanel createSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(SciFiColors.BG_PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                title
        );
        border.setTitleColor(SciFiColors.ACCENT_CYAN);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        section.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(8, 12, 8, 12)));
        return section;
    }

    private void addFormField(JPanel section, String label, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(800, 26));

        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(150, 24));
        lbl.setForeground(SciFiColors.TEXT_SECONDARY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 24));
        comp.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        if (comp instanceof JTextField) {
            ((JTextField) comp).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                    new EmptyBorder(2, 4, 2, 4)
            ));
            comp.setBackground(SciFiColors.BG_DARK);
            comp.setForeground(SciFiColors.TEXT_PRIMARY);
        }

        row.add(lbl, BorderLayout.WEST);
        row.add(comp, BorderLayout.CENTER);

        section.add(row);
        section.add(Box.createVerticalStrut(4));
    }

    public void saveSettings() {
        settings.setAiProvider((String) aiProviderCombo.getSelectedItem());
        settings.setOllamaEndpoint(ollamaEndpointField.getText().trim());
        settings.setOllamaModel(ollamaModelField.getText().trim());
        settings.setGeminiApiKey(new String(apiKeyField.getPassword()).trim());
        settings.setAndroidSdkPath(androidSdkField.getText().trim());
        settings.setAutoRepackAtlasOnBuild(autoRepackCheckBox.isSelected());
        settings.setLiveShaderPreviewEnabled(liveShaderCheckBox.isSelected());
        settings.setMcpServerEnabled(mcpServerCheckBox.isSelected());
        settings.setMcpServerPort((Integer) mcpPortSpinner.getValue());
    }

    public AtomGdxSettings getSettings() {
        return settings;
    }
}
