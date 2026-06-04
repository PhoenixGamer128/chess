package dataaccess;

import model.AuthData;
import model.ResponseException;
import model.UserData;

import java.sql.*;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO, SQLDataAccess {

    public SQLAuthDAO() {
        String createStatement = """
                CREATE TABLE IF NOT EXISTS authTokens (
                authToken varchar(64) NOT NULL,
                username varchar(256) NOT NULL,
                PRIMARY KEY (authToken)
                )
                """;
        configureDatabase(createStatement);
    }

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
            throw new ResponseException(ResponseException.Code.ServerError,
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
                        return new AuthData(null, null);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to add authToken: %s", ex.getMessage()));
        }
    }

    public void deleteAuth(AuthData authData) throws ResponseException {
        var statement = "DELETE FROM authTokens WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to delete authToken: %s", ex.getMessage()));
        }
    }

    public void clearAuths() throws ResponseException {
        clearTable("authTokens");
    }
}
