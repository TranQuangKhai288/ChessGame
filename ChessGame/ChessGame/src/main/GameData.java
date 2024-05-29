package main;

import java.util.ArrayList;

import piece.Piece;

class GameData {
    private ArrayList<Piece> pieces;

    public GameData(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }


    public ArrayList<Piece> getPieces() {
        return pieces;
    }
}