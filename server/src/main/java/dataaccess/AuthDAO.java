package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData requestedUser) throws ResponseException;

    AuthData getAuth(AuthData authData) throws ResponseException;

    boolean deleteAuth(AuthData authData) throws ResponseException;

    AuthData findUser(String authToken);

    void clearAuths() throws ResponseException;
}
