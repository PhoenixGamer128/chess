package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece{

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;
    boolean canUseSpecial;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        canUseSpecial = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator = new PieceMovesCalculator();
        calculator.begin(board, myPosition);
        return calculator.calculateMoves();
    }

    public void initializeSpecial(ChessPosition startPosition) {
        int expectedRow;
        int expectedCol;
        ChessPosition expectedPosition;
        if (type.equals(PieceType.KING)) {
            expectedRow = pieceColor.equals(ChessGame.TeamColor.WHITE) ? 1 : 8;
            expectedCol = 5;
            expectedPosition = new ChessPosition(expectedRow, expectedCol);
            if (startPosition.equals(expectedPosition)) {
                setCanUseSpecial(true);
            }
        } else if (type.equals(PieceType.ROOK)) {
            expectedRow = pieceColor.equals(ChessGame.TeamColor.WHITE) ? 1 : 8;
            expectedPosition = new ChessPosition(expectedRow, 1);
            if (startPosition.equals(expectedPosition)) {
                setCanUseSpecial(true);
            }
            expectedPosition = new ChessPosition(expectedRow, 8);
            if (startPosition.equals(expectedPosition)) {
                setCanUseSpecial(true);
            }
        }
    }

    public boolean getCanUseSpecial() {
        return canUseSpecial;
    }

    public void setCanUseSpecial(boolean availability) {
        canUseSpecial = availability;
    }

    @Override
    public String toString() {
        String special = canUseSpecial ? "*" : "";
        if (pieceColor.equals(ChessGame.TeamColor.BLACK)) {
            return String.format("%s%s", type, special).toLowerCase();
        }
        return String.format("%s%s", type, special);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return canUseSpecial == that.canUseSpecial && pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, canUseSpecial);
    }
}
