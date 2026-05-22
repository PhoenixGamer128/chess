package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {
        configureDatabase();
    }

    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS authTokens (
            authToken varchar(64) NOT NULL,
            username varchar(256) NOT NULL,
            PRIMARY KEY (authToken)
            )
            """;

    public AuthData createAuth(UserData requestedUser) throws ResponseException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,authToken);
                preparedStatement.setString(2, requestedUser.username());
                preparedStatement.executeUpdate();

                return new AuthData(authToken, requestedUser.username());
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess,
                    String.format("Unable to add authToken: %s", ex.getMessage()));

        }
    }

    public AuthData getAuth(String authToken) throws ResponseException {
        var statement = "SELECT authToken, username FROM authTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement((statement))) {
                preparedStatement.setString(1,authToken);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet != null && resultSet.next()) {
                        return new AuthData(resultSet.getString("authToken"),
                                resultSet.getString("username"));
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess,
                    String.format("Unable to add authToken: %s", ex.getMessage()));
        }
    }

    public void deleteAuth(AuthData authData) throws ResponseException {

    }

    public void clearAuths() throws ResponseException {
        var statement = "TRUNCATE TABLE authTokens";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess,
                    String.format("Unable to add authToken: %s", ex.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess,
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
