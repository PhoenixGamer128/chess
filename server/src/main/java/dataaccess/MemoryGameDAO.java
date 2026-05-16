package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    HashMap<Integer, GameData> gameList;

    public MemoryGameDAO() {
        this.gameList = new HashMap<>();
    }

    public void addGame(int gameID, GameData game) {
        gameList.put(gameID, game);
    }

    public HashMap<Integer, GameData> listGames() {
        return gameList;
    }

    public void clearGames() {gameList = new HashMap<>();}
}
