package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece {

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		type = Type.BISHOP;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wB");
			symbol = 'B';
		}else {
			image = getImage("/piece/bB");
			symbol = 'b';
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
		}
		
		return false;
	}
	
	
}
