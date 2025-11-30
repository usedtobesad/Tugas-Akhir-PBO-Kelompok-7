package com.focuswall.app;

import com.focuswall.ui.MenuWindow;

import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        // Ensure GUI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MenuWindow().setVisible(true));
    }
}