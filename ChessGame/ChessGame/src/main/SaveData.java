package main;

import java.util.ArrayList;

import piece.PieceData;

public class SaveData {
    String name;
    ArrayList<PieceData> pieces;

    public SaveData(String name, ArrayList<PieceData> pieces) {
        this.name = name;
        this.pieces = pieces;
    }
}