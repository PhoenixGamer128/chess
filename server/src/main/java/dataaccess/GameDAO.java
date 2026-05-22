package dataaccess;

import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface GameDAO {
    int createGame(GameData game);

    GameData getGame(int gameID);

    ArrayList<GameData> listGames();

    void updateGame(GameData oldGame, GameData gameData);

    void clearGames();
}
