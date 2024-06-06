package main;

import java.util.ArrayList;

import piece.PieceData;

public class SaveData {
    String game;
    ArrayList<PieceData> pieces;
    int currentColor;
    boolean whiteKingSide;
    boolean whiteQueenSide;
    boolean blackKingSide;
    boolean blackQueenSide;
    public SaveData(String game, ArrayList<PieceData> pieces, int currentColor, boolean whiteKingSide, boolean whiteQueenSide, boolean blackKingSide, boolean blackQueenSide) {
        this.game = game;
        this.pieces = pieces;
        this.currentColor = currentColor;
        this.blackKingSide = blackKingSide;
        this.blackQueenSide = blackQueenSide;
        this.whiteKingSide = whiteQueenSide;
        this.whiteKingSide = whiteKingSide;
    }
}