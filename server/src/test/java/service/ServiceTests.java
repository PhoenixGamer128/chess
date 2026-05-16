package service;

import dataaccess.*;
import model.AuthData;
import model.CreateGameRequest;
import model.SessionResponse;
import model.UserData;

import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    static UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    static GameService gameService = new GameService(new MemoryGameDAO(), userService);
    static ClearService clearService = new ClearService(userService, gameService);
    UserData registeredUser = new UserData("Bob", "bob", "bob@bob.com");
    String registeredAuth;

    @BeforeEach
    void SetUp() {
        clearService.clear();
        registeredAuth = userService.register(registeredUser).authToken();
    }

    @Test
    void RegisterUser() throws ResponseException {
        // Add two different users
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");

        userService.register(userAlice);
        HashMap<String, UserData> users = userService.listUsers();
        assertEquals(2, users.size());
    }

    @Test
    void RegisterTakenUser() throws ResponseException {
        // Add different users with identical usernames
        UserData usernameTakenUser = new UserData("Bob","banana","billy@joe.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> userService.register(usernameTakenUser));
        assertEquals(ResponseException.Code.AlreadyTaken, exception.code());
    }

    @Test
    void LoginUser() throws ResponseException {
        // register bob
        // logout bob
        // do something that requires an auth token
        // login bob
        // do something that requires an auth token
        assertEquals("Bob",registeredUser.username());
    }

    @Test
    void CreateGames() throws ResponseException {
        gameService.createGame(new CreateGameRequest(registeredAuth, "game1"));
        assertEquals(1, gameService.listGames().size());

        gameService.createGame(new CreateGameRequest(registeredAuth, "game2"));
        assertEquals(2, gameService.listGames().size());

        clearService.clear();
        assertEquals(0, gameService.listGames().size());
    }

    @Test
    void CreateGameNoAuth() throws  ResponseException {
        CreateGameRequest gameRequest = new CreateGameRequest("", "game1");
        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.createGame(gameRequest));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    @Test
    void CreateGameNoName() throws ResponseException{
        CreateGameRequest gameRequest = new CreateGameRequest(registeredAuth, "");
        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.createGame(gameRequest));
        assertEquals(ResponseException.Code.BadRequest, ex.code());
    }

    @Test
    void ClearDataBase() throws ResponseException {
        HashMap<String, UserData> users = userService.listUsers();
        assertEquals(1, users.size());
        // programmatically add multiple users
        for (int i = 0; i < 19; i++) {
            String username = Integer.toString(i);
            UserData newUser = new UserData(username, "password", "foo@bar.com");
            userService.register(newUser);
        }
        users = userService.listUsers();
        assertEquals(20, users.size());

        // clear database
        clearService.clear();
        users = userService.listUsers();
        assertEquals(0, users.size());
    }
}