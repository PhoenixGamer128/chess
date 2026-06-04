package dataaccess;

import model.DataAccessException;
import model.ResponseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface SQLDataAccess {
    Set<String> ALLOWED_TABLE_NAMES = Set.of("authTokens", "users", "games");

    default void configureDatabase(String createStatement) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    default void clearTable(String tableName) throws ResponseException {
        if (!ALLOWED_TABLE_NAMES.contains(tableName)) {
            throw new ResponseException(ResponseException.Code.BadRequest,
                    String.format("Cannot access table %s", tableName));
        }
        var statement = "TRUNCATE TABLE " + tableName;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to add authToken: %s", ex.getMessage()));
        }
    }
}
