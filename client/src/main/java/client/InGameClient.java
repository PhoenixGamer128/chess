package client;

import chess.*;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import model.ResponseException;
import server.ServerFacade;
import websocket.commands.UserGameCommand;

import java.util.Arrays;
import java.util.Scanner;

import static ui.ChessStyles.*;
import static ui.EscapeSequences.*;

public class InGameClient {
    ServerFacade server;
    ChessClient mainClient;
    WebSocketFacade ws;
    ChessGame currentBoardState;

    private final String[] letterCords = {"a","b","c","d","e","f","g","h"};

    public InGameClient(ChessClient mainClient, ServerFacade server) {
        this.server = server;
        this.mainClient = mainClient;
        this.ws = mainClient.getWs();
    }

    public void enterGame(String authToken, Integer gameID) {
        UserGameCommand request = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        ws.sendRequest(request);
    }

    public void exitGame(String authToken, Integer gameID) {
        UserGameCommand request = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        ws.sendRequest(request);
    }

    public void resignGame(String authToken, Integer gameID) {
        UserGameCommand request = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        ws.sendRequest(request);
    }

    public String makeMove(String[] params) {
        if (params.length == 3 && params[0].equals("move")) {
            String[] startArray = params[0].split("");
            String[] endArray = params[1].split("");
            if (isValidCoords(startArray, endArray)) {
                ChessMove move = parseMove(startArray, endArray);
                UserGameCommand request =
                        new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE,
                                mainClient.getAuthToken(),
                                mainClient.getCurrentGameID());
                request.setMove(move);
                ws.sendRequest(request);
                return "";
            }
        }
        return "Make a move with \"Make move <Position> <Position>\", ex: Make move e2 e4";
    }

    private ChessMove parseMove(String[] startArray, String[] endArray) {
        int startCol = convertStringCoordToInt(startArray[0]);
        int startRow = Integer.parseInt(startArray[1]);
        int endCol = convertStringCoordToInt(endArray[0]);
        int endRow = Integer.parseInt(endArray[1]);
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);

        ChessPiece.PieceType promotionPiece = null;
        if (isPawnPromotion(startPosition, endPosition)) {
            promotionPiece = promotePawn();
        }
        return new ChessMove(startPosition, endPosition, promotionPiece);
    }

    private ChessPiece.PieceType promotePawn() {
        String promotionMessage = "Please choose a piece to promote to: Queen / Knight / Rook / Bishop";
        Scanner scanner = new Scanner(System.in);
        ChessPiece.PieceType promotionPiece = null;
        String input;
        while (promotionPiece == null) {
            input = scanner.nextLine().toLowerCase();
            switch (input) {
                case "queen" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                case "knight" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                case "rook" -> promotionPiece = ChessPiece.PieceType.ROOK;
                case "bishop" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                default -> {
                    System.out.println(SET_TEXT_COLOR_RED + promotionMessage + SET_TEXT_COLOR_LIGHT_GREY);
                    System.out.print(">>> ");
                }
            }
        }
        return promotionPiece;
    }

    private int convertStringCoordToInt(String s) {
        for (int i = 0; i < 8; i++) {
            if (letterCords[i].equals(s)) {return i+1;}
        }
        return -1;
    }

    private boolean isValidCoords(String[] startArray, String[] endArray) {
        return Arrays.asList(letterCords).contains(startArray[0])
                && Arrays.asList(letterCords).contains(endArray[0])
                && isInRange(startArray[1])
                && isInRange(endArray[1]);
    }

    private boolean isInRange(String s) {
        try {
            int position = Integer.parseInt(s);
            return (position >= 1 && position <= 8);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPawnPromotion(ChessPosition startPosition, ChessPosition endPosition) {
        ChessPiece piece = currentBoardState.getBoard().getPiece(startPosition);
        if (piece != null && piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) && endPosition.getRow() == 8) {
                return true;
            }
            else return piece.getTeamColor().equals(ChessGame.TeamColor.BLACK) && endPosition.getRow() == 1;
        }
        return false;
    }

    public String showBoard(ChessGame boardState) {
        this.currentBoardState = boardState;
        try {
            ChessBoard currentBoard = boardState.getBoard();
            if (mainClient.getCurrentUserType().equals(ChessClient.UserType.WHITE)) {
                return printBoardWhite(currentBoard);
            }
            else if (mainClient.getCurrentUserType().equals(ChessClient.UserType.OBSERVER)) {
                return printBoardWhite(new ChessGame().getBoard());
            }
            else {
                return printBoardBlack(currentBoard);
            }
        }
        catch (IndexOutOfBoundsException ex) {
            // ex.printStackTrace();
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
