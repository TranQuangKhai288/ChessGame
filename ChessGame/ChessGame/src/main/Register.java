package main;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends JPanel implements Runnable {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private Runnable onSuccess;
    private Runnable onBackToLogin;

    public Register(Runnable onSuccess, Runnable onBackToLogin) {
        this.onSuccess = onSuccess;
        this.onBackToLogin = onBackToLogin;

        setPreferredSize(new Dimension(500, 500));
        setLayout(new GridBagLayout());
        setBackground(new Color(34, 45, 50)); // Dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10); // Reset insets

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        confirmPasswordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Serif", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(confirmPasswordField, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Serif", Font.BOLD, 18));
        submitButton.setBackground(new Color(255, 215, 0)); // Gold color
        submitButton.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        // Error label
        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(errorLabel, gbc);

        // Back to Login button
        JButton backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Serif", Font.BOLD, 18));
        backButton.setBackground(new Color(255, 215, 0)); // Gold color
        backButton.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        add(backButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    errorLabel.setText("All fields are required.");
                    errorLabel.setVisible(true);
                } else if (!password.equals(confirmPassword)) {
                    errorLabel.setText("Password and Confirm Password do not match.");
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setVisible(false);
                    System.out.println("Username: " + username);
                    System.out.println("Email: " + email);
                    System.out.println("Password: " + password);
                    System.out.println("Password and Confirm Password match.");

                    // Call the method to make the API request
                    callAPI(username, email, password);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onBackToLogin != null) {
                    onBackToLogin.run();
                }
            }
        });
    }

    private void callAPI(String username, String email, String password) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:5000/auth/signUp");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON request body
            String requestBody = String.format("{\"name\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}", username, email, password);

            // Send request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            if (status == 201) {
                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line.trim());
                    }

                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String id = data.getString("_id");
                    String name = data.getString("name");

                   UserSession.getInstance().setUserId(id);
                   UserSession.getInstance().setUserName(name);

                    // Successful registration, run the onSuccess callback
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                }
            } else {
                // Handle unsuccessful registration (e.g., show error message)
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        System.out.println("Error response: " + response.toString());
                    }
                } else {
                    System.out.println("Error: Received HTTP status code " + status);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle exception
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    @Override
    public void run() {
        JFrame frame = new JFrame("Register Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Register(onSuccess, onBackToLogin));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Register(null, null));
    }
}
