package com.focuswall.strategies;

import com.focuswall.challenges.MathChallenge;
import com.focuswall.ui.MenuWindow;
import com.focuswall.ui.SessionTimer;
import com.focuswall.util.BreakLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LimitStrategy implements InterventionStrategy {

    private final double initialMinutes;
    private double lastDuration;
    private int extensionCount = 0;
    private double totalExtendedMinutes = 0;
    private final List<JDialog> distractionWindows = new ArrayList<>();
    private JDialog controlDialog;

    public LimitStrategy(double initialMinutes) {
        this.initialMinutes = initialMinutes;
        this.lastDuration = initialMinutes;
    }

    @Override
    public void execute() {
        BreakLogger.log("LIMIT INTERVENTION: Triggered (Extension #" + extensionCount + ")");
        spawnDistractionWall();
        showControlDialog();
    }

    private void spawnDistractionWall() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Random rand = new Random();

        // Create many small pop-ups to be annoying
        for (int i = 0; i < 25; i++) {
            JDialog d = new JDialog();
            d.setUndecorated(true);
            d.setAlwaysOnTop(true);

            int width = 250 + rand.nextInt(200);
            int height = 100 + rand.nextInt(100);
            d.setSize(width, height);

            int x = rand.nextInt(screenSize.width - width);
            int y = rand.nextInt(screenSize.height - height);
            d.setLocation(x, y);

            JPanel p = new JPanel(new GridBagLayout());
            // Random vibrant pastel colors
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            p.setBackground(new Color(r, g, b).brighter());
            p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            JLabel lbl = new JLabel("<html><center>SCROLLING?<br>STOP!</center></html>");
            lbl.setFont(new Font("Impact", Font.BOLD, 24));
            p.add(lbl);

            d.add(p);
            d.setVisible(true);
            distractionWindows.add(d);
        }
    }

    private void showControlDialog() {
        controlDialog = new JDialog();
        controlDialog.setTitle("Control YOURSELF!!!");
        controlDialog.setModal(true);
        controlDialog.setAlwaysOnTop(true);
        controlDialog.setSize(450, 380);
        controlDialog.setLocationRelativeTo(null);
        controlDialog.setLayout(new BorderLayout());
        controlDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        // Wrap Icon & Title in Panels for Guaranteed Centering
        // FlowLayout.CENTER is the most effective way to force components to the center

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconPanel.add(iconLabel);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("YOUR TIMES IS UP!!!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.RED);
        titlePanel.add(titleLabel);

        mainPanel.add(iconPanel);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Math Logic
        MathChallenge challenge = new MathChallenge(extensionCount);
        JPanel challengePanel = new JPanel(new FlowLayout());
        challengePanel.setBackground(new Color(245, 245, 245));
        challengePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel qLabel = new JLabel("Answer to STOP: " + challenge.getQuestion());
        qLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        challengePanel.add(qLabel);

        mainPanel.add(challengePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- INPUT FIELD ---
        JTextField answerField = new JTextField();
        answerField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setPreferredSize(new Dimension(200, 45));
        answerField.setMaximumSize(new Dimension(200, 45));
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        mainPanel.add(answerField);
        controlDialog.add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBorder(new EmptyBorder(0, 25, 25, 25));
        btnPanel.setBackground(Color.WHITE);

        JButton stopBtn = new JButton("STOP");
        stopBtn.setEnabled(false);
        stopBtn.setBackground(new Color(220, 220, 220));
        stopBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton extendBtn = new JButton("EXTEND");
        extendBtn.setBackground(new Color(50, 50, 50));
        extendBtn.setForeground(Color.WHITE);
        extendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Logic check answer (Visual Feedback)
        answerField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { check(); }
            public void removeUpdate(DocumentEvent e) { check(); }
            public void changedUpdate(DocumentEvent e) { check(); }
            void check() {
                if (challenge.checkAnswer(answerField.getText())) {
                    stopBtn.setEnabled(true);
                    stopBtn.setBackground(new Color(220, 50, 50)); // Red active
                    stopBtn.setForeground(Color.WHITE);
                    answerField.setBackground(new Color(200, 255, 200)); // Green tint
                } else {
                    stopBtn.setEnabled(false);
                    stopBtn.setBackground(new Color(220, 220, 220));
                    stopBtn.setForeground(Color.BLACK);
                    answerField.setBackground(Color.WHITE);
                }
            }
        });

        // Stronger Enter Key Support
        // Check answer immediately when Enter is pressed, regardless of button state
        answerField.addActionListener(e -> {
            if (challenge.checkAnswer(answerField.getText())) {
                stopBtn.setEnabled(true); // Ensure active
                stopBtn.doClick();       // Click the button
            }
        });

        stopBtn.addActionListener(e -> {
            cleanup();
            BreakLogger.log("LIMIT INTERVENTION: Stopped by user.");
            SwingUtilities.invokeLater(() -> new MenuWindow().setVisible(true));
        });

        extendBtn.addActionListener(e -> handleExtension());

        btnPanel.add(stopBtn);
        btnPanel.add(extendBtn);
        controlDialog.add(btnPanel, BorderLayout.SOUTH);

        controlDialog.setVisible(true);
    }

    private void handleExtension() {
        double multiplier = 0.2;
        if (extensionCount >= 2) multiplier = 0.5;
        if (extensionCount >= 4) multiplier = 0.2;

        double rawExtension = lastDuration * multiplier;
        double addedMinutes = Math.ceil(rawExtension * 10) / 10.0;
        if (addedMinutes < 0.1) addedMinutes = 0.1;

        if (totalExtendedMinutes + addedMinutes > (initialMinutes * 2.0)) {
            JOptionPane.showMessageDialog(controlDialog, "MAX EXTENSION REACHED! Time to rest.");
            return;
        }

        extensionCount++;
        totalExtendedMinutes += addedMinutes;
        lastDuration = addedMinutes;

        cleanup();
        SessionTimer nextSession = new SessionTimer(addedMinutes, this, true);
        nextSession.setVisible(true);
    }

    private void cleanup() {
        for (JDialog d : distractionWindows) {
            d.dispose();
        }
        distractionWindows.clear();
        if (controlDialog != null) {
            controlDialog.dispose();
        }
    }
}