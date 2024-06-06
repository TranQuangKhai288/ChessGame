package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class ChessEngine {

    private Process engineProcess;
    private BufferedReader processReader;
    private PrintWriter processWriter;

    public boolean startEngine(String path) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(path);
            engineProcess = processBuilder.start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new PrintWriter(new OutputStreamWriter(engineProcess.getOutputStream()), true);
        } catch (Exception e) {
        	System.out.println(e);
            return false;
        }
        return true;
    }
    

    public void sendCommand(String command) {
        processWriter.println(command);
    }
    
    public void startNewGame() {
        sendCommand("ucinewgame");
    }
    
    public void setBoardPosition(String fen) {
        sendCommand("position fen " + fen);
    }

    public String getBestMove() throws IOException {
        sendCommand("go depth 10");
        String line;
        String bestMove = null;
        
        while ((line = processReader.readLine()) != null) {
//        	System.out.println(line);
            if (line.contains("bestmove")) {
                bestMove = line.split(" ")[1];
                break;
            }
        }
        return bestMove;
    }

    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    
}
