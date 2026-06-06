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

    public int createGame(GameData game) {
        gameList.add(game);
        return game.gameID();
    }

    public GameData getGame(int gameID) {
        for (GameData game : gameList) {
            if (game.gameID() == gameID) {return game;}
        }
        return null;
    }

    public ArrayList<GameData> listGames() {
        return gameList;
    }

    public void updateGame(Integer gameID, GameData gameData) {}

    public void updateGame(GameData oldGame, GameData newGame) {
        gameList.remove(oldGame);
        gameList.add(newGame);
    }

    public void clearGames() {gameList = new ArrayList<>();}
}
