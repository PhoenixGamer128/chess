package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLUserDAO implements UserDAO, SQLDataAccess{
    public SQLUserDAO() {
        String createStatement = """
                CREATE TABLE IF NOT EXISTS users (
                username varchar(256) NOT NULL,
                password varchar(256) NOT NULL,
                email varchar(256) NOT NULL,
                PRIMARY KEY (username)
                )
                """;
        configureDatabase(createStatement);
    }

    public void createUser(UserData requestedUser) throws ResponseException {
        String hashedPassword = BCrypt.hashpw(requestedUser.password(), BCrypt.gensalt());
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(statement)) {
                prepareStatement.setString(1, requestedUser.username());
                prepareStatement.setString(2, hashedPassword);
                prepareStatement.setString(3, requestedUser.email());
                prepareStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess, ex.getMessage());
        }
    }

    public UserData getUser(UserData requestedUser) throws ResponseException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(statement)) {
                prepareStatement.setString(1, requestedUser.username());
                ResultSet resultSet = prepareStatement.executeQuery();

                if (resultSet.next()) {
                    String resultUsername = resultSet.getString("username");
                    String resultPassword = resultSet.getString("password");
                    String resultEmail = resultSet.getString("email");

                    return new UserData(resultUsername, resultPassword, resultEmail);
                }
                else {
                    return null;
                }
            }
        }
        catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.DataAccess, ex.getMessage());
        }
    }

    public HashMap<String, UserData> listUsers() throws ResponseException {
        return null;
    }

    public void clearUsers() throws ResponseException {
        clearTable("users");
    }
}
