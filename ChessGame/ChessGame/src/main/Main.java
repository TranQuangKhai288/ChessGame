package main;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Create placeholder for the Runnables to allow mutual referencing
        Runnable[] showLoginPanel = new Runnable[1];
        Runnable[] showRegisterPanel = new Runnable[1];

        // Define the login panel
        showLoginPanel[0] = () -> {
            Login loginPanel = new Login(
                () -> { // On success
                    SwingUtilities.invokeLater(() -> {
                        DashBoard dashboard = new DashBoard();
                        JButton btnNewGame = dashboard.getButtonNewGame();
                        btnNewGame.addActionListener(e -> {
                            GamePanel gamePanel = new GamePanel(); // Create an instance of GamePanel
                            // Add the GamePanel to the window
                            window.getContentPane().removeAll();
                            window.getContentPane().add(gamePanel);
                            window.pack();
                            window.setLocationRelativeTo(null);
                            window.setVisible(true);

                            // Start the game
                            gamePanel.launchGame();
                        });

                        // Show the Dashboard
                        window.getContentPane().removeAll();
                        window.add(dashboard);
                        window.pack();
                        window.setLocationRelativeTo(null);
                        window.setVisible(true);
                    });
                },
                showRegisterPanel[0] // On register button click
            );

            window.getContentPane().removeAll();
            window.add(loginPanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        };

        // Define the register panel
        showRegisterPanel[0] = () -> {
            Register registerPanel = new Register(
                () -> { // On success
                    SwingUtilities.invokeLater(() -> {
                        DashBoard dashboard = new DashBoard();
                        JButton btnNewGame = dashboard.getButtonNewGame();
                        btnNewGame.addActionListener(e -> {
                            GamePanel gamePanel = new GamePanel(); // Create an instance of GamePanel
                            // Add the GamePanel to the window
                            window.getContentPane().removeAll();
                            window.getContentPane().add(gamePanel);
                            window.pack();
                            window.setLocationRelativeTo(null);
                            window.setVisible(true);

                            // Start the game
                            gamePanel.launchGame();
                        });

                        // Show the Dashboard
                        window.getContentPane().removeAll();
                        window.add(dashboard);
                        window.pack();
                        window.setLocationRelativeTo(null);
                        window.setVisible(true);
                    });
                },
                showLoginPanel[0] // On back to login button click
            );

            window.getContentPane().removeAll();
            window.add(registerPanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        };

        // Show the Login panel initially
        showLoginPanel[0].run();
    }
}
