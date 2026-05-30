package client;

import dataaccess.ResponseException;
import model.CreateGameRequest;
import server.ServerFacade;

import java.util.Objects;

import static client.ChessClient.cmdErrorMessage;

public class PostLoginClient {
    ServerFacade server;
    ChessClient mainClient;

    public PostLoginClient(ChessClient mainClient, ServerFacade server) {
        this.server = server;
        this.mainClient = mainClient;
    }

    public String createGame(String[] params) throws ResponseException {
        if (Objects.equals(params[0], "game")) {
            CreateGameRequest gameRequest = new CreateGameRequest(mainClient.getAuthToken(), params[1]);
            server.createGame(gameRequest);
            return "Created game " + params[1];
        } else {
            return cmdErrorMessage();
        }
    }

    public String listGames(String[] params) {
        return "Not implemented";
    }

    public String playGame(String[] params) {
        return "Not implemented";
    }

    public String observeGame(String[] params) {
        return "Not implemented";
    }
}
