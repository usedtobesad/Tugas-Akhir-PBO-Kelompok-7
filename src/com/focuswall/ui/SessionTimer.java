package com.focuswall.ui;

import com.focuswall.strategies.InterventionStrategy;
import com.focuswall.util.BreakLogger;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SessionTimer extends JFrame {

    private final JLabel timeLabel;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    private final Timer timer;
    private int remainingSeconds;
    private final InterventionStrategy strategy;

    private final Color TIMER_BG = new Color(20, 20, 20);
    private final Color TEXT_COLOR = new Color(0, 255, 127); // Cyber Green

    public SessionTimer(double minutes, InterventionStrategy strategy, boolean isExtension) {
        int totalSeconds = (int) (minutes * 60);
        this.remainingSeconds = totalSeconds;
        this.strategy = strategy;

        // Logging logic
        String type = isExtension ? "EXTENSION" : "SESSION START";
        BreakLogger.logTimeChunk(minutes, type);

        // UI Setup
        setTitle("Focusing...");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(TIMER_BG);

        // Status Header
        statusLabel = new JLabel(isExtension ? "EXTENSION MODE" : "STAY FOCUSED", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        add(statusLabel, BorderLayout.NORTH);

        // Main Timer Digits
        timeLabel = new JLabel(formatTime(remainingSeconds), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 70));
        timeLabel.setForeground(TEXT_COLOR);
        add(timeLabel, BorderLayout.CENTER);

        // Progress Bar
        progressBar = new JProgressBar(0, totalSeconds);
        progressBar.setValue(totalSeconds);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(0, 153, 115));
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setPreferredSize(new Dimension(getWidth(), 10));
        progressBar.setBorderPainted(false);
        add(progressBar, BorderLayout.SOUTH);

        // Logic Loop
        timer = new Timer(1000, e -> tick());
        timer.start();
    }

    private void tick() {
        remainingSeconds--;
        timeLabel.setText(formatTime(remainingSeconds));
        progressBar.setValue(remainingSeconds);

        // Change UI color to red when time is running out (<= 10 seconds)
        if (remainingSeconds <= 10) {
            timeLabel.setForeground(new Color(255, 80, 80));
            statusLabel.setText("Almost done...");
        }

        if (remainingSeconds <= 0) {
            timer.stop();
            finish();
        }
    }

    private void finish() {
        this.setVisible(false);
        BreakLogger.log("SESSION: Timer expired.");
        strategy.execute();
        this.dispose();
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}