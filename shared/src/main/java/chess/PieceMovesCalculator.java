package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static chess.ChessPiece.PieceType;

public class PieceMovesCalculator {

    private final ChessPosition startPosition;
    private final int positionRow;
    private final int positionCol;
    private final ChessPiece.PieceType type;
    private Set<ChessMove> pieceMoves;

    public PieceMovesCalculator(ChessPosition startPosition, ChessPiece.PieceType type) {
        this.startPosition = startPosition;
        positionRow = this.startPosition.getRow();
        positionCol = this.startPosition.getColumn();
        pieceMoves = new HashSet<>();
        this.type = type;
    }

    public Collection<ChessMove> getMoves() {
        if (type == PieceType.BISHOP) {
            return BishopMovesCalculator();
        }
        else {
            return BishopMovesCalculator();
        }
    }

    private Collection<ChessMove> BishopMovesCalculator() {
        int distance = 0;
        int dirCol = 1;
        int dirRow = 1;
        for (int i = 0; i < 4; i++) {

            while (((positionRow + (distance + 1) * dirRow) <= 8 && (positionRow + (distance + 1) * dirRow) >= 1)
                && ((positionCol + (distance + 1) * dirCol) <= 8 && (positionCol + (distance + 1) * dirCol) >= 1)) {
                distance += 1;
                int newRow = positionRow + (distance * dirRow);
                int newCol = positionCol + (distance * dirCol);
                ChessPosition endPosition = new ChessPosition(newRow, newCol);
                ChessMove move = new ChessMove(startPosition, endPosition, null);
                //if (!PieceBlocked()) // Implement a blocked piece
                pieceMoves.add(move);
            }
            if (dirCol == 1 && dirRow == 1) {
                dirCol = -1;
            }
            else if (dirCol == -1 && dirRow == 1) {
                dirRow = -1;
            }
            else{
                dirCol = 1;
            }
            distance = 0;
        }
        return pieceMoves;
    }

    private void Capturable () {}
}
