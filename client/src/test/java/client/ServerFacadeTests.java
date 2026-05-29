package client;

import dataaccess.ResponseException;
import model.AuthData;
import model.UserData;
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
    }


    @Test
    public void registerUser() {
        AuthData authData = serverFacade.register(newUser);
        assertEquals("newUser", authData.username());
        assertNotEquals("", authData.authToken());
    }

    @Test
    public void registerUserInvalid() {
        assertThrows(ResponseException.class, () -> serverFacade.register(incompleteUser));
    }

    @Test
    public void loginUser() {
        serverFacade.register(newUser);
        AuthData authData = serverFacade.login(newUser);
        assertEquals("newUser", authData.username());
    }

    @Test
    public void loginUserInvalid() {
        assertThrows(ResponseException.class, () -> serverFacade.login(invalidUser));
    }


}
