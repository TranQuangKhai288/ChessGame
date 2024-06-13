package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;

import server.ChessServer;
public class ChessClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ChessServer server;
    int newCol;
    int newRow;
    int color;
    String type;
    String role;
    public ChessClientHandler(Socket socket, ChessServer server, String role) {
        this.socket = socket;
        this.server = server;
        this.role = role;
    }
    public Socket getSocket() {
    	return this.socket;
    }

    @Override
    public void run() {
        try {
        	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(socket.getOutputStream(), true);
            out.println(role);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
            	if (inputLine.trim().startsWith("{")) { 
                    JSONObject moveData = new JSONObject(inputLine);
                    out.println(moveData.toString());	
                    server.handleMessageFromClient(inputLine, this);
            	}	
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void getGameState(String state) {
    	out.println(state);
    }
    public void sendMessage(String message) {
    	System.out.println(message+ " this is where it sended");
        out.println(message);
    }
}
