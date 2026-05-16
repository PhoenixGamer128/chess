package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    HashMap<String, String> authTokens;

    public MemoryAuthDAO() {
        authTokens = new HashMap<>();
    }

    public AuthData createAuth(UserData requestedUser) {
        String authToken = UUID.randomUUID().toString();
        authTokens.put(authToken, requestedUser.username());
        return new AuthData(authToken, requestedUser.username());
    }

    public AuthData getAuth(String authToken) {
        return new AuthData(authToken, authTokens.get(authToken));
    }

    public boolean deleteAuth(AuthData authData) {
        return authTokens.remove(authData.authToken(), authData.username());
    }

    public void clearAuths() {
        authTokens = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(authTokens, that.authTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authTokens);
    }
}
