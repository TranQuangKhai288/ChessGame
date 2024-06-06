package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgotPassword extends JPanel {

    private JTextField emailField;
    private Runnable onBackToLogin;

    public ForgotPassword(Runnable onBackToLogin) {
        this.onBackToLogin = onBackToLogin;

        setPreferredSize(new Dimension(500, 500));
        setLayout(new GridBagLayout());
        setBackground(new Color(34, 45, 50)); // Dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        JLabel titleLabel = new JLabel("Forgot Password");
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

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Serif", Font.BOLD, 18));
        submitButton.setBackground(new Color(255, 215, 0)); // Gold color
        submitButton.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Serif", Font.BOLD, 18));
        backButton.setBackground(new Color(255, 215, 0)); // Gold color
        backButton.setForeground(Color.BLACK);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(backButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                callAPI(email);
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

    private void callAPI(String email) {
        try {
            URL url = new URL("http://localhost:5000/user/resetAccount");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON request body
            String requestBody = String.format("{\"email\": \"%s\"}", email);

            // Send request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            if (status == 200) {
                // Successful request, show success message
                JOptionPane.showMessageDialog(this, "Check your email for the new password", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Handle unsuccessful request (e.g., show error message)
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JOptionPane.showMessageDialog(this, "Error: " + response.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

