package server;

import java.net.ServerSocket;
import java.net.Socket;
import  java.util.ArrayList;
import client.ChessClientHandler;
import org.json.JSONObject;
import java.io.*;
public class ChessServer {
    private ServerSocket serverSocket;
    private ArrayList<ChessClientHandler> clients = new ArrayList<>();

    public ChessServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        System.out.println("Chess server is starting...");
        while (true) {	
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("new client connected");
                String role = clients.size() %2 == 0 ? "white" : "black";
                ChessClientHandler clientHandler = new ChessClientHandler(clientSocket, this, role);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                if(clients.size() == 2) {
                	broadcastMessageToAll("true");
                }
            } catch (IOException e) {
                System.out.println("Error accepting client connection");
                e.printStackTrace();
            }
            	
        }	
    }
    public void broadcastMessageToAll(String state) {
        for (ChessClientHandler client : clients) {
                client.getGameState(state);
        }
    }
    public void broadcastMessage(String message, ChessClientHandler sender) {
        for (ChessClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    public void handleMessageFromClient(String receivedData, ChessClientHandler sender) throws IOException {
		JSONObject receivedJson = new JSONObject(receivedData);

		for (ChessClientHandler client : clients) {		
		    if (client != sender) {
		    	System.out.println("Sending data to client: " + client.getSocket());
		        client.sendMessage(receivedData);
		    }else {
	            System.out.println("Not sending data to sender client: " + client.getSocket());
	        }
		}
    }


    public static void main(String[] args) {
        try {
            ChessServer server = new ChessServer(12345);
            server.start();
        } catch (IOException e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }
}

