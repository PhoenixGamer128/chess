package dataaccess;

import model.UserData;

import java.util.HashMap;

public interface UserDAO {

    void createUser(UserData requestedUser) throws ResponseException;

    UserData getUser(UserData requestedUser) throws ResponseException;

    HashMap<String, UserData> listUsers() throws ResponseException;

    void clearUsers() throws ResponseException;

}
