package client;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.ResponseException;
import server.ServerFacade;

public class InGameClient {
    ServerFacade server;
    ChessClient mainClient;

    public InGameClient(ChessClient mainClient, ServerFacade server) {
        this.server = server;
        this.mainClient = mainClient;
    }

    public String showBoard() {
        try {
            ChessGame currentGame = server.
                    listGames(mainClient.getAuthToken())
                    .games()
                    .get(mainClient.getCurrentGameID())
                    .game();
            return showBoardWhite(currentGame);
        }
        catch (ResponseException ex) {
            return "Failed to get board";
        }
    }

    private String showBoardWhite(ChessGame currentGame) {
        ChessBoard board = currentGame.getBoard();
        return "CHESS BOARD";
    }
}
