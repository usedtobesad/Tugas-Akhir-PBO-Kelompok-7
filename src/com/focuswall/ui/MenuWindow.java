package com.focuswall.ui;

import com.focuswall.strategies.FocusStrategy;
import com.focuswall.strategies.InterventionStrategy;
import com.focuswall.strategies.LimitStrategy;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuWindow extends JFrame {

    private final JTextField minutesField;
    private final JRadioButton focusModeBtn;
    private final JRadioButton limitModeBtn;
    private final JButton startButton;

    // UI colors
    private final Color PRIMARY_COLOR = new Color(33, 33, 33);    // Dark Gray
    private final Color ACCENT_COLOR = new Color(0, 153, 115);    // Emerald Green
    private final Color BG_COLOR = new Color(245, 245, 245);      // Off White

    public MenuWindow() {
        setTitle("FocusWall");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("FOCUSWALL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // --- CONTENT ---
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Input Timer
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(Color.WHITE);

        JLabel durationLabel = new JLabel("Focus Duration (minutes):");
        durationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        minutesField = new JTextField("25", 5);
        minutesField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        minutesField.setHorizontalAlignment(JTextField.CENTER);
        minutesField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        inputPanel.add(durationLabel);
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(minutesField);

        centerPanel.add(inputPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // 2. Mode Selection
        JPanel modePanel = new JPanel();
        modePanel.setBackground(Color.WHITE);
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        modePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Choose Mode Intervention"));

        ButtonGroup modeGroup = new ButtonGroup();
        focusModeBtn = createStyledRadio("[1] Focus Mode", true);
        limitModeBtn = createStyledRadio("[2] Limit Mode", false);

        modeGroup.add(focusModeBtn);
        modeGroup.add(limitModeBtn);

        // Alignment X Center to center components in the panel
        focusModeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        limitModeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        modePanel.add(Box.createVerticalStrut(10)); // Top spacer
        modePanel.add(focusModeBtn);
        modePanel.add(Box.createVerticalStrut(10));
        modePanel.add(limitModeBtn);
        modePanel.add(Box.createVerticalStrut(10)); // Bottom spacer

        centerPanel.add(modePanel);
        add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER / BUTTON ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        startButton = new JButton("Starting session");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startButton.setBackground(ACCENT_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(new EmptyBorder(12, 0, 12, 0));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> startTimer());

        bottomPanel.add(startButton, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- KEYBOARD SHORTCUTS & INTERACTIONS ---
        setupShortcuts();
    }

    private void setupShortcuts() {
        // 1. Enter Key Support
        // Set Start button as default (pressed when Enter is hit anywhere)
        this.getRootPane().setDefaultButton(startButton);

        // Specific listener for text field to ensure Enter works while typing
        minutesField.addActionListener(e -> startTimer());

        // 2. Number Keys (1 & 2) Support
        // Using InputMap and ActionMap on RootPane
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        // Map Number '1' -> Select Focus Mode
        inputMap.put(KeyStroke.getKeyStroke('1'), "selectFocus");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), "selectFocus");

        actionMap.put("selectFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only change mode if user is NOT typing in the minutes field
                // Allows user to type numbers like "15" without switching modes
                if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != minutesField) {
                    focusModeBtn.setSelected(true);
                    focusModeBtn.requestFocusInWindow(); // Move visual focus to button
                }
            }
        });

        // Map Number '2' -> Select Limit Mode
        inputMap.put(KeyStroke.getKeyStroke('2'), "selectLimit");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), "selectLimit");

        actionMap.put("selectLimit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only change mode if user is NOT typing in the minutes field
                if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != minutesField) {
                    limitModeBtn.setSelected(true);
                    limitModeBtn.requestFocusInWindow();
                }
            }
        });

        // Allow clicking empty space to remove focus from text field, enabling shortcuts
        this.getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startButton.requestFocusInWindow();
            }
        });
    }

    private JRadioButton createStyledRadio(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text);
        rb.setSelected(selected);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rb.setBackground(Color.WHITE);
        rb.setFocusPainted(false);
        return rb;
    }

    private void startTimer() {
        String input = minutesField.getText();
        try {
            double minutes = Double.parseDouble(input);
            if (minutes <= 0) throw new NumberFormatException();

            InterventionStrategy selectedStrategy;
            if (focusModeBtn.isSelected()) {
                selectedStrategy = new FocusStrategy();
            } else {
                selectedStrategy = new LimitStrategy(minutes);
            }

            SessionTimer session = new SessionTimer(minutes, selectedStrategy, false);
            session.setVisible(true);
            this.setVisible(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Put a valid time!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}