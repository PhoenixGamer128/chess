package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

        // Create new game
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(null, null, null, requestedGame.gameName(),chessGame);
        int gameID = gameDAO.createGame(newGame);

        return new GameID(gameID);
    }

    public void joinGame(JoinGameRequest gameRequest) {
        // validation
        validateAuthToken(gameRequest.authToken());
        String username = userService.getUser(gameRequest.authToken()).username();
        JoinGameData gameData = gameRequest.joinGameData();
        validateJoinSyntax(gameData); // validate user input and gameID

        validateAndJoinGame(gameData, username);
    }

    private void validateJoinSyntax(JoinGameData gameData) throws ResponseException {
        // Check user input
        if (gameData == null
                || gameData.gameID() == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Bad request");

        }
    }

    private void validateAndJoinGame(JoinGameData gameToJoin, String username) throws ResponseException {
        // Check if game (still) exists
        GameData requestedGame = gameDAO.getGame(gameToJoin.gameID());
        if (requestedGame == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Bad request");
        }

        ChessGame.TeamColor requestedColor = gameToJoin.playerColor();

        if (requestedColor == null) {
            gameDAO.updateGame(requestedGame, new GameData(
                    requestedGame.gameID(),
                    requestedGame.whiteUsername(),
                    requestedGame.blackUsername(),
                    requestedGame.gameName(),
                    requestedGame.game()
            ));
        }
        else if (requestedColor == ChessGame.TeamColor.WHITE && requestedGame.whiteUsername() == null) {
            gameDAO.updateGame(requestedGame, new GameData(
                    requestedGame.gameID(),
                    username,
                    requestedGame.blackUsername(),
                    requestedGame.gameName(),
                    requestedGame.game()
            ));
        }
        else if (requestedColor == ChessGame.TeamColor.BLACK && requestedGame.blackUsername() == null) {
            gameDAO.updateGame(requestedGame, new GameData(
                    requestedGame.gameID(),
                    requestedGame.whiteUsername(),
                    username,
                    requestedGame.gameName(),
                    requestedGame.game()
            ));
        } else {
            throw new ResponseException(ResponseException.Code.AlreadyTaken, "Error: Color already taken");
        }
    }

    public GameList listGames(String authToken) {
        validateAuthToken(authToken);
        return new GameList(gameDAO.listGames());
    }

    public ArrayList<GameData> listGamesObjects() {
        return gameDAO.listGames();
    }

    private void validateAuthToken(String authToken) {
        if (!(userService.validAuthToken(authToken))) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
    }

    public void clearGames() {
        gameDAO.clearGames();
    }
}
