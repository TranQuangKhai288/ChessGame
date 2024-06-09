package main;


import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.JSONObject;
import org.json.JSONArray;
import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import piece.PieceData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.swing.SwingConstants;

//class for create VIEW of game
public class GamePanel extends JPanel implements Runnable {
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	String id;
	JButton saveButton;
	JButton connectButton;
	boolean whiteKingSide = false;
	boolean whiteQueenSide = false;
    boolean blackKingSide = false;
    boolean blackQueenSide = false;
    
    // Socket communication
    private PrintWriter out;
    private BufferedReader in;
	//User Infor
	String userId = UserSession.getInstance().getUserId();
	
	//CHESS BOARD
	char[][] chessBoard = {
	        {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
	        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
	        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
	        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
	        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
	        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
	        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
	        {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
	    };
	
	//PIECES
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public Piece wKing, wRookKingSide, wRookQueenSide, bKing, bRookKingSide, bRookQueenSide;
	
	
	//this array will contain the pieces currently on the board
	//use this array
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> promoPieces = new ArrayList<>();
	
	Piece activeP, checkingP, engineP;
	public static Piece castlingP;
	
	//AI
	public ChessEngine chessEngine;
	int modeAI = 0;
	boolean playerTurn = true;
	
	//STATE
	String targetEnPassant = "-";
    int halfmoveClock = 0;
    int fullmoveNumber = 1;

	//COLOR
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	//MOVE
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameover;
	boolean stalemate;
	int currentColor = WHITE;
	boolean onlineMode = false;
	
	String playerRole;
	//constructor
	public GamePanel(String id) {
		this.id = id;
        setLayout(null);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.DARK_GRAY);
		
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		initSaveAsButton();
		if(id != null) {
			initSaveButton();
		}
		setPieces();
		copyPieces(pieces, simPieces);
        if(onlineMode) {
        	playOnline();
        }
        connectButton = new JButton("CONNECT");
        connectButton.addActionListener(new ConnectButtonListener());
        connectButton.setBounds(1100, 52, 80, 30);
        add(connectButton);
	}
	public void playOnline() {
		this.onlineMode = true;
		System.out.println("try connect");
		new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 12345); // Địa chỉ IP và cổng của server
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // Gửi thông điệp chào mừng tới server
                out.println("Client connected");
                
                // Nhận phản hồi từ server và xử lý
                String response;
                while ((response = in.readLine()) != null) {
                	if (!response.trim().startsWith("{")) {
                		playerRole = response;
                		System.out.println(playerRole + "First");
                	}
                	else {
                    System.out.println("Server says: " + response);
                    JSONObject moveData = new JSONObject(response);
                    JSONArray piecesArray = moveData.getJSONArray("data");
                    currentColor = moveData.getInt("currentColor");
                    pieces.clear();
                    for (int i = 0; i < piecesArray.length(); i++) {
                        JSONObject pieceJson = piecesArray.getJSONObject(i);
                        Piece piece = Piece.fromJson(pieceJson);
                        if(piece instanceof Pawn) {
                        	piece.twoStepped = true;
                        }
                        pieces.add(piece);
                    }    
                    copyPieces(pieces, simPieces);
                	}
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
	}
	public boolean pieceExists(Piece piece) {
	    for (Piece p : pieces) {
	        if (p.equals(piece)) {
	            return true; // Đã tồn tại piece trong danh sách
	        }
	    }
	    return false; // Không tìm thấy piece trong danh sách
	}
	
	
	// Listener for the connect button
    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                try {
                    Socket socket = new Socket("127.0.0.1", 12345); // Địa chỉ IP và cổng của server
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Gửi thông điệp chào mừng tới server
                    out.println("Client connected");
                    
                    // Nhận phản hồi từ server và xử lý
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("Server says: " + response);
                        JSONObject moveData = new JSONObject(response);
                        JSONArray piecesArray = moveData.getJSONArray("data");
                        currentColor = moveData.getInt("currentColor");
                        pieces.clear();
                        for (int i = 0; i < piecesArray.length(); i++) {
                            JSONObject pieceJson = piecesArray.getJSONObject(i);
                            Piece piece = Piece.fromJson(pieceJson);
                            if(piece instanceof Pawn) {
                            	piece.twoStepped = true;
                            }
                            pieces.add(piece);
                        }    
                        copyPieces(pieces, simPieces);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }
    
    
    
	private void initSaveButton() {
		 saveButton = new JButton("SAVE");
	        saveButton.setBounds(WIDTH - 220, 10, 100, 30); // Position top right
	        saveButton.addActionListener(e -> {
	               sendPieces(simPieces, null, id);
	        });
	        add(saveButton);
	}
	 private void initSaveAsButton() {
	        saveButton = new JButton("SAVE AS");
	        saveButton.setBounds(WIDTH - 100, 10, 100, 30); // Position top right
	        saveButton.addActionListener(e -> {
	            String saveName = JOptionPane.showInputDialog(null, "Enter the name of the save:", "Save Game", JOptionPane.PLAIN_MESSAGE);
	            if (saveName != null && !saveName.trim().isEmpty()) {
	                sendPieces(simPieces, saveName.trim(), null);
	            }
	        });
	        add(saveButton);
	    }

public void sendPieces(ArrayList<Piece> pieces, String saveName, String id) {
    ArrayList<PieceData> pieceDataList = new ArrayList<>();
    for (Piece piece : pieces) {
        String type = "";
        if (piece instanceof Rook) {
            type = "Rook";
        } else if (piece instanceof King) {
            type = "King";
        } else if (piece instanceof Queen) {
            type = "Queen";
        } else if (piece instanceof Bishop) {
            type = "Bishop";
        } else if (piece instanceof Knight) {
            type = "Knight";
        } else if (piece instanceof Pawn) {
            type = "Pawn";
        }
        PieceData pieceData = new PieceData(piece.col, piece.row, piece.color, type);
        pieceDataList.add(pieceData);
    }
    boolean whiteKingSide = false;
    boolean whiteQueenSide = false;
    boolean blackKingSide = false;
    boolean blackQueenSide = false;
    
    if (wKing != null && wRookKingSide != null && wRookQueenSide != null) {
        if (!wKing.moved && !wRookKingSide.moved && chessBoard[7][7] == 'R') {
            whiteKingSide = true;
        }
        if (!wKing.moved && !wRookQueenSide.moved && chessBoard[7][0] == 'R') {
            whiteQueenSide = true; 
        }
    }

    if (bKing != null && bRookKingSide != null && bRookQueenSide != null) {
        if (!bKing.moved && !bRookKingSide.moved && chessBoard[0][7] == 'r') {
            blackKingSide = true; 
        }
        if (!bKing.moved && !bRookQueenSide.moved && chessBoard[0][0] == 'r') {
            blackQueenSide = true; 
        }
    }
    SaveData saveData = new SaveData(saveName, pieceDataList, this.currentColor, whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide);
    Gson gson = new Gson();
    String json = gson.toJson(saveData);

    if (id != null) {
        sendPutRequest(json, id);
    } else {
        sendPostRequest(json);
    }
}

private void sendPostRequest(String json) {
    try {
        URL url = new URL("http://localhost:5000/game/save");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")) {
            writer.write(json);
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                String jsonResponse = response.toString();
                System.out.println(jsonResponse);

                JSONObject objectResponse = parseDataFromResponse(jsonResponse);
                this.id = objectResponse.getString("id");
                initSaveButton();
                JOptionPane.showMessageDialog(null, "Saved", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            System.out.println("Error: " + responseCode);
        }

        connection.disconnect();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}

private void sendPutRequest(String json, String id) {
    try {
        URL url = new URL("http://localhost:5000/game/override/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")) {
            writer.write(json);
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            JOptionPane.showMessageDialog(null, "Saved", "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Game updated successfully.");
        } else {
            System.out.println("Failed to update game. Response Code: " + responseCode);
        }

        connection.disconnect();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}

	 private JSONObject parseDataFromResponse(String response) {
	        try {
	            JSONObject jsonObject = new JSONObject(response);
	            JSONObject data = jsonObject.getJSONObject("data");
	            return data;
	        } catch (org.json.JSONException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 private void callAPIBonusMarkUser(String user_id, String score) {
	        try {
	            URL url = new URL("http://localhost:5000/user/updateScore");
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Content-Type", "application/json");
	            connection.setDoOutput(true);

	            // Create JSON request body
	            String requestBody = String.format("{\"user_id\": \"%s\", \"score\": \"%s\"}", user_id, score);

	            // Send request body
	            try (OutputStream outputStream = connection.getOutputStream()) {
	                byte[] input = requestBody.getBytes("utf-8");
	                outputStream.write(input, 0, input.length);
	            }

	            int status = connection.getResponseCode();
	            if (status == 200) {
	                
	            } else {
	                // Handle unsuccessful login (e.g., show error message)
	                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
	                    StringBuilder response = new StringBuilder();
	                    String line;
	                    while ((line = reader.readLine()) != null) {
	                        response.append(line);
	                    }
	                    System.out.println("Error response: " + response.toString());
	                }
	            }

	            connection.disconnect();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	            // Handle exception
	        }
	    }
	
	
	public void setPieces() {
		
		//the White TEAM
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));	
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new King(WHITE, 4, 7));
		
		//the Black TEAM
		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));	
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Queen(BLACK, 3, 0));
		pieces.add(new King(BLACK, 4, 0));
		if (id == null) {
			pieces.clear();
			System.out.print("is null");
			// the White TEAM
			pieces.add(new Pawn(WHITE, 0, 6));
			pieces.add(new Pawn(WHITE, 1, 6));
			pieces.add(new Pawn(WHITE, 2, 6));
			pieces.add(new Pawn(WHITE, 3, 6));
			pieces.add(new Pawn(WHITE, 4, 6));
			pieces.add(new Pawn(WHITE, 5, 6));
			pieces.add(new Pawn(WHITE, 6, 6));
			pieces.add(new Pawn(WHITE, 7, 6));
			pieces.add(new Rook(WHITE, 0, 7));
			pieces.add(new Rook(WHITE, 7, 7));
			pieces.add(new Knight(WHITE, 1, 7));
			pieces.add(new Knight(WHITE, 6, 7));
			pieces.add(new Bishop(WHITE, 2, 7));
			pieces.add(new Bishop(WHITE, 5, 7));
			pieces.add(new Queen(WHITE, 3, 7));
			pieces.add(new King(WHITE, 4, 7));

			// the Black TEAM
			pieces.add(new Pawn(BLACK, 0, 1));
			pieces.add(new Pawn(BLACK, 1, 1));
			pieces.add(new Pawn(BLACK, 2, 1));
			pieces.add(new Pawn(BLACK, 3, 1));
			pieces.add(new Pawn(BLACK, 4, 1));
			pieces.add(new Pawn(BLACK, 5, 1));
			pieces.add(new Pawn(BLACK, 6, 1));
			pieces.add(new Pawn(BLACK, 7, 1));
			pieces.add(new Rook(BLACK, 0, 0));
			pieces.add(new Rook(BLACK, 7, 0));
			pieces.add(new Knight(BLACK, 1, 0));
			pieces.add(new Knight(BLACK, 6, 0));
			pieces.add(new Bishop(BLACK, 2, 0));
			pieces.add(new Bishop(BLACK, 5, 0));
			pieces.add(new Queen(BLACK, 3, 0));
			pieces.add(new King(BLACK, 4, 0));
		} else {
			pieces.clear();
			try {
			    String url = "http://localhost:5000/game/" + this.id;

			    // Tạo kết nối HTTP
			    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			    connection.setRequestMethod("GET");
			    connection.setRequestProperty("Accept", "application/json");

			    int responseCode = connection.getResponseCode();
			    if (responseCode == HttpURLConnection.HTTP_OK) {
			        // Đọc dữ liệu từ response
			        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			        StringBuilder response = new StringBuilder();
			        String line;
			        while ((line = reader.readLine()) != null) {
			            response.append(line);
			        }
			        reader.close();

			        // In ra dữ liệu game nhận được từ response
			        System.out.println(response.toString());

			        // Tạo một đối tượng Gson
			        Gson gson = new Gson();

			        // Chuyển đổi dữ liệu JSON thành một đối tượng Map<String, Object>
			        java.lang.reflect.Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
			        Map<String, Object> gameData = gson.fromJson(response.toString(), mapType);

			        // Lấy thông tin currentColor
			        if (gameData.get("currentColor") instanceof Double) {
			            System.out.println("Yessssss");
			        }
			        long curLong = Math.round((double) gameData.get("currentColor"));
			        int curInt = (int) curLong;
			        this.currentColor = gameData.get("currentColor") != null ? curInt : 0;
			        System.out.println(this.currentColor);
			        System.out.println(curLong);

			        // Lấy danh sách các đối tượng Piece từ dữ liệu trả về từ backend
			        List<Map<String, Object>> listPieces = (List<Map<String, Object>>) gameData.get("pieces");
			        this.whiteKingSide = (boolean) gameData.get("whiteKingSide");
			        this.whiteQueenSide = (boolean) gameData.get("whiteQueenSide");
			        this.blackKingSide = (boolean) gameData.get("blackKingSide");
			        this.blackQueenSide = (boolean) gameData.get("blackQueenSide");
			        // Tạo các đối tượng Piece từ danh sách pieces
			        for (Map<String, Object> pieceData : listPieces) {
			            int col = ((Double) pieceData.get("col")).intValue();
			            int row = ((Double) pieceData.get("row")).intValue();
			            int color = ((Double) pieceData.get("color")).intValue();
			            String type = (String) pieceData.get("type");

			            // Tạo đối tượng Piece tương ứng với dữ liệu từ backend
			            Piece piece = createPiece(color, type, col, row);
			            if (type.equals("King")) {
			                if (color == WHITE) {
			                    wKing = piece;
			                } else {
			                    bKing = piece;
			                }
			            } else if (type.equals("Rook")) {
			                if (color == WHITE) {
			                    wRookQueenSide = piece;
			                } else {
			                    bRookQueenSide = piece;
			                }
			            }
			            pieces.add(piece);
			        }
			    } else {
			        System.out.println("GET request failed. Response Code: " + responseCode);
			    }

			    // Đóng kết nối
			    connection.disconnect();

			} catch (IOException e) {
			    e.printStackTrace();
			}

		}
		if(id==null) {
		//For tracking King and Rook
		wKing = pieces.get(15);
		wRookQueenSide = pieces.get(8);
		wRookKingSide = pieces.get(9);
		
		bKing = pieces.get(31);
		bRookQueenSide = pieces.get(24);
		bRookKingSide = pieces.get(25);
		}
	}
	private static Piece createPiece(int color, String type, int col, int row) {
		switch (type) {
			case "Pawn":
				return new Pawn(color, col, row);
			case "Rook":
				return new Rook(color, col, row);
			case "Knight":
				return new Knight(color, col, row);
			case "Bishop":
				return new Bishop(color, col, row);
			case "Queen":
				return new Queen(color, col, row);
			case "King":
				return new King(color, col, row);
			default:
				throw new IllegalArgumentException("Invalid piece type: " + type);
		}
	}
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
		
	}
	
	public String generateFEN() {
	    StringBuilder fen = new StringBuilder();

	    for (int i = 0; i < 8; i++) {
	        int emptySquares = 0;
	        for (int j = 0; j < 8; j++) {
	            char piece = chessBoard[i][j];
	            if (piece == ' ') {
	                emptySquares++;
	            } else {
	                if (emptySquares > 0) {
	                    fen.append(emptySquares);
	                    emptySquares = 0;
	                }
	                fen.append(piece);
	            }
	        }
	        if (emptySquares > 0) {
	            fen.append(emptySquares);
	        }
	        if (i < 7) {
	            fen.append('/');
	        }
	    }

	    //the active color
	    fen.append(' ');
	    fen.append(currentColor == WHITE ? 'w' : 'b');

	    //castling availability
	    fen.append(' ');

	    
	    if(id==null) {
	    if(!wKing.moved && !wRookKingSide.moved && chessBoard[7][7] == 'R')
	    	whiteKingSide = true;
	    if(!wKing.moved && !wRookQueenSide.moved && chessBoard[7][0] == 'R')
	    	whiteQueenSide = true; 
	    	
	    if(!bKing.moved && !bRookKingSide.moved && chessBoard[0][7] == 'r')
	    	blackKingSide = true;
	    if(!bKing.moved && !bRookQueenSide.moved && chessBoard[0][0] == 'r')
	    	blackQueenSide = true; 
	    }
	    if (whiteKingSide) {
	        fen.append('K');
	    }
	    if (whiteQueenSide) {
	        fen.append('Q');
	    }
	    if (blackKingSide) {
	        fen.append('k');
	    }
	    if (blackQueenSide) {
	        fen.append('q');
	    }
	    if (!whiteKingSide && !whiteQueenSide && !blackKingSide && !blackQueenSide) {
	        fen.append('-');
	    }

	    //en passant target square
	    fen.append(" "+ targetEnPassant);

	    //halfmove clock
	    fen.append(" " + halfmoveClock);

	    //fullmove number
	    fen.append(" "+ fullmoveNumber);

	    return fen.toString();
	}

	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	
	@Override
	public void run() {
		//GAME LOOP
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		
		while(gameThread !=null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			if(delta>-1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	
	public void movePiece() {
		if(onlineMode) {
			System.out.print(playerRole);
       		// Tạo JSON từ dữ liệu di chuyển
	        JSONObject moveData = new JSONObject();
	        JSONArray jsonArray = new JSONArray();
	        for (Piece piece1 : pieces) {
	            jsonArray.put(piece1.toJson());
	        }
	        moveData.put("data", jsonArray);
	        moveData.put("currentColor", currentColor);

	        
	        String jsonMove = moveData.toString();

	        // Gửi dữ liệu di chuyển đến server
	        if (out != null) {
	            out.println(jsonMove);
	        }
		}
}

	
	
	private void update() {
		//Mouse Button PRESSED	
		if (playerTurn == true) {
			if(promotion) {
				
				promoting();
			}else if(gameover == false && stalemate == false) {
				
				if(mouse.pressed) {
					
					if(activeP == null) {
						//if the activeP is null, check if you can pick up a piece
						for(Piece piece : simPieces) {
							if (piece.color == currentColor &&
	                                piece.col == mouse.x / Board.SQUARE_SIZE &&
	                                piece.row == mouse.y / Board.SQUARE_SIZE &&
	                                ((playerRole.equals("white") && piece.color == WHITE) || 
	                                 (playerRole.equals("black") && piece.color != WHITE)))                      
									{
										activeP = piece;
									}
						}
					}
					else {
						//if the player is holding a piece, simulate the move
						simulate();
					}
				}
				//Mouse Button RLEASED
				if(mouse.pressed == false) {
					if(activeP !=null) {
						System.out.println("VALID: "+ validSquare);
						if(validSquare) {
							 Piece dummy = activeP;
							//MOVE CONFIRM
							//Update the piece list
							copyPieces(simPieces, pieces);
						    chessBoard[activeP.preRow][activeP.preCol] = ' ';
					        if(activeP.hittingP != null)
					        	chessBoard[activeP.hittingP.row][activeP.hittingP.col] = ' ';
						    chessBoard[activeP.row][activeP.col] = activeP.symbol;		    
						    
						    
							//En Passant string for FEN
							if(activeP.type == Type.PAWN) {
							    if(Math.abs(activeP.row-activeP.preRow)==2) {
							        char col = (char) ('a' + activeP.col);
							        int row = 7 - (activeP.row + activeP.preRow)/2 + 1; 
							        targetEnPassant = "" + col + row;
							    }
							}
							else
								targetEnPassant = "-";
							
							//halfmove string for FEN
							if (activeP.type == Type.PAWN || activeP.hittingP != null) {
					            halfmoveClock = 0;
					        } else {
					            halfmoveClock++;
					        }
							
							//fullmove string for FEN
							if(activeP.color == 1)
								fullmoveNumber++;
									
							activeP.updatePosition();
							
							if(castlingP !=null) {
								castlingP.updatePosition();	
							}
							
							//
							if(isKingInCheck()&&isCheckmate()) {
								gameover=true;
								
								if (currentColor == WHITE) {
									
									callAPIBonusMarkUser(userId, "100");
									
								}
								
							}
							else if(isStalemate()&& isKingInCheck() == false) {
								stalemate = true;
							}
							else {
								if(canPromote()) {
									promotion = true;
								}else {	
							
									changePlayer();	
									
									
								}
							}
							movePiece();
							
						}else {
							copyPieces(pieces, simPieces);
							activeP.resetPosition();
							activeP = null;
						}
						
					}
				}
			}		
			
		}
		if (playerTurn == false && gameover == false){
			engineMove();			
	}
}
	public void engineMove(){
		String fen = generateFEN();
        System.out.println(fen);

        chessEngine.setBoardPosition(fen);
        try {
            String bestMove = chessEngine.getBestMove();

            int startCol = bestMove.charAt(0) - 'a';
            int startRow = 8 - Character.getNumericValue(bestMove.charAt(1));
            int endCol = bestMove.charAt(2) - 'a';
            int endRow = 8 - Character.getNumericValue(bestMove.charAt(3));
            char symbolP = chessBoard[startRow][startCol];
            
            for(Piece piece : simPieces) 
				if(piece.symbol == symbolP)
				{
					if(piece.col == startCol && piece.row == startRow)
					{
						engineP = piece;
						break;
					}
				}
            
		    engineP.hittingP = engineP.getHittingP(endCol, endRow);

		    engineP.canMove(endCol, endRow);
		    chessBoard[engineP.row][engineP.col] = ' ';
		    engineP.col = endCol;
		    engineP.row = endRow;
		    if(engineP.hittingP != null) {
		    	chessBoard[engineP.hittingP.row][engineP.hittingP.col] = ' ';
		    }
		    chessBoard[engineP.row][engineP.col] = engineP.symbol;
//			System.out.println(engineP.type);
		    	    		

		    engineP.updatePosition();   
//			System.out.println(engineP.hittingP);
		    
		    if (engineP.type == Type.PAWN || engineP.hittingP != null) {
	            halfmoveClock = 0;
	        } else {
	            halfmoveClock++;
	        }   
			
			
			if (engineP.type == Type.KING) {
				if(endCol == 2)	{
					if(currentColor == 0)
						castlingP = wRookQueenSide;
					else
						castlingP = bRookQueenSide;
				}
				else if(endCol == 6) {
					if(currentColor == 0)
						castlingP = wRookKingSide;
					else
						castlingP = bRookKingSide;
				}
				checkCastling();

				if (castlingP != null) {
					castlingP.updatePosition();
					castlingP = null;
				}			
			}
			
			if(engineP.hittingP != null) {
	            simPieces.remove(engineP.hittingP.getIndex());
				copyPieces(simPieces, pieces);
	        }  
		    //For promoting
		    if(engineP.type == Type.PAWN)
		    	if(engineP.row == 0 || engineP.row == 7)
		    	{
		            simPieces.remove(engineP.getIndex());

		    		char promotePSymbol = bestMove.charAt(4);
		    		
		    		if (promotePSymbol == 'n' || promotePSymbol == 'N') 
		    			simPieces.add(new Knight(currentColor, endCol, endRow));
		    		else if (promotePSymbol == 'b' || promotePSymbol == 'B')
		    			simPieces.add(new Bishop(currentColor, endCol, endRow));
		    		else if (promotePSymbol == 'r' || promotePSymbol == 'R')
		    			simPieces.add(new Rook(currentColor, endCol, endRow));
		    		else if (promotePSymbol == 'q' || promotePSymbol == 'Q')
		    			simPieces.add(new Queen(currentColor, endCol, endRow));	
		    		
					copyPieces(simPieces, pieces);
		    		chessBoard[endRow][endCol] = promotePSymbol;
		    		
		    	}
		    
		    activeP = engineP;
		    
		    if(isKingInCheck()&&isCheckmate()) {
				gameover=true;	
			}
			else if(isStalemate()&& isKingInCheck() == false) {
				stalemate = true;
			}
			else {
				changePlayer();	
			}
		    
		    activeP = null;
			engineP = null;

			fullmoveNumber++;

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void simulate() {
	    canMove = false;
	    validSquare = false;
	    
	    copyPieces(pieces, simPieces);
	    
	    //reset the castling piece's position
	    if(castlingP != null) {
	        castlingP.col = castlingP.preCol;
	        castlingP.x = castlingP.getX(castlingP.col);
	        castlingP = null;
	    }

	    //if a piece is being held, update its position
	    activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
	    activeP.y= mouse.y- Board.HALF_SQUARE_SIZE;

	    // Update the chessBoard array
	    activeP.col = activeP.getCol(activeP.x);
	    activeP.row = activeP.getRow(activeP.y);
	    // Update the chessBoard array

	    //check if the piece is hovering over a reachable square
	    if(activeP.canMove(activeP.col, activeP.row)) {
	        canMove = true;
	        //validSquare = true;
	        
	        // if hitting a piece, remove this piece from the list
	        if(activeP.hittingP != null) {
	            // Update the chessBoard array
	            simPieces.remove(activeP.hittingP.getIndex());
	        }   
	        
	        checkCastling();

	        if(isIllegal(activeP)==false && opponentCanCaptureKing()==false) {
	            validSquare = true;
	        }

	        //validSquare = true;
	    }
	}

	
	private boolean isStalemate() {
		
		int count = 0;
		for(Piece piece : simPieces) {
			if(piece.color != currentColor) {
				count++;
			}
		}
		if(count ==1) {
			if(kingCanMove(getKing(true))== false) {
				return true;
			}
		}
		
		return false;
	}
	
	private void checkCastling() {
		if(castlingP != null) {
			chessBoard[castlingP.row][castlingP.col] = ' ';

			if(castlingP.col ==0) {
				castlingP.col +=3;
			}
			else if(castlingP.col ==7) {
				castlingP.col -=2;
				
			}
			castlingP.x = castlingP.getX(castlingP.col);
			chessBoard[castlingP.row][castlingP.col] = castlingP.symbol;
		}
	}

	
	private boolean isCheckmate() {
		Piece king = getKing(true);
		if(kingCanMove(king)) {
			return false;
		}else {
			//check can block the attack with a piece
			
			// Check the position of the checking piece add the king in check
			int colDiff = Math.abs(checkingP.col);
			int rowDiff = Math.abs(checkingP.row);
			
			if(colDiff == 0) {
				//The checking piece is attacking vertically
				if(checkingP.row < king.row) {
					for(int row = checkingP.row; row < king.row; row ++) {
						for(Piece piece : simPieces) {
							if(piece!= king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				
				if(checkingP.row < king.row) {
					for(int row = checkingP.row; row > king.row; row --) {
						for(Piece piece : simPieces) {
							if(piece!= king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				
			}else if(rowDiff ==0) {
				//The Checking piece is attaching horizontally
				
				if(checkingP.col < king.col) {
					for(int col = checkingP.row; col < king.col; col ++) {
						for(Piece piece : simPieces) {
							if(piece!= king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.col > king.col) {
					for(int col = checkingP.row; col > king.col; col --) {
						for(Piece piece : simPieces) {
							if(piece!= king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				
				
			}else if(colDiff == rowDiff) {
				//the checking piece is attacking diagonally
				
				if(checkingP.row < king.row) {
					//checking piece is above the king
					if(checkingP.col < king.col) {
						//checking piece is in the upper left
						for(int col = checkingP.col, row = checkingP.row; col<king.col; col++,row++ ) {
							for(Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						//checking piece is in the upper right
						for(int col = checkingP.col, row = checkingP.row; col>king.col; col--,row++ ) {
							for(Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					
				}
				if(checkingP.row > king.row) {
					if(checkingP.col < king.col) {
						//checking piece is in the lower left
						for(int col = checkingP.col, row = checkingP.row; col<king.col; col++,row-- ) {
							for(Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						//checking piece is in the lower right
						for(int col = checkingP.col, row = checkingP.row; col>king.col; col--,row-- ) {
							for(Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}else {
				//The checking piece is Knight
			}
		}
		
		return true;
	}
	
	private boolean kingCanMove(Piece king) {
		if(isValidMove(king, -1, -1)) {return true;}
		if(isValidMove(king, 0, -1)) {return true;}
		if(isValidMove(king, 1, -1)) {return true;}
		if(isValidMove(king, 1, 0)) {return true;}
		if(isValidMove(king, -1, 0)) {return true;}
		if(isValidMove(king, -1, 1)) {return true;}
		if(isValidMove(king, 0, 1)) {return true;}
		if(isValidMove(king, 1, 1)) {return true;}
		return false;
	}
	
	private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
		boolean isValidMove = false;
		
		king.col +=colPlus;
		king.row +=rowPlus;
		
		if(king.canMove(king.col, king.row)) {
			if(king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if(isIllegal(king)==false) {
				isValidMove = true;
			}
		}
		king.resetPosition();
		copyPieces(pieces, simPieces);
		
		return isValidMove;
	}
	
 	private void changePlayer() {
		if(currentColor == WHITE) {
			currentColor = BLACK;
			// reset black's two stepped status
			for(Piece piece : pieces) {
				if(piece.color ==BLACK) {
					piece.twoStepped = false;
				}
			}
		}else {
			currentColor = WHITE;
			for(Piece piece : pieces) {
				if(piece.color == WHITE) {
					piece.twoStepped = false;
				}
			}
		}

		activeP = null;
			
		if(modeAI == 1)
			playerTurn = !playerTurn;
	}
	
	private boolean canPromote() {
		
		if(activeP.type == Type.PAWN) {
			if((currentColor == WHITE && activeP.row == 0) || (currentColor == BLACK && activeP.row ==7)) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor,9,2));
				promoPieces.add(new Knight(currentColor,9,3));
				promoPieces.add(new Bishop(currentColor,9,4));
				promoPieces.add(new Queen(currentColor,9,5));
				return true;
			}
		}
		
		return false;
	}
	
	private void promoting() {
		if(mouse.pressed) {
			for(Piece piece: promoPieces) {
				if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
					switch(piece.type) {
					case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row));break;
					case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row));break;
					case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));break;
					case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row));break;
					default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					changePlayer();
					movePiece();
				}
			}
		}
	}
	
	private boolean isIllegal(Piece king) {
		if(king.type==Type.KING) {
			for(Piece piece : simPieces) {
				if(piece !=king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean opponentCanCaptureKing() {
		
		Piece king = getKing(false);
		
		for(Piece piece : simPieces) {
			if(piece.color != king.color && piece.canMove(king.col, king.row)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isKingInCheck() {
		Piece king = getKing(true);
		
		for(Piece piece : simPieces) {
			if(piece.color == currentColor) {
				if(piece.canMove(king.col, king.row)) {
					System.out.println(piece);

					checkingP = piece;
					validSquare = false;
					return true;
				}else {
					checkingP = null;
				}
			}
		}
		
		return false;
	}
	
	private Piece getKing(boolean opponent) {
		Piece king = null;
		for(Piece piece : simPieces) {
			if(opponent) {
				if(piece.type == Type.KING && piece.color != currentColor) {
					king = piece;
				}
			}
			else {
				if(piece.type == Type.KING && piece.color == currentColor) {
					king = piece;
				}
			}
		}
		return king;
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		//Draw the board
		board.draw(g2);
		
		ArrayList<Piece> simPiecesCopy = new ArrayList<>(simPieces);
	    //Draw the pieces
	    for(Piece p : simPiecesCopy) {
	        p.draw(g2);
	    }   
		if(activeP != null) {
			if(canMove) {
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					g2.setColor(Color.red);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				}
				else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				}
				
			}
			
			activeP.draw(g2);
		}
		
		//STATUS MESSAGES
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		if(promotion) {
			g2.drawString("Promote to:", 840, 150);
			for(Piece piece: promoPieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		else {
			if(currentColor == WHITE) {
				g2.drawString("White's Turn", 840, 550);
				if(checkingP != null && checkingP.color == BLACK && validSquare == false) {
					g2.setColor(Color.red);

					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(getKing(false).col*Board.SQUARE_SIZE, getKing(false).row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				}
			}else {
				g2.drawString("Black's Turn", 840, 250);
				if(checkingP != null && checkingP.color == WHITE && validSquare == false) {
					g2.setColor(Color.red);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(getKing(false).col*Board.SQUARE_SIZE, getKing(false).row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				}
			}
		}
		
		if(gameover) {
			String s = "";
			if(currentColor == WHITE) {
				s= "White Wins";
				
			}else {
				s= "Black Wins";
			}
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.green);
			g2.drawString(s, 200, 420);
		}
		if(stalemate) {
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.lightGray);
			g2.drawString("Tie", 200, 420);
		}
		
	}
}
