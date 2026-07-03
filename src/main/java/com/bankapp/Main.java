package com.bankapp;

import com.bankapp.ui.BankMainFrame;

import javax.swing.*;

/**
 * Application entry point. Kept deliberately tiny: its only job is to set
 * a native look-and-feel and hand control to the UI on the Swing event
 * dispatch thread, as required by all Swing applications.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Fall back to the default cross-platform look and feel silently.
        }

        SwingUtilities.invokeLater(() -> new BankMainFrame().setVisible(true));
    }
}
