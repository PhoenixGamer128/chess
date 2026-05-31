package service;

import dataaccess.*;
import model.AuthData;
import model.ResponseException;
import model.SessionResponse;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

// import javax.xml.crypto.Data;
import java.util.HashMap;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public SessionResponse register(UserData requestedUser) throws ResponseException {
        validateCredentials(requestedUser); // will throw an error if user input is syntactically incorrect

        // Will return null only if no user has the requested username
        if (userDAO.getUser(requestedUser) != null) {
            throw new ResponseException(ResponseException.Code.AlreadyTaken, "Error: Username already taken.");
        }

        userDAO.createUser(requestedUser);
        return login(requestedUser);
    }

    public SessionResponse login(UserData requestedUser) throws ResponseException {
        validateCredentials(requestedUser);

        UserData foundUser = userDAO.getUser(requestedUser);
        // verify username
        if (foundUser == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        // verify password
        if (!BCrypt.checkpw(requestedUser.password(), foundUser.password())) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        // give user an authToken
        AuthData newAuthData = authDAO.createAuth(requestedUser);
        return new SessionResponse(newAuthData.username(), newAuthData.authToken());
    }

    private void validateCredentials(UserData requestedUser) throws ResponseException {
        if (requestedUser == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Bad request.");
        }
        if (requestedUser.username() == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Username cannot be blank");
        }
        if (requestedUser.password() == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: Password cannot be empty");
        }
    }

    public void logout(String authToken) throws ResponseException {
        AuthData requestedUserAuth = authDAO.getAuth(authToken);
        if (requestedUserAuth.username() == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        authDAO.deleteAuth(requestedUserAuth);
    }

    public boolean validAuthToken(String authToken) {
        return (authDAO.getAuth(authToken).username() != null);
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

    public AuthData getUser(String authToken) {
        return authDAO.getAuth(authToken);
    }

    public void clearUsers() throws ResponseException {
        authDAO.clearAuths();
        userDAO.clearUsers();
    }
}
