package client;

import chess.ChessGame;
import dataaccess.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    private final UserData newUser = new UserData(
            "newUser",
            "newUserPass",
            "new.user@mail.com"
    );
    private final UserData otherUser = new UserData(
            "otherUser",
            "otherUserPass",
            "otherUser@mail.com"
    );
    private final UserData incompleteUser = new UserData(
            "incompleteUser",
            "pass",
            null
    );
    private final UserData invalidUser = new UserData(
            null,
            "pass",
            null
    );
    private String newAuthToken;



    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() {
        serverFacade.clear();
        newAuthToken = serverFacade.register(newUser).authToken();

    }


    @Test
    public void registerUser() {
        AuthData authData = serverFacade.register(otherUser);
        assertEquals("otherUser", authData.username());
        assertNotEquals("", authData.authToken());
    }

    @Test
    public void registerUserInvalid() {
        assertThrows(ResponseException.class, () -> serverFacade.register(incompleteUser));
    }

    @Test
    public void loginUser() {
        AuthData authData = serverFacade.login(newUser);
        assertEquals("newUser", authData.username());
    }

    @Test
    public void loginUserInvalid() {
        assertThrows(ResponseException.class, () -> serverFacade.login(invalidUser));
    }

    @Test
    public void logoutUser() {
        assertDoesNotThrow(() -> serverFacade.logout(newAuthToken));
    }

    @Test
    public void logoutInvalidUser() {
        assertThrows(ResponseException.class, () -> serverFacade.logout("badAuthToken"));
    }

    private boolean containsGame(GameList gameList, GameData targetGame) {
        return gameList.games().contains(targetGame);
    }

    @Test
    public void createGame() {
        CreateGameRequest gameRequest = new CreateGameRequest(newAuthToken, "newGame");
        GameID gameID = serverFacade.createGame(gameRequest);
        GameData targetGame = new GameData(
                gameID.gameID(),
                null,
                null,
                "newGame",
                new ChessGame()
        );
        assertTrue(containsGame(serverFacade.listGames(newAuthToken), targetGame));
    }

    @Test
    public void createGameInvalid() {
        CreateGameRequest gameRequest = new CreateGameRequest(newAuthToken, null);
        assertThrows(ResponseException.class, () -> serverFacade.createGame(gameRequest));
    }

    private void createMultipleGames(int numGames) {
        for (int i = 0; i < numGames; i++) {
            CreateGameRequest gameRequest = new CreateGameRequest(newAuthToken, "game" + i);
            serverFacade.createGame(gameRequest);
        }
    }

    @Test
    void listGames() {
        int numGames = 10;
        createMultipleGames(numGames);
        assertEquals(numGames, serverFacade.listGames(newAuthToken).games().size());
    }

    @Test
    void listGamesNoAuth() {
        CreateGameRequest gameRequest = new CreateGameRequest("badAuth", "noAuthGame");
        assertThrows(ResponseException.class, () -> serverFacade.createGame(gameRequest));
    }

    private void joinGameAsColor(String authToken, ChessGame.TeamColor color, int gameID) {
        JoinGameData joinData = new JoinGameData(color, gameID);
        JoinGameRequest joinUser = new JoinGameRequest(authToken, joinData);

        serverFacade.joinGame(joinUser);
    }

    @Test
    void joinGame() {
        String otherAuthToken = serverFacade.register(otherUser).authToken();
        CreateGameRequest gameRequest = new CreateGameRequest(newAuthToken, "newGame");
        GameID gameID = serverFacade.createGame(gameRequest);

        joinGameAsColor(newAuthToken, ChessGame.TeamColor.WHITE, gameID.gameID());
        joinGameAsColor(otherAuthToken, ChessGame.TeamColor.BLACK, gameID.gameID());

        GameData expectedGame = new GameData(
                gameID.gameID(),
                "newUser",
                "otherUser",
                "newGame",
                new ChessGame()
        );
        assertTrue(containsGame(serverFacade.listGames(newAuthToken), expectedGame));
    }

    @Test
    void joinGameTwoWhites() {
        String otherAuthToken = serverFacade.register(otherUser).authToken();
        CreateGameRequest gameRequest = new CreateGameRequest(newAuthToken, "newGame");
        GameID gameID = serverFacade.createGame(gameRequest);

        joinGameAsColor(newAuthToken, ChessGame.TeamColor.WHITE, gameID.gameID());
        assertThrows(ResponseException.class,
                () -> joinGameAsColor(otherAuthToken, ChessGame.TeamColor.WHITE, gameID.gameID())
        );
    }
}
