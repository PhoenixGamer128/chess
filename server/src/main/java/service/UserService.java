package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterResponse;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResponse register(UserData requestedUser) throws DataAccessException, AlreadyTakenException {
        if (userDAO.getUser(requestedUser) != null) {
            throw new AlreadyTakenException("User already taken.");
        }
        userDAO.createUser(requestedUser);
        AuthData newAuthData = authDAO.createAuth(requestedUser);
        return new RegisterResponse(newAuthData.authToken(), newAuthData.username());
    }

    public HashMap<String, UserData> listUsers() {
        try {
            return userDAO.listUsers();
        }
        catch (DataAccessException ex) {
            System.out.println("Cannot access users: " + ex);
            return null;
        }
    }
}
