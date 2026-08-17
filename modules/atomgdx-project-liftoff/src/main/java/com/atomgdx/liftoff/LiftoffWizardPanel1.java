package com.atomgdx.liftoff;

import com.atomgdx.core.project.ExtensionType;
import com.atomgdx.core.project.PlatformType;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * Swing wizard panel for configuring a new LibGDX Liftoff project.
 */
public class LiftoffWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    private final LiftoffProjectDescriptor descriptor = new LiftoffProjectDescriptor();
    private JPanel component;

    private JTextField projectNameField;
    private JTextField packageField;
    private JTextField mainClassField;
    private JTextField destinationDirField;
    private JComboBox<String> gdxVersionCombo;
    private JComboBox<String> javaVersionCombo;

    private final Map<PlatformType, JCheckBox> platformBoxes = new EnumMap<>(PlatformType.class);
    private final Map<ExtensionType, JCheckBox> extensionBoxes = new EnumMap<>(ExtensionType.class);

    private WizardDescriptor wizard;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = createComponent();
        }
        return component;
    }

    private JPanel createComponent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Project Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Project Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        projectNameField = new JTextField(descriptor.getProjectName(), 20);
        form.add(projectNameField, gbc);

        // Package Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Package Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        packageField = new JTextField(descriptor.getPackageName(), 20);
        form.add(packageField, gbc);

        // Main Class
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(new JLabel("Main Class:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        mainClassField = new JTextField(descriptor.getMainClassName(), 20);
        form.add(mainClassField, gbc);

        // Destination Dir
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(new JLabel("Destination Folder:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        String userHome = System.getProperty("user.home");
        destinationDirField = new JTextField(new File(userHome, "LibGDXProjects").getAbsolutePath(), 20);
        form.add(destinationDirField, gbc);

        // GDX & Java Versions
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        versionPanel.add(new JLabel("LibGDX:"));
        gdxVersionCombo = new JComboBox<>(new String[]{"1.13.1", "1.12.1", "1.11.0"});
        versionPanel.add(gdxVersionCombo);
        versionPanel.add(new JLabel("Java:"));
        javaVersionCombo = new JComboBox<>(new String[]{"21", "17", "11"});
        versionPanel.add(javaVersionCombo);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(versionPanel, gbc);

        panel.add(form, BorderLayout.NORTH);

        // Tabs for Platforms & Extensions
        JTabbedPane tabs = new JTabbedPane();

        // Platforms Panel
        JPanel platformsPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        platformsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        for (PlatformType platform : PlatformType.values()) {
            JCheckBox box = new JCheckBox(platform.getDisplayName());
            if (platform == PlatformType.CORE || platform == PlatformType.DESKTOP_LWJGL3) {
                box.setSelected(true);
            }
            if (platform == PlatformType.CORE) {
                box.setEnabled(false); // Core is mandatory
            }
            platformBoxes.put(platform, box);
            platformsPanel.add(box);
        }
        tabs.addTab("Platforms", platformsPanel);

        // Extensions Panel
        JPanel extensionsPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        extensionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        for (ExtensionType ext : ExtensionType.values()) {
            JCheckBox box = new JCheckBox(ext.getDisplayName());
            extensionBoxes.put(ext, box);
            extensionsPanel.add(box);
        }
        tabs.addTab("Official & Third-Party Extensions", new JScrollPane(extensionsPanel));

        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    public LiftoffProjectDescriptor getDescriptor() {
        descriptor.setProjectName(projectNameField.getText().trim());
        descriptor.setPackageName(packageField.getText().trim());
        descriptor.setMainClassName(mainClassField.getText().trim());
        descriptor.setDestinationDir(new File(destinationDirField.getText().trim()));
        descriptor.setGdxVersion((String) gdxVersionCombo.getSelectedItem());
        descriptor.setJavaVersion((String) javaVersionCombo.getSelectedItem());

        descriptor.getPlatforms().clear();
        for (Map.Entry<PlatformType, JCheckBox> entry : platformBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                descriptor.getPlatforms().add(entry.getKey());
            }
        }

        descriptor.getExtensions().clear();
        for (Map.Entry<ExtensionType, JCheckBox> entry : extensionBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                descriptor.getExtensions().add(entry.getKey());
            }
        }

        return descriptor;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        this.wizard = settings;
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        settings.putProperty("projectDescriptor", getDescriptor());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void validate() throws WizardValidationException {
        if (projectNameField.getText().trim().isEmpty()) {
            throw new WizardValidationException(projectNameField, "Project name must not be empty.", null);
        }
        if (packageField.getText().trim().isEmpty()) {
            throw new WizardValidationException(packageField, "Package name must not be empty.", null);
        }
        if (mainClassField.getText().trim().isEmpty()) {
            throw new WizardValidationException(mainClassField, "Main class name must not be empty.", null);
        }
    }
}
