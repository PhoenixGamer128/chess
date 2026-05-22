package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements SQLDataAccess, GameDAO{
    public void createGame(int gameID, GameData game) {

    }

    public GameData getGame(int gameID) {
        return null;
    }

    public ArrayList<GameData> listGames() {
        return null;
    }

    public void updateGame(GameData oldGame, GameData gameData) {

    }

    public void clearGames() {

    }
}
