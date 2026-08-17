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
                String selected = styleList.getSelectedValue();
                if (selected != null) {
                    inspectStyle(selected);
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
        styleList.setSelectedIndex(0);
    }

    private void populateStyles() {
        styleListModel.clear();
        styleListModel.addElement("TextButton: default");
        styleListModel.addElement("TextButton: toggle");
        styleListModel.addElement("TextButton: cyber-cyan");
        styleListModel.addElement("Label: default");
        styleListModel.addElement("Label: title-large");
        styleListModel.addElement("Window: default");
        styleListModel.addElement("Window: dialog");
        styleListModel.addElement("ScrollPane: default");
        styleListModel.addElement("ProgressBar: default-horizontal");
        styleListModel.addElement("Slider: default-horizontal");
        styleListModel.addElement("SelectBox: default");
        styleListModel.addElement("Touchpad: default");
    }

    private void inspectStyle(String styleName) {
        styleHeaderLabel.setText("Style: " + styleName);
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

        if (styleName.startsWith("TextButton")) {
            addPropertyField(propsBox, "up (Drawable)", "button-up");
            addPropertyField(propsBox, "down (Drawable)", "button-down");
            addPropertyField(propsBox, "over (Drawable)", "button-over");
            addPropertyField(propsBox, "font (BitmapFont)", "default-font");
            addPropertyField(propsBox, "fontColor (Color)", "white");
            addPropertyField(propsBox, "downFontColor (Color)", "neon-cyan");
        } else if (styleName.startsWith("Label")) {
            addPropertyField(propsBox, "font (BitmapFont)", "default-font");
            addPropertyField(propsBox, "fontColor (Color)", "white");
            addPropertyField(propsBox, "background (Drawable)", "optional");
        } else if (styleName.startsWith("Window")) {
            addPropertyField(propsBox, "background (Drawable)", "window-bg");
            addPropertyField(propsBox, "titleFont (BitmapFont)", "title-font");
            addPropertyField(propsBox, "titleFontColor (Color)", "neon-cyan");
            addPropertyField(propsBox, "stageBackground (Drawable)", "dim-bg");
        } else {
            addPropertyField(propsBox, "background (Drawable)", "default-bg");
            addPropertyField(propsBox, "knob (Drawable)", "slider-knob");
            addPropertyField(propsBox, "font (BitmapFont)", "default-font");
        }

        propertiesPanel.add(propsBox);
        propertiesPanel.add(Box.createVerticalGlue());
        propertiesPanel.revalidate();
        propertiesPanel.repaint();
    }

    private void addPropertyField(JPanel panel, String label, String val) {
        JLabel lbl = new JLabel(" " + label + ":");
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
