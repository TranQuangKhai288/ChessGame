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
    
    public ChessClientHandler(Socket socket, ChessServer server) {
        this.socket = socket;
        this.server = server;
    }
    public Socket getSocket() {
    	return this.socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
            	if (inputLine.trim().startsWith("{")) {
                    // Phân tích dữ liệu từ client
                    JSONObject moveData = new JSONObject(inputLine);
                    out.println(moveData.toString());	
                    // Xử lý di chuyển quân cờ
                    // Ví dụ: cập nhật trạng thái của bảng cờ và kiểm tra tính hợp lệ của nước đi

                    // Phản hồi cho client (nếu cần)
                    // Ví dụ: gửi thông báo xác nhận về việc di chuyển quân cờ thành công
                    server.handleMessageFromClient(inputLine, this);
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
    	System.out.println(message+ "  this is where it sended");
    	JSONObject moveData = new JSONObject(message);
        out.println(message);
    }
}
