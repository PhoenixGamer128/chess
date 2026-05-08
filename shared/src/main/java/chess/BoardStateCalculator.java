package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardStateCalculator {

    public BoardStateCalculator() {

    }

    public boolean IsInCheckCalculator(ChessBoard board, ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor enemyColor = teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        ChessPosition kingPosition;
        List<ChessPosition> enemyPiecePositions;

        kingPosition = SearchPieces(board, ChessPiece.PieceType.KING, teamColor).getFirst();
        enemyPiecePositions = SearchPieces(board, null, enemyColor);

        ChessPiece myKing = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        for (ChessPosition enemyPosition : enemyPiecePositions) {
            Collection<ChessMove> enemyMoves = myKing.pieceMoves(board, enemyPosition);
            for (ChessMove enemyMove : enemyMoves) {
                if (enemyMove.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<ChessPosition> SearchPieces(ChessBoard board, ChessPiece.PieceType targetPieceType, ChessGame.TeamColor color) {
        List<ChessPosition> pieces = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor().equals(color)) {
                        if (targetPieceType == null) {
                            pieces.add(position);
                        } else if (targetPieceType.equals(piece.getPieceType())) {
                            pieces.add(position);
                        }
                    }
                }
            }
        }
        return pieces;
    }
}
