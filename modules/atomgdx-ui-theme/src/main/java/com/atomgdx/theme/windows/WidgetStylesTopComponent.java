package com.atomgdx.theme.windows;

import com.atomgdx.editor.skin.SkinModel;
import com.atomgdx.editor.skin.WidgetStyle;
import com.atomgdx.theme.SciFiColors;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

/**
 * NetBeans TopComponent for Scene2D / VisUI Skin Classes & Widget Styles manager.
 * Docks in "explorer" or "properties".
 */
public class WidgetStylesTopComponent extends TopComponent {

    private final SkinModel skinModel = new SkinModel("NeonCosmosSkin");
    private final DefaultListModel<String> styleListModel = new DefaultListModel<>();
    private final JList<String> styleList = new JList<>(styleListModel);
    private final JPanel propertiesPanel = new JPanel();
    private final JLabel styleHeaderLabel = new JLabel("Select Style");

    public WidgetStylesTopComponent() {
        setName("Widget Styles");
        setToolTipText("LibGDX Scene2D / VisUI Widget Styles & Skin Classes");
        setLayout(new BorderLayout());
        setBackground(SciFiColors.BG_DARKEST);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SciFiColors.BG_PANEL);
        header.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel title = new JLabel("Skin Classes & Styles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(SciFiColors.ACCENT_CYAN);
        header.add(title, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerActions.setOpaque(false);
        JButton addStyleBtn = new JButton("+ Add");
        addStyleBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter new Style Name:", "custom");
            if (name != null && !name.isBlank()) {
                WidgetStyle ws = new WidgetStyle("com.badlogic.gdx.scenes.scene2d.ui.TextButton$TextButtonStyle", name.trim());
                ws.setProperty("up", "button-up");
                ws.setProperty("down", "button-down");
                ws.setProperty("font", "default-font");
                ws.setProperty("fontColor", "white");
                skinModel.addStyle(ws);
                populateStyles();
                styleList.setSelectedIndex(skinModel.getStyles().size() - 1);
            }
        });
        JButton removeStyleBtn = new JButton("- Delete");
        removeStyleBtn.addActionListener(e -> {
            int idx = styleList.getSelectedIndex();
            if (idx >= 0 && skinModel.getStyles().size() > 1) {
                skinModel.removeStyle(skinModel.getStyles().get(idx));
                populateStyles();
                styleList.setSelectedIndex(Math.max(0, idx - 1));
            }
        });
        headerActions.add(addStyleBtn);
        headerActions.add(removeStyleBtn);
        header.add(headerActions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Split: Left List of Styles, Right Property Editor
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(180);
        splitPane.setBackground(SciFiColors.BG_DARKEST);
        splitPane.setBorder(null);

        // Styles List
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(SciFiColors.BG_PANEL);
        listPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

        styleList.setBackground(SciFiColors.BG_DARK);
        styleList.setForeground(SciFiColors.TEXT_PRIMARY);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleList.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        populateStyles();

        styleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = styleList.getSelectedIndex();
                if (idx >= 0 && idx < skinModel.getStyles().size()) {
                    inspectStyle(skinModel.getStyles().get(idx));
                }
            }
        });

        listPanel.add(new JScrollPane(styleList), BorderLayout.CENTER);
        splitPane.setLeftComponent(listPanel);

        // Properties Editor Panel
        JPanel propContainer = new JPanel(new BorderLayout());
        propContainer.setBackground(SciFiColors.BG_DARKEST);

        JPanel propHeader = new JPanel(new BorderLayout());
        propHeader.setBackground(SciFiColors.BG_DARK);
        propHeader.setBorder(new EmptyBorder(4, 8, 4, 8));
        styleHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        styleHeaderLabel.setForeground(SciFiColors.ACCENT_CYAN);
        propHeader.add(styleHeaderLabel, BorderLayout.WEST);
        propContainer.add(propHeader, BorderLayout.NORTH);

        propertiesPanel.setLayout(new BoxLayout(propertiesPanel, BoxLayout.Y_AXIS));
        propertiesPanel.setBackground(SciFiColors.BG_DARKEST);
        propertiesPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane propScroll = new JScrollPane(propertiesPanel);
        propScroll.setBorder(null);
        propScroll.getViewport().setBackground(SciFiColors.BG_DARKEST);
        propContainer.add(propScroll, BorderLayout.CENTER);

        splitPane.setRightComponent(propContainer);
        add(splitPane, BorderLayout.CENTER);

        // Initial selection
        if (!skinModel.getStyles().isEmpty()) {
            styleList.setSelectedIndex(0);
        }
    }

    private void populateStyles() {
        styleListModel.clear();
        for (WidgetStyle s : skinModel.getStyles()) {
            String shortType = s.getWidgetType();
            int dot = shortType.lastIndexOf('.');
            if (dot != -1) shortType = shortType.substring(dot + 1).replace("$", ".");
            styleListModel.addElement(shortType + ": " + s.getStyleName());
        }
    }

    private void inspectStyle(WidgetStyle style) {
        styleHeaderLabel.setText("Style: " + style.getStyleName() + " (" + style.getWidgetType() + ")");
        propertiesPanel.removeAll();

        JPanel propsBox = new JPanel(new GridLayout(0, 2, 6, 4));
        propsBox.setBackground(SciFiColors.BG_CARD);
        propsBox.setMaximumSize(new Dimension(Short.MAX_VALUE, propsBox.getPreferredSize().height));
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                "Style Properties"
        );
        border.setTitleColor(SciFiColors.TEXT_PRIMARY);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        propsBox.setBorder(border);

        for (Map.Entry<String, String> entry : style.getProperties().entrySet()) {
            addPropertyField(propsBox, style, entry.getKey(), entry.getValue());
        }

        propertiesPanel.add(propsBox);
        propertiesPanel.add(Box.createVerticalGlue());
        propertiesPanel.revalidate();
        propertiesPanel.repaint();
    }

    private void addPropertyField(JPanel panel, WidgetStyle style, String key, String val) {
        JLabel lbl = new JLabel(" " + key + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(SciFiColors.TEXT_SECONDARY);

        JTextField txt = new JTextField(val);
        txt.setPreferredSize(new Dimension(80, 24));
        txt.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
        txt.setBackground(SciFiColors.BG_DARK);
        txt.setForeground(SciFiColors.TEXT_PRIMARY);
        txt.setCaretColor(SciFiColors.ACCENT_CYAN);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SciFiColors.BORDER_SUBTLE, 1),
                new EmptyBorder(2, 4, 2, 4)
        ));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        txt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { style.setProperty(key, txt.getText().trim()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { style.setProperty(key, txt.getText().trim()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { style.setProperty(key, txt.getText().trim()); }
        });

        panel.add(lbl);
        panel.add(txt);
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return "WidgetStylesTopComponent";
    }
}
