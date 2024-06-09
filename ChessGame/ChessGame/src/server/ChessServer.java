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
                String role = clients.size() == 0 ? "white" : "black";
                ChessClientHandler clientHandler = new ChessClientHandler(clientSocket, this, role);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Error accepting client connection");
                e.printStackTrace();
            }
            	
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
        // Xử lý dữ liệu nhận được từ Client A hoặc Client B
		JSONObject receivedJson = new JSONObject(receivedData);
		int index = 0;
		// Gửi dữ liệu đến cả hai loại client
		for (ChessClientHandler client : clients) {
			
		    if (client != sender) {
		    	if(index == 0) {
		    		System.out.println("this is A");
		    	}
		    	else {
		    		System.out.println("this is B");
		    	}
		        client.sendMessage(receivedData);
		    }
		    index += 1;
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

