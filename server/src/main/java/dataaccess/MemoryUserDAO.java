package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Objects;

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

    public void clearUsers() {
        users = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(users);
    }
}
