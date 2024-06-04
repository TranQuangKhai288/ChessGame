package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        // Create placeholder for the Runnables to allow mutual referencing
        Runnable[] showLoginPanel = new Runnable[1];
        Runnable[] showRegisterPanel = new Runnable[1];
        Runnable[] showForgotPasswordPanel = new Runnable[1];
        Runnable[] showDashboardPanel = new Runnable[1];

        // Define the login panel
        showLoginPanel[0] = () -> {
            Login loginPanel = new Login(
                () -> { // On success
                    showDashboardPanel[0].run();
                },
                showRegisterPanel[0], // On register button click
                showForgotPasswordPanel[0] // On forgot password button click
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
                    showDashboardPanel[0].run();
                },
                showLoginPanel[0] // On back to login button click
            );

            window.getContentPane().removeAll();
            window.add(registerPanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        };

        // Define the forgot password panel
        showForgotPasswordPanel[0] = () -> {
            ForgotPassword forgotPasswordPanel = new ForgotPassword(
                showLoginPanel[0] // On back to login button click
            );

            window.getContentPane().removeAll();
            window.add(forgotPasswordPanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        };

        // Define the dashboard panel
        showDashboardPanel[0] = () -> {
            DashBoard dashboard = new DashBoard();
            JButton btnNewGame = dashboard.getButtonNewGame();
            JButton btnLoadGame = dashboard.getButtonLoadGame();
            btnLoadGame.addActionListener(e -> {
                loadGames();
            });
            btnNewGame.addActionListener(e -> {
                GamePanel gamePanel = new GamePanel(null); // Create an instance of GamePanel
                // Add the GamePanel to the window
                window.getContentPane().removeAll();
                window.getContentPane().add(gamePanel);
                window.pack();
                window.setLocationRelativeTo(null);
                window.setVisible(true);

                // Start the game
                gamePanel.launchGame();
            });

            JButton btnPlayWithAI = dashboard.getButtonPlayWithAI();
            btnPlayWithAI.addActionListener(e -> {
                GamePanel gamePanel = new GamePanel(null);
                gamePanel.modeAI = 1;
                gamePanel.chessEngine = new ChessEngine();
                gamePanel.chessEngine.startEngine("ChessEngine\\stockfish\\stockfish-windows-x86-64-avx2.exe");
                window.getContentPane().removeAll();
                window.getContentPane().add(gamePanel);
                window.pack();
                window.setLocationRelativeTo(null);
                window.setVisible(true);

                gamePanel.launchGame();
            });

            JButton btnShowTop5Player = dashboard.getButtonTop5Player();
            btnShowTop5Player.addActionListener(e -> {
                System.out.print("top 5 user");
                Top5User top5Player = new Top5User(window, showDashboardPanel[0]);
                window.getContentPane().removeAll();
                window.getContentPane().add(top5Player);
                window.revalidate();
                window.repaint();
                window.pack();
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            });

            // Show the Dashboard
            window.getContentPane().removeAll();
            window.add(dashboard);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        };

        // Show the Login panel initially
        showLoginPanel[0].run();
    }

    private static void loadGames() {
        try {
            URL url = new URL("http://localhost:5000/game/load");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                Gson gson = new Gson();
                System.out.println(response.toString());
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                List<Map<String, Object>> games = gson.fromJson(jsonResponse.get("listGame"), new TypeToken<List<Map<String, Object>>>(){}.getType());

                // Display the games as buttons
                displayGamesAsButtons(games);
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

   

    private static void displayGamesAsButtons(List<Map<String, Object>> games) {
        // Create a JFrame to display the game buttons
        JFrame frame = new JFrame("Games List");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a JPanel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        // Create buttons for each game
        for (Map<String, Object> game : games) {
            String gameId = game.get("_id").toString();
            String gameNumber = game.get("game").toString();
            JButton gameButton = new JButton("Game Number: " + gameNumber);
            gameButton.setForeground(new Color(255, 255, 255));
            gameButton.setBackground(new Color(112, 128, 144));
            gameButton.setBounds(376, 130, 150, 30);
            gameButton.setAlignmentX((float) 0.5);
            // Add an ActionListener to handle button clicks
            gameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GamePanel gamePanel = new GamePanel(gameId);
                    // Handle game button click
                    System.out.println("Game Button Clicked: " + gameId);
                    // Add the GamePanel to the window
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(gamePanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                    // Start the game
                    gamePanel.launchGame();
                }
            });

            panel.add(gameButton);
        }

        // Add the panel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Display the JFrame
        frame.setVisible(true);
    }
}
