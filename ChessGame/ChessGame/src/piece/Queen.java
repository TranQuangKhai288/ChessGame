package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece {

	public Queen(int color, int col, int row) {
		super(color, col, row);
		type = Type.QUEEN;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wQ");
		}else {
			image = getImage("/piece/bQ");
		}
		
	}

public boolean canMove(int targetCol, int targetRow) {
	int dx = Math.abs(targetCol - preCol);
    int dy = Math.abs(targetRow - preRow);
		if(isWithinBoard(targetCol, targetRow) && (isSameSquare(targetCol, targetRow)==false))
		{
			if(dx == dy) {
				if(isValidSquare(targetCol, targetRow) && isPieceOnCrossLine(targetCol, targetRow)==false) {
					return true;
				}
			}		
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && isPieceOnStraightLine(targetCol, targetRow)==false) {
					return true;
				}
			}
		}
		
		return false;
	}

}
