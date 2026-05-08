package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] chessBoard;

    public ChessBoard() {
        chessBoard = new ChessPiece[8][8];
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
        if (position.getRow() < 1
                || position.getRow() > 8
                || position.getColumn() < 1
                || position.getColumn() > 8) {
            return null;
        }
        return chessBoard[position.getRow() - 1][position.getColumn() - 1];

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece.PieceType[] chessSetup = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        ChessGame.TeamColor[] chessSetupSide = {
                ChessGame.TeamColor.WHITE,
                ChessGame.TeamColor.BLACK
        };

        for (ChessGame.TeamColor color : chessSetupSide) {
            int row = color == ChessGame.TeamColor.WHITE ? 1 : 8;
            int col = 1;
            for (ChessPiece.PieceType type : chessSetup) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = new ChessPiece(color, type);
                addPiece(position, piece);
                col++;
            }
            row = color == ChessGame.TeamColor.WHITE ? 2 : 7;
            col = 1;
            for (int i = 0; i < 8; i++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                addPiece(position, piece);
                col++;
            }
        }
    }
}
