package service;

import dataaccess.*;
import model.UserData;

import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    //TODO: after implementing deletion, change service to static
    UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    ClearService clearService = new ClearService(userService);

    @BeforeEach
    void SetUp() {
        clearService.clear();
    }

    @Test
    void RegisterUser() throws ResponseException {
        // Add two different users
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");
        UserData userBob = new UserData("Bob","bob","bob@bob.com");

        userService.register(userAlice);
        userService.register(userBob);
        HashMap<String, UserData> users = userService.listUsers();
        assertEquals(2, users.size());
    }

    @Test
    void RegisterTakenUser() throws ResponseException {
        // Add different users with identical usernames
        UserData userBob = new UserData("Bob","bob","bob@bob.com");
        UserData userBobTaken = new UserData("Bob","banana","billy@joe.com");

        userService.register(userBob);
        ResponseException exception = assertThrows(ResponseException.class, () -> userService.register(userBobTaken));
        assertEquals(ResponseException.Code.AlreadyTaken, exception.code());
    }

    @Test
    void ClearDataBase() throws ResponseException {
        HashMap<String, UserData> users = userService.listUsers();
        assertEquals(0, users.size());
        // programmatically add multiple users
        for (int i = 0; i < 20; i++) {
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