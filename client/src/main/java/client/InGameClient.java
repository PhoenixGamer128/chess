package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.ResponseException;
import server.ServerFacade;

import static ui.ChessStyles.*;
import static ui.EscapeSequences.*;

public class InGameClient {
    ServerFacade server;
    ChessClient mainClient;

    public InGameClient(ChessClient mainClient, ServerFacade server) {
        this.server = server;
        this.mainClient = mainClient;
    }

    public String showBoard() {
        try {
            ChessBoard currentBoard = server.
                    listGames(mainClient.getAuthToken())
                    .games()
                    .get(mainClient.getCurrentGameID()-1)
                    .game()
                    .getBoard();
            if (!mainClient.getCurrentUserType().equals(ChessClient.UserType.BLACK)) {
                return printBoardWhite(currentBoard);
            } else {
                return printBoardBlack(currentBoard);
            }
        }
        catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return "Out of bounds";
        }
        catch (ResponseException ex) {
            return "Failed to get board";
        }
    }

    private String printBoardWhite(ChessBoard currentBoard) {
        return printBoardGeneric(true, currentBoard);
    }

    private String printBoardBlack(ChessBoard currentBoard) {
        return printBoardGeneric(false, currentBoard);
    }

    private String printBoardGeneric(boolean isWhite, ChessBoard currentBoard) {
        String rowCoords = "   a   b   c   d   e   f   g   h";
        String rowCoordsReverse = "   h   g   f   e   d   c   b   a";
        String rowCoordsColor = isWhite? rowCoords : rowCoordsReverse;
        char[] colCoords = {'1', '2', '3', '4', '5', '6', '7', '8'};
        int beginRow = 8;
        int beginCol = 1;
        int direction = 1;
        if (!isWhite) {
            beginRow = 1;
            beginCol = 8;
            direction = -1;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\n")
                .append(EMPTY)
                .append(SET_BG_COLOR_LIGHT_GREY)
                .append(SET_TEXT_COLOR_BLACK)
                .append(rowCoordsColor)
                .append(EMPTY)
                .append(SET_BG_COLOR_BLACK)
                .append("\n");
        for (int row = beginRow; isInRange(row); row -= direction) {
            builder.append(EMPTY);
            for (int col = beginCol; isInRange(col); col += direction) {
                if (col == beginCol) {
                    builder.append(SET_BG_COLOR_LIGHT_GREY)
                            .append(SET_TEXT_COLOR_BLACK)
                            .append(" ")
                            .append(colCoords[row-1])
                            .append(" ");
                }

                builder.append(printSquare((row + col) % 2 == 1));
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = currentBoard.getPiece(position);

                if (piece == null) {
                    builder.append(EMPTY);
                }
                else {
                    if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        builder.append(SET_TEXT_COLOR_RED);
                    }
                    else {
                        builder.append(SET_TEXT_COLOR_BLUE);
                    }
                    builder.append(renderPiece(piece));
                }

                if (col == (beginCol + 7*direction)) {
                    builder.append(SET_BG_COLOR_LIGHT_GREY)
                            .append(SET_TEXT_COLOR_BLACK)
                            .append(" ")
                            .append(colCoords[row-1])
                            .append(" ");
                }
            }
            builder.append(SET_BG_COLOR_BLACK);
            builder.append("\n");
        }
        builder.append(SET_BG_COLOR_BLACK)
                .append(EMPTY)
                .append(SET_BG_COLOR_LIGHT_GREY)
                .append(SET_TEXT_COLOR_BLACK)
                .append(rowCoordsColor)
                .append(EMPTY)
                .append(SET_BG_COLOR_BLACK)
                .append("\n");
        return builder.toString();
    }

    private boolean isInRange(int currentNum) {
        int max = Integer.max(1, 8);
        int min = Integer.min(1, 8);
        return (currentNum <= max) && (currentNum >= min);
    }

    private String renderPiece(ChessPiece piece) {
        ChessPiece.PieceType pieceType = piece.getPieceType();
        switch (pieceType) {
            case KING -> {
                if (isWhite(piece)) {return WHITE_KING;}
                return BLACK_KING;
            }
            case QUEEN -> {
                if (isWhite(piece)) {return WHITE_QUEEN;}
                return BLACK_QUEEN;
            }
            case KNIGHT -> {
                if (isWhite(piece)) {return WHITE_KNIGHT;}
                return BLACK_KNIGHT;
            }
            case ROOK -> {
                if (isWhite(piece)) {return WHITE_ROOK;}
                return BLACK_ROOK;
            }
            case BISHOP -> {
                if (isWhite(piece)) {return WHITE_BISHOP;}
                return BLACK_BISHOP;
            }
            case PAWN -> {
                if (isWhite(piece)) {return WHITE_PAWN;}
                return BLACK_PAWN;
            }
            default -> {
                return EMPTY;
            }
        }
    }

    private boolean isWhite(ChessPiece piece) {
        return piece.getTeamColor().equals(ChessGame.TeamColor.WHITE);
    }
}
