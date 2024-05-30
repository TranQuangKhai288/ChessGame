package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Color;

public class DashBoard extends JPanel implements Runnable {
	
	private GamePanel gamePanel; // Reference to the GamePanel instance
	JButton btnNewGame;
    JButton btnLoadGame;
	public JButton getButtonNewGame() {
		return btnNewGame;
	}
	public JButton getButtonLoadGame() {
		return btnLoadGame;
	}
    public DashBoard() {
    	this.gamePanel = gamePanel;
    	setLayout(null);
        setPreferredSize(new Dimension(600, 400));
    	
    	 JLabel imgBackground = new JLabel("New Game");
         imgBackground.setBounds(0, 0, 598, 399);
         Image originalImage = new ImageIcon(this.getClass().getResource("/background.png")).getImage();
         Image scaledImage = originalImage.getScaledInstance(imgBackground.getWidth(), imgBackground.getHeight(), Image.SCALE_SMOOTH);
         
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
         btnPlayOnline.setBounds(376, 170, 150, 30);
         add(btnPlayOnline);
         
         JButton btnPlayWithAI = new JButton("Play With AI");
         btnPlayWithAI.setForeground(Color.WHITE);
         btnPlayWithAI.setBackground(new Color(112, 128, 144));
         btnPlayWithAI.setBounds(376, 210, 150, 30);
         add(btnPlayWithAI);
         imgBackground.setIcon(new ImageIcon(scaledImage));
         add(imgBackground);
    }

    

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}
