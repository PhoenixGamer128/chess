package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.CreateGameRequest;
import model.GameData;
import model.GameID;

import java.util.Collection;
import java.util.HashMap;

public class GameService {

    private final GameDAO gameDAO;
    private final UserService userService;

    public GameService(GameDAO gameDAO, UserService userService) {
        this.gameDAO = gameDAO;
        this.userService = userService;
    }

    public GameID createGame(CreateGameRequest requestedGame) throws ResponseException {
        // Validate request
        validateAuthToken(requestedGame.authToken());
        if (requestedGame.gameName() == null || requestedGame.gameName().isEmpty()) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Game name cannot be empty.");
        }

        // Find valid gameID
        HashMap<Integer, GameData> gameList = listGamesObjects();
        int gameID = 1;
        while (gameList.containsKey(gameID)) {gameID++;}

        // Create new game
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(new GameID(gameID), "", "", requestedGame.gameName(),chessGame);
        gameDAO.addGame(gameID, newGame);

        return new GameID(gameID);
    }

    public Collection<GameData> listGames(String authToken) {
        validateAuthToken(authToken);
        return gameDAO.listGames().values();
    }

    public HashMap<Integer, GameData> listGamesObjects() {
        return gameDAO.listGames();
    }

    private void validateAuthToken(String authToken) {
        if (!(userService.validAuthToken(authToken))) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
    }

    public void clearGames() {
        gameDAO.clearGames();;
    }
}
