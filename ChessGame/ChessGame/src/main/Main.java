package main;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame window = new JFrame("Chess Game");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.setResizable(false);
		
		//DashBoard
		DashBoard dashboard = new DashBoard();
		
		
		//add game panel(VIEW) into the window
//		GamePanel gp = new GamePanel();
		window.add(dashboard);
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
//		
//		gp.launchGame();
		
	}

}
