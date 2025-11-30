package com.focuswall.strategies;

import com.focuswall.ui.MenuWindow;
import com.focuswall.util.BreakLogger;

import java.awt.*;
import javax.swing.*;

public class FocusStrategy implements InterventionStrategy {

    private JFrame breakFrame;
    private JLabel mainLabel;
    private JLabel subLabel;
    private Timer breakTimer;
    private int secondsLeft = 15; // Example: 15 seconds

    @Override
    public void execute() {
        BreakLogger.log("FOCUS INTERVENTION: Started");

        // Fullscreen Setup
        breakFrame = new JFrame();
        breakFrame.setUndecorated(true);
        breakFrame.setAlwaysOnTop(true);
        breakFrame.getContentPane().setBackground(Color.BLACK);
        breakFrame.setLayout(new GridBagLayout()); // Center elements
        breakFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Graphics Device handling for true fullscreen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(breakFrame);
        } else {
            breakFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            breakFrame.setVisible(true);
        }

        // Layout Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.BLACK);

        mainLabel = new JLabel("INHALE", SwingConstants.CENTER);
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        mainLabel.setForeground(Color.WHITE);
        mainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subLabel = new JLabel(String.valueOf(secondsLeft), SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 100));
        subLabel.setForeground(new Color(100, 255, 218)); // Soft Cyan
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(mainLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(subLabel);

        breakFrame.add(contentPanel);

        startBreakTimer();
    }

    private void startBreakTimer() {
        breakTimer = new Timer(1000, e -> {
            secondsLeft--;
            subLabel.setText(String.valueOf(secondsLeft));

            // Simple text animation
            if (secondsLeft % 4 == 0) mainLabel.setText("INHALE");
            else if (secondsLeft % 4 == 2) mainLabel.setText("EXHALE");

            if (secondsLeft <= 0) {
                finishBreak();
            }
        });
        breakTimer.start();
    }

    private void finishBreak() {
        breakTimer.stop();
        breakFrame.dispose();
        BreakLogger.log("FOCUS INTERVENTION: Completed");
        SwingUtilities.invokeLater(() -> new MenuWindow().setVisible(true));
    }
}