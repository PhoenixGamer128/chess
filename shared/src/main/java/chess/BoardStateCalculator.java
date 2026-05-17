package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardStateCalculator {

    public BoardStateCalculator() {

    }

    public boolean isInCheckCalculator(ChessBoard board, ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor enemyColor = teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        ChessPosition kingPosition = findKing(board, teamColor);
        List<ChessPosition> enemyPiecePositions = searchPieces(board, null, enemyColor);

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

    private List<ChessPosition> searchPieces(ChessBoard board,
                                             ChessPiece.PieceType targetPieceType,
                                             ChessGame.TeamColor color) {

        List<ChessPosition> pieces = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor().equals(color)) {
                    if (targetPieceType == null) {
                        pieces.add(position);
                    }
                    else if (targetPieceType.equals(piece.getPieceType())) {
                        pieces.add(position);
                    }
                }
            }
        }
        return pieces;
    }

    private ChessPosition findKing(ChessBoard board, ChessGame.TeamColor color) {
        List<ChessPosition> kingPositionList = searchPieces(board, ChessPiece.PieceType.KING, color);
        if (!kingPositionList.isEmpty()) {
            return kingPositionList.getFirst();
        }
        return null;
    }

    public boolean isInStalemateCalculator(ChessGame game, ChessBoard board, ChessGame.TeamColor teamColor) {
        boolean inCheck = isInCheckCalculator(board, teamColor);

        if (!inCheck) {
            return cannotMove(game, board, teamColor);
        }
        return false;
    }

    public boolean cannotMove(ChessGame game, ChessBoard board, ChessGame.TeamColor teamColor) {
        List<ChessPosition> allTeamPositions = searchPieces(board, null, teamColor);
        int totalMoves = 0;
        for (ChessPosition piecePosition : allTeamPositions) {
            Collection<ChessMove> possibleMoves = game.validMoves(piecePosition);
            if (!possibleMoves.isEmpty()) {
                totalMoves += possibleMoves.size();
            }
        }
        return totalMoves == 0;
    }
}
