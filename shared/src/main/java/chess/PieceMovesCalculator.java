package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static chess.ChessPiece.PieceType;

public class PieceMovesCalculator {

    private final ChessPosition startPosition;
    private final int positionRow;
    private final int positionCol;
    private Set<ChessMove> pieceMoves;
    private final ChessPiece piece;
    private final ChessBoard board;

    public PieceMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
        this.board = board;
        this.startPosition = startPosition;
        positionRow = this.startPosition.getRow();
        positionCol = this.startPosition.getColumn();
        pieceMoves = new HashSet<>();
        this.piece = piece;
    }

    public Collection<ChessMove> getMoves() {
        if (piece.getPieceType() == PieceType.BISHOP) {
            BishopMovesCalculator();
        } else if (piece.getPieceType() == PieceType.ROOK) {
            RookMovesCalculator();
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            BishopMovesCalculator();
            RookMovesCalculator();
        } else if (piece.getPieceType() == PieceType.KING) {
            KingMovesCalculator();
        }
        return pieceMoves;
    }

    private void CardinalMoves(int distance, int dirRow, int dirCol) {
        while (InBoundsAxis(positionRow + (distance + 1) * dirRow)
                && InBoundsAxis(positionCol + (distance + 1) * dirCol)) {
            distance += 1;
            int newRow = positionRow + (distance * dirRow);
            int newCol = positionCol + (distance * dirCol);

            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            ChessPiece targetPiece = board.getPiece(endPosition);

            if (!(targetPiece == null)) {
                if (targetPiece.getTeamColor().equals(piece.getTeamColor())) {
                    break;
                } else {
                    pieceMoves.add(move);
                    break;
                }
            }
            pieceMoves.add(move);
        }
    }

    private boolean InBoundsAxis(int axis) {
        return (axis <= 8 && axis >= 1);
    }

    private boolean InBounds(ChessPosition position) {
        return (InBoundsAxis(position.getRow()) && InBoundsAxis(position.getColumn()));
    }

    private void BishopMovesCalculator() {
        int distance = 0;
        int dirRow = 1;
        int dirCol = 1;
        for (int i = 0; i < 4; i++) {

            CardinalMoves(distance, dirRow, dirCol);

            if (dirCol == 1 && dirRow == 1) {
                dirCol = -1;
            } else if (dirCol == -1 && dirRow == 1) {
                dirRow = -1;
            } else{
                dirCol = 1;
            }
            distance = 0;
        }
    }

    private void RookMovesCalculator() {
        int distance = 0;
        int dirRow = 0;
        int dirCol = 1;
        for (int i = 0; i < 4; i++) {

            CardinalMoves(distance, dirRow, dirCol);

            if (dirCol == 1) {
                dirCol = -1;
            } else if (dirCol == -1) {
                dirCol = 0;
                dirRow = 1;
            } else if (dirRow == 1) {
                dirRow = -1;
            }
            distance = 0;
        }
    }

    private void CheckAddMove(int row, int col, PieceType promotionPiece) {
        ChessPosition endPosition = new ChessPosition(row, col);
        if (!InBounds(endPosition)) {
            return;
        }
        ChessPiece targetPiece = board.getPiece(endPosition);
        if (targetPiece != null) {
            if (targetPiece.getTeamColor().equals(piece.getTeamColor())) {
                return;
            }
        }
        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
        pieceMoves.add(move);
    }

    private void KingMovesCalculator() {
        CheckAddMove(positionRow + 1, positionCol + 1, null);
        CheckAddMove(positionRow + 1, positionCol, null);
        CheckAddMove(positionRow + 1, positionCol - 1, null);
        CheckAddMove(positionRow, positionCol - 1, null);
        CheckAddMove(positionRow - 1, positionCol - 1, null);
        CheckAddMove(positionRow - 1, positionCol, null);
        CheckAddMove(positionRow - 1, positionCol + 1, null);
        CheckAddMove(positionRow, positionCol + 1, null);
    }
}
