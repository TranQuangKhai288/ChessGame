package main;

import javax.swing.*;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

public class Login extends JPanel implements Runnable {

    private JTextField emailField;
    private JPasswordField passwordField;
    
    private void callAPI(String email, String password) {
        try {
            URL url = new URL("http://localhost:5000/auth/signIn");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON request body
            String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

            // Send request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            // Read response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                // Handle response
                System.out.println("Response: " + response.toString());
            }

            // Close connection
            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle exception
        }
    }

    public Login() {
        setPreferredSize(new Dimension(500, 500));
        setLayout(new GridBagLayout());
        setBackground(new Color(34, 45, 50)); // Dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        JLabel titleLabel = new JLabel("King Chess Login");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10); // Reset insets

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Serif", Font.BOLD, 18));
        submitButton.setBackground(new Color(255, 215, 0)); // Gold color
        submitButton.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                
                // Call the method to make the API request
                callAPI(email, password);
            }
        });
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("King Chess Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Login());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Login());
    }
}
