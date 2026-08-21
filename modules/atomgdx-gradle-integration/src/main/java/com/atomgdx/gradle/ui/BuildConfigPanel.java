package com.atomgdx.gradle.ui;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.gradle.GradleRunner;
import com.atomgdx.gradle.GradleTask;
import com.atomgdx.gradle.config.BuildConfigModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Visual Editor for Multi-Platform Build Configurations and execution target matrix in AtomGDX.
 */
public class BuildConfigPanel extends JPanel {

    private final BuildConfigModel model;
    private final DefaultListModel<BuildConfigModel.Profile> listModel = new DefaultListModel<>();
    private final JList<BuildConfigModel.Profile> profileList = new JList<>(listModel);

    // Form fields
    private final JTextField nameField = new JTextField();
    private final JComboBox<BuildConfigModel.Platform> platformCombo = new JComboBox<>(BuildConfigModel.Platform.values());
    private final JTextField tasksField = new JTextField();
    private final JTextField jvmArgsField = new JTextField();
    private final JTextField gradleFlagsField = new JTextField();
    private final JCheckBox debugCheckBox = new JCheckBox("Enable Debug Mode", true);

    private final JTextArea consoleOutputArea = new JTextArea();
    private LibGdxProject currentProject;

    public BuildConfigPanel() {
        this(new BuildConfigModel());
    }

