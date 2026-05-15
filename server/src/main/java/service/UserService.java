package service;

import dataaccess.*;
import model.AuthData;
import model.RegisterResponse;
import model.UserData;

// import javax.xml.crypto.Data;
import java.util.HashMap;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResponse register(UserData requestedUser) throws ResponseException {
        if (userDAO.getUser(requestedUser) != null) {
            throw new ResponseException(ResponseException.Code.AlreadyTaken, "Username already taken.");
        }

        userDAO.createUser(requestedUser);
        AuthData newAuthData = authDAO.createAuth(requestedUser);
        return new RegisterResponse(newAuthData.authToken(), newAuthData.username());
    }

    public HashMap<String, UserData> listUsers() {
        try {
            return userDAO.listUsers();
        }
        catch (ResponseException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
