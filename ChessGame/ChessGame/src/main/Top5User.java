package main;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Top5User extends JFrame {
    private JTable table;
    private String[] columnNames = {"Name", "Email", "Mark"};
    private Object[][] data;

    public Top5User() {
        setTitle("Top 5 Users");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        fetchTopUsers();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Top 5 Users", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("User Rankings", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void fetchTopUsers() {
        try {
            URL url = new URL("http://localhost:5000/game/getTop5User"); // Replace with your actual endpoint
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray topUsers = jsonResponse.getJSONArray("topUsers");

                data = new Object[topUsers.length()][3];
                for (int i = 0; i < topUsers.length(); i++) {
                    JSONObject user = topUsers.getJSONObject(i);
                    JSONObject userDetails = user.getJSONObject("user");

                    data[i][0] = userDetails.getString("name");
                    data[i][1] = userDetails.getString("email");
                    data[i][2] = user.getInt("mark");
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Top5User frame = new Top5User();
            frame.setVisible(true);
        });
    }
}