    public BuildConfigPanel(BuildConfigModel model) {
        this.model = model != null ? model : new BuildConfigModel();
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(22, 23, 26));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header toolbar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 32, 36));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Build Configurations & Target Matrix");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(0, 220, 255));
        header.add(title, BorderLayout.WEST);

        JButton runBtn = new JButton("Run Build Profile");
        runBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        runBtn.setBackground(new Color(44, 93, 212));
        runBtn.setForeground(Color.WHITE);
        runBtn.addActionListener(e -> executeSelectedProfile());
        header.add(runBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center split: Left profiles list, Right profile editor & output console
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(260);
        splitPane.setBackground(new Color(22, 23, 26));
        splitPane.setBorder(null);

        // Left List & Actions
        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.setBackground(new Color(30, 32, 36));
        leftPanel.setBorder(createTitledBorder("Build Profiles"));

        refreshListModel();
        profileList.setBackground(new Color(18, 19, 22));
        profileList.setForeground(Color.WHITE);
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedProfileIntoForm();
        });
        leftPanel.add(new JScrollPane(profileList), BorderLayout.CENTER);

        JPanel listActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        listActions.setOpaque(false);

        JButton addBtn = new JButton("+ Add");
        addBtn.addActionListener(e -> {
            BuildConfigModel.Profile p = new BuildConfigModel.Profile("New Profile", BuildConfigModel.Platform.DESKTOP_LWJGL3, "lwjgl3:run", "", "", true);
            this.model.addProfile(p);
            refreshListModel();
            profileList.setSelectedIndex(this.model.getProfiles().size() - 1);
        });
        listActions.add(addBtn);

        JButton deleteBtn = new JButton("- Delete");
        deleteBtn.addActionListener(e -> {
            int sel = profileList.getSelectedIndex();
            if (sel >= 0) {
                this.model.removeProfile(sel);
                refreshListModel();
            }
        });
        listActions.add(deleteBtn);
        leftPanel.add(listActions, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right Editor & Output Console
        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(new Color(22, 23, 26));

        // Form parameters
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 32, 36));
        formPanel.setBorder(createTitledBorder("Profile Settings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        formPanel.add(new JLabel("Configuration Name:"), setGbc(gbc, 0, 0, 1));
        nameField.addActionListener(e -> saveFormToSelectedProfile());
        formPanel.add(nameField, setGbc(gbc, 1, 0, 1));

        formPanel.add(new JLabel("Target Platform:"), setGbc(gbc, 0, 1, 1));
        platformCombo.addActionListener(e -> {
            BuildConfigModel.Platform p = (BuildConfigModel.Platform) platformCombo.getSelectedItem();
            if (p != null && tasksField.getText().isBlank()) {
                tasksField.setText(p.getDefaultTask());
            }
            saveFormToSelectedProfile();
        });
        formPanel.add(platformCombo, setGbc(gbc, 1, 1, 1));

        formPanel.add(new JLabel("Gradle Tasks:"), setGbc(gbc, 0, 2, 1));
        tasksField.addActionListener(e -> saveFormToSelectedProfile());
        formPanel.add(tasksField, setGbc(gbc, 1, 2, 1));

        formPanel.add(new JLabel("JVM Arguments:"), setGbc(gbc, 0, 3, 1));
        jvmArgsField.addActionListener(e -> saveFormToSelectedProfile());
        formPanel.add(jvmArgsField, setGbc(gbc, 1, 3, 1));

        formPanel.add(new JLabel("Gradle Flags:"), setGbc(gbc, 0, 4, 1));
        gradleFlagsField.addActionListener(e -> saveFormToSelectedProfile());
        formPanel.add(gradleFlagsField, setGbc(gbc, 1, 4, 1));

        debugCheckBox.setOpaque(false);
        debugCheckBox.setForeground(Color.WHITE);
        debugCheckBox.addActionListener(e -> saveFormToSelectedProfile());
        formPanel.add(debugCheckBox, setGbc(gbc, 1, 5, 1));

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // Console output
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setBackground(new Color(18, 19, 22));
        consolePanel.setBorder(createTitledBorder("Build Output Console"));

        consoleOutputArea.setEditable(false);
        consoleOutputArea.setBackground(new Color(14, 15, 18));
        consoleOutputArea.setForeground(new Color(0, 255, 180));
        consoleOutputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        consoleOutputArea.setText("Ready to run build profiles.\n");
        consolePanel.add(new JScrollPane(consoleOutputArea), BorderLayout.CENTER);
        rightPanel.add(consolePanel, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        if (!model.getProfiles().isEmpty()) {
            profileList.setSelectedIndex(0);
            loadSelectedProfileIntoForm();
        }
    }

    public void setProject(LibGdxProject project) {
        this.currentProject = project;
    }

    private void refreshListModel() {
        listModel.clear();
        for (BuildConfigModel.Profile p : model.getProfiles()) {
            listModel.addElement(p);
        }
    }

    private void loadSelectedProfileIntoForm() {
        BuildConfigModel.Profile p = profileList.getSelectedValue();
        if (p == null) return;

        nameField.setText(p.getName());
        platformCombo.setSelectedItem(p.getPlatform());
        tasksField.setText(p.getTasks());
        jvmArgsField.setText(p.getJvmArgs());
        gradleFlagsField.setText(p.getGradleFlags());
        debugCheckBox.setSelected(p.isDebug());
    }

    private void saveFormToSelectedProfile() {
        BuildConfigModel.Profile p = profileList.getSelectedValue();
        if (p == null) return;

        p.setName(nameField.getText().trim());
        p.setPlatform((BuildConfigModel.Platform) platformCombo.getSelectedItem());
        p.setTasks(tasksField.getText().trim());
        p.setJvmArgs(jvmArgsField.getText().trim());
        p.setGradleFlags(gradleFlagsField.getText().trim());
        p.setDebug(debugCheckBox.isSelected());
        profileList.repaint();
    }

    private void executeSelectedProfile() {
        saveFormToSelectedProfile();
        BuildConfigModel.Profile p = profileList.getSelectedValue();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Please select a build profile first.", "No Profile Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        consoleOutputArea.setText(">>> Executing Profile: " + p.getName() + " [" + p.getTasks() + "] ...\n");

        File projectDir = currentProject != null ? currentProject.getRootDirectory() : new File("Workspace/NeonCosmos");
        if (!projectDir.exists()) {
            consoleOutputArea.append("Warning: Using local working directory: " + new File(".").getAbsolutePath() + "\n");
            projectDir = new File(".");
        }

        List<String> args = new ArrayList<>();
        if (!p.getGradleFlags().isBlank()) {
            args.addAll(Arrays.asList(p.getGradleFlags().split("\\s+")));
        }

        LibGdxProject execProj = currentProject != null ? currentProject : new LibGdxProject(projectDir.getName(), "com.neon.cosmos", "Main", projectDir);

        GradleRunner.executeTask(execProj, p.getTasks(), args, line -> {
            SwingUtilities.invokeLater(() -> {
                consoleOutputArea.append(line.getText() + "\n");
                consoleOutputArea.setCaretPosition(consoleOutputArea.getDocument().getLength());
            });
        }).thenAccept(exitCode -> {
            SwingUtilities.invokeLater(() -> {
                consoleOutputArea.append("\n>>> BUILD " + (exitCode == 0 ? "SUCCESSFUL" : "FAILED (Exit Code " + exitCode + ")") + "\n");
            });
        });
    }

    private GridBagConstraints setGbc(GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = width > 1 ? 1.0 : 0.0;
        return gbc;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 52, 58), 1),
                title
        );
        border.setTitleColor(new Color(220, 224, 230));
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 11));
        return border;
    }
}
