package chess;

import java.util.Collection;

import static java.lang.Math.abs;

public class SpecialMovesCalculator {
    public boolean isCastleMove(ChessMove move) {
        int castleDirection = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
        return castleDirection == 2 || castleDirection == -2;
    }

    public boolean canCastle(BoardStateCalculator stateCalculator, ChessBoard boardSimulation, ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        // Simply check the king is in the right position (column)
        ChessPiece piece = boardSimulation.getPiece(startPosition);
        int castleDirection = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();

        // Check if move is a castling move
        if (isCastleMove(move)) {
            // Queen/Kingside Rook
            int rookCol = castleDirection > 0 ? 8 : 1;
            ChessPosition rookPosition = new ChessPosition(startPosition.getRow(), rookCol);
            ChessPiece rookPiece = boardSimulation.getPiece(rookPosition);
            // Check if Rook has moved (already determined if King has moved)
            if (!rookPiece.getCanUseSpecial()) {
                return false;
            }
            // Simulate mid-move check
            int midColumn = startPosition.getColumn() + (castleDirection / 2);
            ChessPosition midPosition = new ChessPosition(startPosition.getRow(), midColumn);
            boardSimulation.addPiece(midPosition, piece);
            boardSimulation.addPiece(startPosition, null);
            if (stateCalculator.IsInCheckCalculator(boardSimulation, piece.getTeamColor())) {
                return false;
            }
            // Simulate post-move check
            else {
                boardSimulation.addPiece(move.getEndPosition(), piece);
                boardSimulation.addPiece(midPosition, null);
                return !stateCalculator.IsInCheckCalculator(boardSimulation, piece.getTeamColor());
            }
        }
        return false;
    }
}
