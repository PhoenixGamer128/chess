package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.ResponseException;
import model.*;

import java.util.ArrayList;

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

        updateGameData(username, gameData.gameID(), gameData.playerColor());
    }

    private void validateJoinSyntax(JoinGameData gameData) throws ResponseException {
        // Check user input
        if (gameData == null
                || gameData.playerColor() == null
                || gameData.gameID() == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Bad request");

        }
    }

    private void updateGameData(String username,
                                Integer gameID,
                                ChessGame.TeamColor targetColor) throws ResponseException{
        GameData game = gameDAO.getGame(gameID);
        String targetWhite = game.whiteUsername();
        String targetBlack = game.blackUsername();
        if (targetColor != null) {
            if (targetColor.equals(ChessGame.TeamColor.WHITE) && game.whiteUsername() == null) {
                targetWhite = username;
            } else if (targetColor.equals(ChessGame.TeamColor.BLACK) && game.blackUsername() == null) {
                targetBlack = username;
            } else {
                throw new ResponseException(ResponseException.Code.AlreadyTaken, "Error: Color already taken");
            }
        }
        else {
            if (game.whiteUsername().equals(username)) {
                targetWhite = null;
            } else if (game.blackUsername().equals(username)) {
                targetBlack = null;
            }
        }
        updateGameNames(game, targetWhite, targetBlack);
    }

    private void updateGameNames(GameData game, String targetWhite, String targetBlack) {
        gameDAO.updateGame(game.gameID(), new GameData(
                game.gameID(),
                targetWhite,
                targetBlack,
                game.gameName(),
                game.game()
        ));
    }

    public void updateBoard(Integer gameID, ChessGame game) {
        GameData gameData = gameDAO.getGame(gameID);
        GameData newGameData =
                new GameData(gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game);
        gameDAO.updateGame(gameID, newGameData);
    }

    public void leaveGame(String authToken, Integer gameID) {
        validateAuthToken(authToken);
        String username = userService.getUser(authToken).username();
        updateGameData(username, gameID, null);
    }

    public GameList listGames(String authToken) {
        validateAuthToken(authToken);
        return new GameList(gameDAO.listGames());
    }

    public ArrayList<GameData> listGamesObjects() {
        return gameDAO.listGames();
    }

    public GameData getGame(String authToken, Integer gameID) {
        validateAuthToken(authToken);
        return gameDAO.getGame(gameID);
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
