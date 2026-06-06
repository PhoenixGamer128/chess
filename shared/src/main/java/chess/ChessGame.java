package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard board;
    ChessPosition enPassantPosition;
    boolean gameResigned;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
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
        BoardStateCalculator stateCalculator = new BoardStateCalculator();
        SpecialMovesCalculator specialMovesCalculator = new SpecialMovesCalculator();
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validPieceMoves;
        Collection<ChessMove> invalidPieceMoves = new HashSet<>();

        if (piece != null) {
            validPieceMoves = piece.pieceMoves(board, startPosition);
            specialMovesCalculator.addEnPassantMoves(this, startPosition, validPieceMoves);
        } else {
            return null;
        }

        // Add invalid moves to remove
        for (ChessMove move : validPieceMoves) {
            ChessBoard boardSimulation = board.clone();

            // Castling branch
            boolean canCastle = false;
            boolean inCheck = isInCheck(piece.getTeamColor());
            if (piece.getPieceType().equals(ChessPiece.PieceType.KING) && specialMovesCalculator.isCastleMove(move)) {
                if (inCheck) {
                    invalidPieceMoves.add(move);
                } else {
                    canCastle = specialMovesCalculator.canCastle(stateCalculator, boardSimulation, move);
                }
                if (!canCastle) {
                    invalidPieceMoves.add(move);
                }
            }
            // En Passant branch
            if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)
                    && startPosition.getColumn() != move.getEndPosition().getColumn()
                    && board.getPiece(move.getEndPosition()) == null) {
                ChessPosition targetPawnPosition =
                        new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn());
                boardSimulation.addPiece(targetPawnPosition, null);
            }
            // Check general move validity
            boardSimulation.addPiece(move.getEndPosition(), piece);
            boardSimulation.addPiece(move.getStartPosition(), null);

            if (stateCalculator.isInCheckCalculator(boardSimulation, piece.getTeamColor())) {
                invalidPieceMoves.add(move);
            }
        }
        // Remove invalid moves from valid moves
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
        SpecialMovesCalculator specialMovesCalculator = new SpecialMovesCalculator();
        boolean currentEnPassantOpening = false;

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (!piece.getTeamColor().equals(teamTurn)) {
            throw new InvalidMoveException("Move made out of turn");
        }

        Collection<ChessMove> validPieceMoves = validMoves(startPosition);

        if (validPieceMoves.contains(move)) {
            // Promotion and En Passant rules
            if (!piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                enPassantPosition = new ChessPosition(0, 0);
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                // Allow En Passant for targeted Pawn
                int moveDistance = move.getEndPosition().getRow() - move.getStartPosition().getRow();
                if (moveDistance == 2 || moveDistance == -2) {
                    int targetRow = move.getStartPosition().getRow() + (moveDistance / 2);
                    int targetCol = move.getStartPosition().getColumn();
                    enPassantPosition = new ChessPosition(targetRow, targetCol);
                    currentEnPassantOpening = true;
                }
                // Execute En Passant for Pawn performing action
                int targetRow = move.getEndPosition().getRow();
                if (!move.getEndPosition().equals(enPassantPosition) && !currentEnPassantOpening) {
                    enPassantPosition = new ChessPosition(0, 0);
                }
                if (move.getStartPosition().getColumn() != move.getEndPosition().getColumn() && targetRow != 1 && targetRow != 8) {
                    board.addPiece(move.getEndPosition(), piece);
                    board.addPiece(move.getStartPosition(), null);
                    ChessPosition targetPawnPosition =
                            new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn());
                    board.addPiece(targetPawnPosition, null);
                }
                // Check pawn promotion
                if (move.getPromotionPiece() != null) {
                    ChessPiece promotionPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
                    board.addPiece(move.getEndPosition(), promotionPiece);
                } else {
                    board.addPiece(move.getEndPosition(), piece);
                }
            }
            // Check for castling
            else if (piece.getPieceType().equals(ChessPiece.PieceType.KING)
                    && piece.getCanUseSpecial()
                    && specialMovesCalculator.isCastleMove(move)) {

                ChessPosition rookPosition;
                ChessPosition newRookPosition;
                if (move.getEndPosition().getColumn() == 3) {
                    rookPosition = new ChessPosition(startPosition.getRow(), 1);
                    newRookPosition = new ChessPosition(startPosition.getRow(), 4);
                } else {
                    rookPosition = new ChessPosition(startPosition.getRow(), 8);
                    newRookPosition = new ChessPosition(startPosition.getRow(), 6);
                }

                if (board.getPiece(rookPosition).getCanUseSpecial()) {
                    board.addPiece(move.getEndPosition(), piece);
                    board.addPiece(newRookPosition, board.getPiece(rookPosition));
                    board.addPiece(rookPosition, null);
                    board.getPiece(move.getEndPosition()).setCanUseSpecial(false);
                    board.getPiece(newRookPosition).setCanUseSpecial(false);
                }

            } else {
                board.addPiece(move.getEndPosition(), piece);
                piece.setCanUseSpecial(false);
            }
            // Finally...
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
        BoardStateCalculator stateCalculator = new BoardStateCalculator();
        return stateCalculator.isInCheckCalculator(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        BoardStateCalculator stateCalculator = new BoardStateCalculator();
        boolean inCheck = isInCheck(teamColor);
        boolean noMoves = stateCalculator.cannotMove(this, board, teamColor);
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
        BoardStateCalculator stateCalculator = new BoardStateCalculator();
        return stateCalculator.isInStalemateCalculator(this, board, teamColor);
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

    public void setEnPassantPosition(ChessPosition enPassantPosition) {
        this.enPassantPosition = enPassantPosition;
    }

    public ChessPosition getEnPassantPosition() {
        return enPassantPosition;
    }

    public void setGameResigned(boolean gameResigned) {this.gameResigned = gameResigned;}

    public boolean getGameResigned() {return this.gameResigned;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn
                && Objects.equals(board, chessGame.board)
                && Objects.equals(enPassantPosition, chessGame.enPassantPosition)
                && Objects.equals(gameResigned, chessGame.gameResigned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, enPassantPosition, gameResigned);
    }
}
