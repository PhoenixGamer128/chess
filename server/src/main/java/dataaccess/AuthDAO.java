package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData requestedUser) throws DataAccessException;

    AuthData getAuth(AuthData authData) throws DataAccessException;

    boolean deleteAuth(AuthData authData) throws DataAccessException;
}
