package client;

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


    @Test
    public void registerUserNoError() {
        AuthData authData = serverFacade.register(newUser);
        assertEquals("newUser", authData.username());
    }

}
