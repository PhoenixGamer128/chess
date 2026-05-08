package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard board;
    BoardStateCalculator stateCalculator;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        stateCalculator = new BoardStateCalculator();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validPieceMoves;
        Collection<ChessMove> invalidPieceMoves = new HashSet<>();
        if (piece != null) {
            validPieceMoves = piece.pieceMoves(board, startPosition);
        } else {
            return null;
        }
        for (ChessMove move : validPieceMoves) {
            ChessBoard boardSimulation = board.clone();

            boardSimulation.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            boardSimulation.addPiece(move.getStartPosition(), null);

            if (stateCalculator.IsInCheckCalculator(boardSimulation, teamTurn)) {
                invalidPieceMoves.add(move);
            }
        }
        for (ChessMove move : invalidPieceMoves) {
            validPieceMoves.remove(move);
        }
        return validPieceMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (!piece.getTeamColor().equals(teamTurn)) {
            throw new InvalidMoveException("Move made out of turn");
        }

        Collection<ChessMove> validPieceMoves = validMoves(startPosition);

        if (validPieceMoves.contains(move)) {
            // Check if piece is a pawn being promoted
            if (move.getPromotionPiece() != null) {
                ChessPiece promotionPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotionPiece);
            } else {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));

            }
            board.addPiece(move.getStartPosition(), null);
            teamTurn = teamTurn.equals(TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            return;
        }

        throw new InvalidMoveException("Cannot make this move");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return stateCalculator.IsInCheckCalculator(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean inCheck = isInCheck(teamColor);
        boolean noMoves = stateCalculator.CannotMove(this, board, teamColor);
        return inCheck && noMoves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return stateCalculator.IsInStalemateCalculator(this, board, teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
