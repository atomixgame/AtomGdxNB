package com.atomgdx.theme.timeline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for inspecting and editing Timeline Events (parameters, audio cues, triggers).
 */
public class EventEditorDialog extends JDialog {

    private final TimelineEvent event;
    private boolean isConfirmed = false;

    private final JTextField nameField = new JTextField();
    private final JSpinner timeSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 60.0, 0.05));
    private final JComboBox<TimelineEvent.PayloadType> typeCombo = new JComboBox<>(TimelineEvent.PayloadType.values());
    private final JTextField paramField = new JTextField();
    private final JTextField audioField = new JTextField();

    public EventEditorDialog(Frame parent, TimelineEvent event) {
        super(parent, "Timeline Event Inspector", true);
        this.event = event;

        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(28, 30, 34));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setOpaque(false);

        form.add(createLabel("Event Name:"));
        nameField.setText(event.eventName);
        form.add(nameField);

        form.add(createLabel("Timestamp (sec):"));
        timeSpinner.setValue((double) event.timeSec);
        form.add(timeSpinner);

        form.add(createLabel("Payload Type:"));
        typeCombo.setSelectedItem(event.payloadType);
        form.add(typeCombo);

        form.add(createLabel("Parameter Value:"));
        paramField.setText(event.stringParam);
        form.add(paramField);

        form.add(createLabel("Audio Cue SFX:"));
        audioField.setText(event.audioClipPath);
        form.add(audioField);

        add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setOpaque(false);

        JButton okBtn = new JButton("Save Event");
        okBtn.setBackground(new Color(44, 93, 212));
        okBtn.setForeground(Color.WHITE);
        okBtn.addActionListener(e -> {
            event.eventName = nameField.getText().trim();
            event.timeSec = ((Double) timeSpinner.getValue()).floatValue();
            event.payloadType = (TimelineEvent.PayloadType) typeCombo.getSelectedItem();
            event.stringParam = paramField.getText().trim();
            event.audioClipPath = audioField.getText().trim();
            isConfirmed = true;
            dispose();
        });
        btnPanel.add(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 260);
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() { return isConfirmed; }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }
}
