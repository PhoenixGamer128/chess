package dataaccess;

import model.UserData;

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

    }

    public UserData getUser(UserData requestedUser) throws ResponseException {
        return null;
    }

    public HashMap<String, UserData> listUsers() throws ResponseException {
        return null;
    }

    public void clearUsers() throws ResponseException {

    }
}
