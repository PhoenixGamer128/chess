package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] chessBoard = new ChessPiece[8][8];;

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoard[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        boolean rowBound = position.getRow() <= 8 && position.getRow() >= 1;
        boolean columnBound = position.getColumn() <= 8 && position.getColumn() >= 1;
        if (rowBound && columnBound) {
            return chessBoard[position.getRow()-1][position.getColumn()-1];
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ChessPosition position;
        ChessPiece piece;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                position = new ChessPosition(i,j);
                piece = getPiece(position);
                if (piece != null) {
                    builder.append(getPiece(position).toString());
                } else {
                    builder.append("null");
                }
            }
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        chessBoard = new ChessPiece[8][8];
        ChessPosition position;
        ChessPiece piece;
        int row = 1;
        int col = 1;

        ChessPiece.PieceType[] pieces = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
        };

        ChessGame.TeamColor[] teams = {
                ChessGame.TeamColor.WHITE,
                ChessGame.TeamColor.BLACK
        };

        for (ChessGame.TeamColor team : teams) {
            for (ChessPiece.PieceType type : pieces) {
                position = new ChessPosition(row, col);
                piece = new ChessPiece(team,type);
                addPiece(position, piece);
                col++;
            }
            row = (team == ChessGame.TeamColor.WHITE) ? 2 : 7;
            col = 1;
            for (int i = 0; i < 8; i++) {
                position = new ChessPosition(row, col);
                piece = new ChessPiece(team, ChessPiece.PieceType.PAWN);
                addPiece(position, piece);
                col++;
            }
            row = 8;
            col = 1;
        }
    }
}
