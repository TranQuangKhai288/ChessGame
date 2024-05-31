package main;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
        JFrame window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel gamePanel = new GamePanel(); // Create an instance of GamePanel

        // DashBoard
        DashBoard dashboard = new DashBoard(); 

        JButton btnNewGame = dashboard.getButtonNewGame();
        btnNewGame.addActionListener(e -> {
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
        
        window.add(dashboard);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

}
