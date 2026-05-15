package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public interface UserDAO {

    void createUser(UserData requestedUser) throws DataAccessException, AlreadyTakenException;

    UserData getUser(UserData requestedUser) throws DataAccessException;

    HashMap<String, UserData> listUsers() throws DataAccessException;

}
