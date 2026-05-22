package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface SQLDataAccess {
    Set<String> allowedTableNames = Set.of("authTokens", "users", "games");


    default void clearTable(String tableName) throws ResponseException {
        if (!allowedTableNames.contains(tableName)) {
            throw new ResponseException(ResponseException.Code.BadRequest,
                    String.format("Cannot access table %s", tableName));
        }
        var statement = "TRUNCATE TABLE " + tableName;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess,
                    String.format("Unable to add authToken: %s", ex.getMessage()));
        }
    }
}
