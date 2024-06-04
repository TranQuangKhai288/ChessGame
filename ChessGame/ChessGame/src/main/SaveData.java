package main;

import java.util.ArrayList;

import piece.PieceData;

public class SaveData {
    String name;
    ArrayList<PieceData> pieces;
    int currentColor;
    public SaveData(String name, ArrayList<PieceData> pieces, int currentColor) {
        this.name = name;
        this.pieces = pieces;
        this.currentColor = currentColor;
    }
}