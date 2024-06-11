package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.PAWN;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wP");
			symbol = 'P';
		}else {
			image = getImage("/piece/bP");
			symbol = 'p';
		}
		
	}
	public Pawn(int color, int col, int row, boolean moved, boolean twoStepped) {
		super(color, col, row, moved, twoStepped);
		type = Type.PAWN;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wP");
			symbol = 'P';
		}else {
			image = getImage("/piece/bP");
			symbol = 'p';
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
	    int dx = Math.abs(targetCol - preCol);
	    int dy = Math.abs(targetRow - preRow);

	    if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
	        //Define the move value base on its color
	    	int moveValue;
	    	if(color==GamePanel.WHITE) {
	    		moveValue = -1;
	    	}else {
	    		moveValue = 1;
	    	}
	    	
	    	//check the hitting piece
	    	hittingP = getHittingP(targetCol, targetRow);
	    	// 1 square movement
	    	if(targetCol==preCol && targetRow == preRow + moveValue && hittingP == null) {
	    		return true;
	    	}
	    	// 2 square movement
	    	if(targetCol==preCol && targetRow == preRow  + moveValue* 2 && hittingP == null &&
	    			moved == false && isPieceOnStraightLine(targetCol, targetRow)==false
	    			) {
	    		return true;
	    	}
	    	
	    	//cross movement and hit a piece
	    	if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow+moveValue && hittingP != null && 
	    			hittingP.color!=color
	    			)
	    	{
	    		return true;
	    	}
	    	
	    	//en passant
	    	if(Math.abs(targetCol-preCol)== 1 && targetRow == preRow + moveValue) {
	    		for(Piece piece : GamePanel.simPieces) {
	    			if(piece.col == targetCol && piece.row == preRow && piece.twoStepped==true) {
	    				hittingP = piece;
	    				return true;
	    			}
	    		}
	    	}
	    	
	    }	

	    return false;
	}

}
