package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {
    void addGame(int gameID, GameData game);

    HashMap<Integer, GameData> listGames();

    void clearGames();
}
