package com.atomgdx.theme.wizard;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.core.project.PlatformType;
import com.atomgdx.liftoff.LiftoffProjectDescriptor;
import com.atomgdx.liftoff.LiftoffTemplateEngine;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

/**
 * NetBeans-compliant New Project Wizard for multi-platform LibGDX game applications.
 */
public class LibGdxNewProjectWizard {

    public static void showWizard() {
        JDialog dialog = new JDialog((Frame) null, "New LibGDX Multi-Platform Project Wizard", true);
        dialog.setSize(750, 580);
        dialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(56, 189, 248), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLabel = new JLabel("Create New LibGDX Game Project");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(56, 189, 248));
        JLabel descLabel = new JLabel("Configure project metadata, target deployment platforms, and official LibGDX extensions.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(148, 163, 184));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content: Metadata + Platforms + Extensions
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setOpaque(false);

        // Left Column: Metadata
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JPanel metaBox = new JPanel(new GridBagLayout());
        metaBox.setBackground(new Color(30, 41, 59));
        metaBox.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(71, 85, 105)), " Project Metadata ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(148, 163, 184)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField nameField = createField("MyGdxGame");
        JTextField pkgField = createField("com.mygame");
        JTextField mainField = createField("MainGame");
        JTextField locField = createField("G:\\GameDev\\LibGDX\\AtomGdx\\AtomGdxNB\\Workspace");
        JTextField gdxVerField = createField("1.13.1");

        addFormRow(metaBox, gbc, 0, "Project Name:", nameField);
        addFormRow(metaBox, gbc, 1, "Package ID:", pkgField);
        addFormRow(metaBox, gbc, 2, "Main Class:", mainField);
        addFormRow(metaBox, gbc, 3, "Location:", locField);
        addFormRow(metaBox, gbc, 4, "LibGDX Ver:", gdxVerField);

        leftPanel.add(metaBox);
        leftPanel.add(Box.createVerticalStrut(10));

        // Target Platforms
        JPanel platformBox = new JPanel(new GridLayout(2, 2, 8, 8));
        platformBox.setBackground(new Color(30, 41, 59));
        platformBox.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(71, 85, 105)), " Target Platforms ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(148, 163, 184)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JCheckBox coreCb = createCheckBox("Core Module (Required)", true, false);
        JCheckBox desktopCb = createCheckBox("Desktop (LWJGL3)", true, true);
        JCheckBox androidCb = createCheckBox("Android", true, true);
        JCheckBox htmlCb = createCheckBox("HTML5 / Web (TeaVM)", true, true);

        platformBox.add(coreCb);
        platformBox.add(desktopCb);
        platformBox.add(androidCb);
        platformBox.add(htmlCb);

        leftPanel.add(platformBox);
        contentPanel.add(leftPanel);

        // Right Column: Extensions & AI Features
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JPanel extBox = new JPanel(new GridLayout(6, 1, 6, 6));
        extBox.setBackground(new Color(30, 41, 59));
        extBox.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(71, 85, 105)), " LibGDX Extensions ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(148, 163, 184)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JCheckBox box2dCb = createCheckBox("Box2D (2D Physics Engine)", true, true);
        JCheckBox visuiCb = createCheckBox("VisUI (Modern Scene2D UI)", true, true);
        JCheckBox ashleyCb = createCheckBox("Ashley (Entity Component System)", true, true);
        JCheckBox freetypeCb = createCheckBox("FreeType (TTF Font Rasterizer)", true, true);
        JCheckBox aiCb = createCheckBox("GDX AI (Steering, Pathfinding, BT)", false, true);
        JCheckBox lightsCb = createCheckBox("Box2D-Lights (2D Dynamic Shadows)", false, true);

        extBox.add(box2dCb);
        extBox.add(visuiCb);
        extBox.add(ashleyCb);
        extBox.add(freetypeCb);
        extBox.add(aiCb);
        extBox.add(lightsCb);

        rightPanel.add(extBox);
        contentPanel.add(rightPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Bottom Button Bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonBar.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(51, 65, 85));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton finishBtn = new JButton("Generate Project");
        finishBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        finishBtn.setBackground(new Color(14, 165, 233));
        finishBtn.setForeground(Color.WHITE);
        finishBtn.setFocusPainted(false);
        finishBtn.addActionListener(e -> {
            try {
                String pName = nameField.getText().trim();
                String pPkg = pkgField.getText().trim();
                String pMain = mainField.getText().trim();
                String pLoc = locField.getText().trim();
                String gdxVer = gdxVerField.getText().trim();

                if (pName.isEmpty() || pPkg.isEmpty() || pMain.isEmpty() || pLoc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all project metadata fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LiftoffProjectDescriptor descriptor = new LiftoffProjectDescriptor();
                descriptor.setProjectName(pName);
                descriptor.setPackageName(pPkg);
                descriptor.setMainClassName(pMain);
                descriptor.setDestinationDir(new File(pLoc));
                descriptor.setGdxVersion(gdxVer);

                descriptor.getPlatforms().add(PlatformType.CORE);
                if (desktopCb.isSelected()) descriptor.getPlatforms().add(PlatformType.DESKTOP_LWJGL3);
                if (androidCb.isSelected()) descriptor.getPlatforms().add(PlatformType.ANDROID);
                if (htmlCb.isSelected()) descriptor.getPlatforms().add(PlatformType.WEB_TEAVM);

                if (box2dCb.isSelected()) descriptor.getExtensions().add(ExtensionType.BOX2D);
                if (visuiCb.isSelected()) descriptor.getExtensions().add(ExtensionType.VIS_UI);
                if (ashleyCb.isSelected()) descriptor.getExtensions().add(ExtensionType.ASHLEY);
                if (freetypeCb.isSelected()) descriptor.getExtensions().add(ExtensionType.FREETYPE);
                if (aiCb.isSelected()) descriptor.getExtensions().add(ExtensionType.GDX_AI);
                if (lightsCb.isSelected()) descriptor.getExtensions().add(ExtensionType.BOX2DLIGHTS);

                LibGdxProject generated = LiftoffTemplateEngine.generateProject(descriptor);
                dialog.dispose();

                NotifyDescriptor notify = new NotifyDescriptor.Message(
                        "LibGDX Project '" + pName + "' generated successfully at:\n" + generated.getRootDirectory().getAbsolutePath(),
                        NotifyDescriptor.INFORMATION_MESSAGE
                );
                DialogDisplayer.getDefault().notify(notify);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to generate project: " + ex.getMessage(), "Generation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonBar.add(cancelBtn);
        buttonBar.add(finishBtn);
        mainPanel.add(buttonBar, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private static JTextField createField(String text) {
        JTextField field = new JTextField(text);
        field.setBackground(new Color(15, 23, 42));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(56, 189, 248));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85)),
                new EmptyBorder(4, 6, 4, 6)
        ));
        return field;
    }

    private static JCheckBox createCheckBox(String text, boolean selected, boolean enabled) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setOpaque(false);
        cb.setForeground(new Color(226, 232, 240));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cb.setEnabled(enabled);
        return cb;
    }

    private static void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel l = new JLabel(label);
        l.setForeground(new Color(148, 163, 184));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
}
