package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {

	public Rook(int color, int col, int row) {
		super(color, col, row);
		type = Type.ROOK;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/wR");
			symbol = 'R';
		}else {
			image = getImage("/piece/bR");
			symbol = 'r';
		}
		
	}

	
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow) && (isSameSquare(targetCol, targetRow)==false))
		{
			if(targetCol == preCol || targetRow == preRow) {
				if(isValidSquare(targetCol, targetRow) && isPieceOnStraightLine(targetCol, targetRow)==false) {
					return true;
				}
			}
		}
		
		return false;
	}
}
