package client;

import dataaccess.ResponseException;
import model.CreateGameRequest;
import model.GameData;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.HashMap;

import static client.ChessClient.cmdErrorMessage;

public class PostLoginClient {
    ServerFacade server;
    ChessClient mainClient;

    public PostLoginClient(ChessClient mainClient, ServerFacade server) {
        this.server = server;
        this.mainClient = mainClient;
    }

    public String createGame(String[] params) throws ResponseException {
        if (params.length > 1 && params[0].equals("game")) {
            CreateGameRequest gameRequest = new CreateGameRequest(mainClient.getAuthToken(), params[1]);
            server.createGame(gameRequest);
            return "Created game " + params[1];
        } else {
            return cmdErrorMessage();
        }
    }

    public String listGames(String[] params) {
        if (params.length > 0 && params[0].equals("games")) {
            StringBuilder builder = new StringBuilder();
            HashMap<Integer, Integer> gameIDList = new HashMap<>();


            ArrayList<GameData> gameList = server.listGames(mainClient.getAuthToken()).games();
            for (int i = 1; i <= gameList.size(); i++) {
                GameData game = gameList.get(i-1);
                builder.append("Game Number: ")
                        .append(i)
                        .append(" | Player white: ")
                        .append(game.whiteUsername())
                        .append(" | Player black: ")
                        .append(game.blackUsername())
                        .append("\n");
                gameIDList.put(i, game.gameID());
            }
            mainClient.setGameIDList(gameIDList);
            return builder.toString();
        } else {
            return cmdErrorMessage();
        }
    }

    public String playGame(String[] params) {
        return "Not implemented";
    }

    public String observeGame(String[] params) {
        return "Not implemented";
    }
}
