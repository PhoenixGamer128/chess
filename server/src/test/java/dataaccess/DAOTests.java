package dataaccess;

import chess.ChessGame;
import model.*;

import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {
    UserDAO userDAO = new SQLUserDAO();
    AuthDAO authDAO = new SQLAuthDAO();
    GameDAO gameDAO = new SQLGameDAO();

    UserData newUser = new UserData("newUser", "newPassword", "new@mail.com");
    UserData invalidNewUser = new UserData("badUser", "invalidPassword", "bad@mail.com");
    UserData invalidOtherUser = new UserData(null, "BadPassword",null);
    static ArrayList<UserData> userList = new ArrayList<>();
    static ArrayList<GameData> gameList = new ArrayList<>();

    GameData newGame = new GameData(
            null,
            "",
            "",
            "newGame",
            new ChessGame()
    );

    GameData invalidGame = new GameData(
            null,
            "",
            "",
            null,
            new ChessGame()
    );


    @BeforeAll
    static void initialization() {
        for (int i = 0; i < 10; i++) {
            userList.add(new UserData("user" + i, "pass" + i, i + "@mail.com"));
        }
        for (int i = 0; i < 10; i++) {
            gameList.add(new GameData(null,
                    "",
                    "",
                    "game" + i,
                    new ChessGame())
            );
        }
    }

    @BeforeEach
    void setUp() throws ResponseException {
        gameDAO.clearGames();
        userDAO.clearUsers();
        authDAO.clearAuths();
    }

    @Test
    void createUser() {
        userDAO.createUser(newUser);
        assertTrue(assertUser(newUser, userDAO.getUser(newUser)));
    }

    @Test
    void createUserInvalid() {
        userDAO.createUser(newUser);
        assertNull(userDAO.getUser(invalidNewUser));
    }

    boolean assertUser(UserData expected, UserData actual) {
        return expected.username().equals(actual.username()) &&
                BCrypt.checkpw(expected.password(), actual.password()) &&
                expected.email().equals(actual.email());
    }

    @Test
    void getUser() {
        for (UserData user : userList) {
            userDAO.createUser(user);
        }
        for (UserData user : userList) {
            assertTrue(assertUser(user, userDAO.getUser(user)));
        }
    }

    @Test
    void getUserInvalid() {
        userDAO.createUser(newUser);
        assertNull(userDAO.getUser(invalidOtherUser));
    }

    @Test
    void listUsers() {
        for (UserData user : userList) {
            userDAO.createUser(user);
        }
        assertEquals(userList.size(), userDAO.listUsers().size());
    }

    @Test
    void listNoUsers() {
        userDAO.clearUsers();
        assertEquals(0, userDAO.listUsers().size());
    }

    @Test
    void clearUsers() {
        for (UserData user : userList) {
            userDAO.createUser(user);
        }
        for (UserData user : userList) {
            assertTrue(assertUser(user, userDAO.getUser(user)));
        }
        userDAO.clearUsers();
        assertEquals(0, userDAO.listUsers().size());
    }

    boolean assertGame(GameData expected, GameData actual) {
        return Objects.equals(expected.whiteUsername(), actual.whiteUsername()) &&
                Objects.equals(expected.blackUsername(), actual.blackUsername()) &&
                Objects.equals(expected.gameName(), actual.gameName()) &&
                expected.game().equals(actual.game());
    }

    @Test
    void createGame() {
        int gameID = gameDAO.createGame(newGame);
        assertTrue(assertGame(newGame, gameDAO.getGame(gameID)));
    }

    @Test
    void createInvalidGame() {
        assertThrows(ResponseException.class, () -> gameDAO.createGame(invalidGame));
    }

    @Test
    void getGame() {
        ArrayList<Integer> gameIDList = new ArrayList<>();
        for (GameData game : gameList) {
            gameIDList.add(gameDAO.createGame(game));
        }
        for (int i = 0; i < gameIDList.size(); i++) {
            assertTrue(assertGame(gameList.get(i), gameDAO.getGame(gameIDList.get(i))));
        }
    }

    @Test
    void getInvalidGame() {
        int gameID = gameDAO.createGame(newGame);
        int invalidID = gameID == 1 ? 0 : 1;
        assertNull(gameDAO.getGame(invalidID));
    }

    @Test
    void listGames() {
        for (GameData game : gameList) {
            gameDAO.createGame(game);
        }
        assertEquals(gameList.size(), gameDAO.listGames().size());
    }

    @Test
    void listNoGames() {
        gameDAO.clearGames();
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    void updateGame() {
        int gameID = gameDAO.createGame(newGame);
        GameData newGameWithID = new GameData(
                gameID,
                newGame.whiteUsername(),
                newGame.blackUsername(),
                newGame.gameName(),
                new ChessGame()
        );
        GameData newUpdatedGameWithID = new GameData(
                gameID,
                "whiteUsername",
                "blackUsername",
                newGame.gameName(),
                new ChessGame()
        );
        gameDAO.updateGame(newGameWithID.gameID(), newUpdatedGameWithID);
        assertTrue(assertGame(newUpdatedGameWithID, gameDAO.getGame(gameID)));
    }

    @Test
    void invalidUpdateGame() {
        int gameID = gameDAO.createGame(newGame);
        int invalidGameID = gameID == 1 ? 0 : 1;
        GameData validGame = new GameData(
                gameID,
                newGame.whiteUsername(),
                newGame.blackUsername(),
                newGame.gameName(),
                newGame.game()
        );
        GameData invalidGame = new GameData(
                invalidGameID,
                newGame.whiteUsername(),
                newGame.blackUsername(),
                newGame.gameName(),
                newGame.game()
        );
        gameDAO.updateGame(newGame.gameID(), invalidGame);
        assertEquals(validGame, gameDAO.getGame(gameID));
    }

    @Test
    void clearGames() {
        for (GameData game : gameList) {
            gameDAO.createGame(game);
        }
        assertEquals(gameList.size(), gameDAO.listGames().size());

        gameDAO.clearGames();
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    void createUserWithAuth() {
        String authToken = authDAO.createAuth(newUser).authToken();
        AuthData newAuthData = new AuthData(authToken, newUser.username());
        assertEquals(newAuthData, authDAO.getAuth(authToken));
    }

    @Test
    void createUserInvalidAuth() {
        authDAO.createAuth(newUser);
        assertNull(authDAO.getAuth("invalidAuthToken").authToken());
    }

    @Test
    void getAuth() {
        ArrayList<String> authTokens = new ArrayList<>();
        for (UserData user : userList) {
            authTokens.add(authDAO.createAuth(user).authToken());
        }
        for (int i = 0; i < userList.size(); i++) {
            AuthData expectedAuthData = new AuthData(authTokens.get(i), userList.get(i).username());
            assertEquals(expectedAuthData, authDAO.getAuth(authTokens.get(i)));
        }
    }

    @Test
    void getInvalidAuth() {
        authDAO.createAuth(newUser);
        String invalidAuthToken = "invalidAuth";
        assertNull(authDAO.getAuth(invalidAuthToken).authToken());
    }

    @Test
    void deleteAuth() {
        String authToken = authDAO.createAuth(newUser).authToken();
        AuthData newAuthData = new AuthData(authToken, newUser.username());
        assertEquals(newAuthData, authDAO.getAuth(authToken));

        authDAO.deleteAuth(newAuthData);
        assertNull(authDAO.getAuth(authToken).authToken());
    }

    int iterateAuthSize(ArrayList<String> authList) {
        int authSize = 0;
        for (String authToken : authList) {
            if (authDAO.getAuth(authToken).authToken() != null) {
                authSize++;
            }
        }
        return authSize;
    }

    @Test
    void clearAuths() {
        ArrayList<String> authList = new ArrayList<>();
        for (UserData user: userList) {
            authList.add(authDAO.createAuth(user).authToken());
        }

        assertEquals(userList.size(), iterateAuthSize(authList));

        authDAO.clearAuths();
        assertEquals(0, iterateAuthSize(authList));
    }
}
