package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    static UserService userService = new UserService(new SQLUserDAO(), new SQLAuthDAO());
    static GameService gameService = new GameService(new SQLGameDAO(), userService);
    static ClearService clearService = new ClearService(userService, gameService);
    UserData registeredUser = new UserData("Bob", "bob", "bob@bob.com");
    String registeredAuth;

    @BeforeEach
    void setUp() throws ResponseException  {
        clearService.clear();
        registeredAuth = userService.register(registeredUser).authToken();
    }

    @Test
    void registerUser() throws ResponseException {
        // Add two different users
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");

        userService.register(userAlice);
        HashMap<String, UserData> users = userService.listUsers();
        assertEquals(2, users.size());
    }

    @Test
    void registerTakenUser() throws ResponseException {
        // Add different users with identical usernames
        UserData usernameTakenUser = new UserData("Bob","banana","billy@joe.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> userService.register(usernameTakenUser));
        assertEquals(ResponseException.Code.AlreadyTaken, exception.code());
    }

    @Test
    void loginUser() throws ResponseException {
        userService.logout(registeredAuth);
        assertEquals(1, userService.listUsers().size());
    }

    @Test
    void loginUserUnauthorized() throws ResponseException {
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(userAlice));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    @Test
    void logoutUser() throws ResponseException  {
        userService.logout(registeredAuth);
        CreateGameRequest gameRequest = new CreateGameRequest(registeredAuth, "game1");
        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.createGame(gameRequest));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    @Test
    void logoutTwice() throws ResponseException {
        userService.logout(registeredAuth);
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.logout(registeredAuth));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    @Test
    void listGames() throws ResponseException {
        assertEquals(0,gameService.listGames(registeredAuth).games().size());
        CreateGameRequest gameRequest = new CreateGameRequest(registeredAuth, "game1");
        gameService.createGame(gameRequest);
        assertEquals(1,gameService.listGames(registeredAuth).games().size());
    }

    @Test
    void listGamesNoAuth() throws ResponseException {
        userService.logout(registeredAuth);
        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.listGames(registeredAuth));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    @Test
    void createGames() throws ResponseException {
        gameService.createGame(new CreateGameRequest(registeredAuth, "game1"));
        assertEquals(1, gameService.listGamesObjects().size());

        gameService.createGame(new CreateGameRequest(registeredAuth, "game2"));
        assertEquals(2, gameService.listGamesObjects().size());

        clearService.clear();
        assertEquals(0, gameService.listGamesObjects().size());
    }

    @Test
    void createGameNoAuth() throws  ResponseException {
        CreateGameRequest gameRequest = new CreateGameRequest("", "game1");
        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.createGame(gameRequest));
        assertEquals(ResponseException.Code.Unauthorized, ex.code());
    }

    @Test
    void createGameNoName() throws ResponseException {
        CreateGameRequest gameRequest = new CreateGameRequest(registeredAuth, "");
        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.createGame(gameRequest));
        assertEquals(ResponseException.Code.BadRequest, ex.code());
    }

    @Test
    void joinTwoPlayers() throws ResponseException {
        // Join Bob as white
        gameService.createGame(new CreateGameRequest(registeredAuth, "game1"));
        assertEquals(1, gameService.listGamesObjects().size());

        JoinGameData dataRequest = new JoinGameData(ChessGame.TeamColor.WHITE, 1);
        JoinGameRequest joinRequest = new JoinGameRequest(registeredAuth, dataRequest);
        gameService.joinGame(joinRequest);

        // Join Alice as black
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");
        String aliceAuth = userService.register(userAlice).authToken();

        JoinGameData newDataRequest = new JoinGameData(ChessGame.TeamColor.BLACK, 1);
        JoinGameRequest newJoinRequest = new JoinGameRequest(aliceAuth, newDataRequest);
        gameService.joinGame(newJoinRequest);

        GameData finalGame = new GameData(1, "Bob", "Alice", "game1", new ChessGame());
        assertEquals(finalGame, gameService.listGames(registeredAuth).games().getFirst());
    }

    @Test
    void joinSameColor() {
        // Join Bob as white
        gameService.createGame(new CreateGameRequest(registeredAuth, "game1"));
        assertEquals(1, gameService.listGamesObjects().size());

        JoinGameData dataRequest = new JoinGameData(ChessGame.TeamColor.WHITE, 1);
        JoinGameRequest joinRequest = new JoinGameRequest(registeredAuth, dataRequest);
        gameService.joinGame(joinRequest);

        // Join Alice as white as well
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");
        String aliceAuth = userService.register(userAlice).authToken();

        JoinGameData newDataRequest = new JoinGameData(ChessGame.TeamColor.WHITE, 1);
        JoinGameRequest newJoinRequest = new JoinGameRequest(aliceAuth, newDataRequest);

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.joinGame(newJoinRequest));
        assertEquals(ResponseException.Code.AlreadyTaken, ex.code());
    }

    @Test
    void clearDataBase() throws ResponseException {
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