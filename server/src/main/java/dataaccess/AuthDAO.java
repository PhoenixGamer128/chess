package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData requestedUser) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    void deleteAuth(AuthData authData) throws ResponseException;

    void clearAuths() throws ResponseException;
}
