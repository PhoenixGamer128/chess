package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.ResponseException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements SQLDataAccess, GameDAO{

    public SQLGameDAO() {
        String createStatement = """
                CREATE TABLE IF NOT EXISTS games (
                id INT NOT NULL AUTO_INCREMENT,
                whiteUsername varchar(256),
                blackUsername varchar(256),
                gameName varchar(128) NOT NULL,
                game JSON,
                PRIMARY KEY (id)
                )
                """;
        configureDatabase(createStatement);
    }

    public int createGame(GameData game) {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String chessGame = new Gson().toJson(game.game());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, chessGame);
                preparedStatement.executeUpdate();

                ResultSet resultGame = preparedStatement.getGeneratedKeys();
                if (resultGame.next()) {
                    return resultGame.getInt(1);
                }
                return 0;
            }
        }
        catch (SQLException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

//    public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
//    }

    public GameData getGame(int gameID) {
        var statement = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.executeQuery();

                ResultSet resultGame = preparedStatement.getResultSet();

                if (resultGame.next()) {
                    return parseGame(resultGame);
                }
                return null;
            }
        }
        catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private GameData parseGame(ResultSet resultGameSet) throws SQLException {
        int thisGameID = resultGameSet.getInt("id");
        String thisWhiteUsername = resultGameSet.getString("whiteUsername");
        String thisBlackUsername = resultGameSet.getString("blackUsername");
        String thisGameName = resultGameSet.getString("gameName");
        String gameJson = resultGameSet.getString("game");
        ChessGame thisGame = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(
                thisGameID,
                thisWhiteUsername,
                thisBlackUsername,
                thisGameName,
                thisGame
        );
    }

    public ArrayList<GameData> listGames() {
        var statement = "SELECT * FROM games";
        ArrayList<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
                ResultSet resultGamesList = preparedStatement.getResultSet();

                while (resultGamesList.next()) {
                    games.add(parseGame(resultGamesList));
                }

                return games;
            }
        }
        catch (SQLException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void updateGame(Integer gameID, GameData newGame) {
        var statement = """ 
                UPDATE games SET
                whiteUsername = ?,
                blackUsername = ?,
                game = ?
                WHERE id = ?
                """;

        String newGameJson = new Gson().toJson(newGame.game());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, newGame.whiteUsername());
                preparedStatement.setString(2, newGame.blackUsername());
                preparedStatement.setString(3, newGameJson);
                preparedStatement.setInt(4, gameID);
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void clearGames() {
        clearTable("games");
    }
}