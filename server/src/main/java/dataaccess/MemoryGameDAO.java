package dataaccess;

import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;
//import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    ArrayList<GameData> gameList;

    public MemoryGameDAO() {
        this.gameList = new ArrayList<>();
    }

    public void createGame(int gameID, GameData game) {
        gameList.add(game);
    }

    public GameData getGame(int gameID) {
        for (GameData game : gameList) {
            if (game.gameID() == gameID) return game;
        }
        return null;
    }

    public ArrayList<GameData> listGames() {
        return gameList;
    }

    public void updateGame(GameData oldGame, GameData newGame) {
        gameList.remove(oldGame);
        gameList.add(newGame);
    }

    public void clearGames() {gameList = new ArrayList<>();}
}
