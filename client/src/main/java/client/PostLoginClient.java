package client;

import chess.ChessGame;
import model.ResponseException;
import model.CreateGameRequest;
import model.GameData;
import model.JoinGameData;
import model.JoinGameRequest;
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

    public String listGames(String[] params) throws ResponseException {
        if (params.length > 0 && params[0].equals("games")) {
            StringBuilder builder = new StringBuilder();
            HashMap<Integer, Integer> gameIDList = new HashMap<>();


            ArrayList<GameData> gameList = server.listGames(mainClient.getAuthToken()).games();
            for (int i = 1; i <= gameList.size(); i++) {
                GameData game = gameList.get(i-1);
                String whiteUsername = game.whiteUsername() == null ? "[  ]" : game.blackUsername();
                String blackUsername = game.blackUsername() == null ? "[  ]" : game.blackUsername();

                builder.append("Game Number: ")
                        .append(i)
                        .append(" | Name: ")
                        .append(game.gameName())
                        .append(" | Player white: ")
                        .append(whiteUsername)
                        .append(" | Player black: ")
                        .append(blackUsername)
                        .append("\n");
                gameIDList.put(i, game.gameID());
            }
            mainClient.setGameIDList(gameIDList);
            return builder.toString();
        } else {
            return cmdErrorMessage();
        }
    }

    public String playGame(String[] params) throws ResponseException {
        if (mainClient.getGameIDList().isEmpty()) {
            return "Choose a game from an ID using \"List Games\"!";
        }
        if (params.length == 3 && params[0].equals("game")) {
            try {
                int requestedID = Integer.parseInt(params[1]);
                var gameID = mainClient.getGameIDList().get(requestedID);

                if (gameID == null) {
                    return "Game does not exist";
                }

                String requestedColorString = params[2].toLowerCase();
                if (!(requestedColorString.equals("white") || requestedColorString.equals("black"))) {
                    return "Please input \"White\" or \"Black\" as a color to play as";
                }

                ChessGame.TeamColor requestedColor = requestedColorString.equals("white") ?
                        ChessGame.TeamColor.WHITE :
                        ChessGame.TeamColor.BLACK;

                // Make request
                JoinGameData joinData = new JoinGameData(requestedColor, gameID);
                JoinGameRequest joinRequest = new JoinGameRequest(mainClient.getAuthToken(), joinData);
                server.joinGame(joinRequest);

                // Update user info to match request
                ChessClient.UserType userType = requestedColor.equals(ChessGame.TeamColor.WHITE) ?
                        ChessClient.UserType.WHITE :
                        ChessClient.UserType.BLACK;
                mainClient.setState(ChessClient.State.INGAME);
                mainClient.setCurrentGameID(gameID);
                mainClient.setCurrentUserType(userType);
                return mainClient.printBoard();
            }
            catch (NumberFormatException ex) {
                return "Please input an integer";
            }
        } else {
            return "Please input a game number and team color:\nPlay Game <Game number> <White/Black>";
        }
    }

    public String observeGame(String[] params) {
        if (mainClient.getGameIDList().isEmpty()) {
            return "Choose a game from an ID using \"List Games\"!";
        }
        if (params.length == 2 && params[0].equals("game")) {
            try {
                int requestedID = Integer.parseInt(params[1]);
                var gameID = mainClient.getGameIDList().get(requestedID);

                if (gameID == null) {
                    return "Game does not exist";
                }

                // Update user info to match request
                ChessClient.UserType userType = ChessClient.UserType.OBSERVER;
                mainClient.setState(ChessClient.State.INGAME);
                mainClient.setCurrentGameID(gameID);
                mainClient.setCurrentUserType(userType);
                return mainClient.printBoard();
            }
            catch (NumberFormatException ex) {
                return "Please input an integer";
            }
        } else {
            return "Please input a game number and team color:\nObserve Game <Game number>";
        }
    }
}
