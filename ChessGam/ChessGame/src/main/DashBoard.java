package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DashBoard extends JPanel implements Runnable {

    private GamePanel gamePanel; // Reference to the GamePanel instance
    JButton btnNewGame;
    JButton btnLoadGame;
    JButton btnPlayWithAI;
    JButton btnShowTopUser;

    public JButton getButtonLoadGame() {
        return btnLoadGame;
    }

    public JButton getButtonNewGame() {
        return btnNewGame;
    }

    public JButton getButtonPlayWithAI() {
        return btnPlayWithAI;
    }
    
    public JButton getButtonTop5Player() {
        return btnShowTopUser;
    }

    public DashBoard() {
        this.gamePanel = gamePanel;
        setLayout(null);
        setPreferredSize(new Dimension(600, 400));
        Image originalImage = new ImageIcon(this.getClass().getResource("/background.png")).getImage();

        // Retrieve the user ID from UserSession
        String userName = UserSession.getInstance().getUserName();
        String welcomeMessage = "Welcome, " + (userName != null ? userName : "Guest");

        // Create and add the welcome message label
        JLabel lblWelcome = new JLabel(welcomeMessage);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBounds(20, 20, 300, 30); // Adjust bounds as needed
        add(lblWelcome);

        btnNewGame = new JButton("New Game");
        btnNewGame.setForeground(new Color(255, 255, 255));
        btnNewGame.setBackground(new Color(112, 128, 144));
        btnNewGame.setBounds(376, 130, 150, 30);
        add(btnNewGame);

        btnLoadGame = new JButton("Load Game");
        btnLoadGame.setForeground(new Color(255, 255, 255));
        btnLoadGame.setBackground(new Color(112, 128, 144));
        btnLoadGame.setBounds(376, 170, 150, 30);
        add(btnLoadGame);

        JButton btnPlayOnline = new JButton("Play Online");
        btnPlayOnline.setForeground(Color.WHITE);
        btnPlayOnline.setBackground(new Color(112, 128, 144));
        btnPlayOnline.setBounds(376, 210, 150, 30);
        add(btnPlayOnline);

        btnPlayWithAI = new JButton("Play With AI");
        btnPlayWithAI.setForeground(Color.WHITE);
        btnPlayWithAI.setBackground(new Color(112, 128, 144));
        btnPlayWithAI.setBounds(376, 250, 150, 30);
        add(btnPlayWithAI);
        
        btnShowTopUser = new JButton("Top 5 Player");
        btnShowTopUser.setForeground(Color.WHITE);
        btnShowTopUser.setBackground(new Color(112, 128, 144));
        btnShowTopUser.setBounds(376, 290, 150, 30);
        add(btnShowTopUser);
        
        JLabel imgBackground = new JLabel("New Game");
        imgBackground.setBounds(0, 0, 598, 399);
        Image scaledImage = originalImage.getScaledInstance(imgBackground.getWidth(), imgBackground.getHeight(), Image.SCALE_SMOOTH);
        
        imgBackground.setIcon(new ImageIcon(scaledImage));
        add(imgBackground);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }
}
