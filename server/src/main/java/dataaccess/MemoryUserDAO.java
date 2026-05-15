package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    HashMap<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    public void createUser(UserData requestedUser) {
        users.put(requestedUser.username(), requestedUser);
    }

    public UserData getUser(UserData requestedUser) {
        return users.get(requestedUser.username());
    }

    public HashMap<String, UserData> listUsers() {
        return users;
    }
}
