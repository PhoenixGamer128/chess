package service;

import dataaccess.*;
import model.UserData;


import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    //TODO: after implementing deletion, change service to static
    UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    @BeforeEach
    void SetUp() {
        service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    }

    @Test
    void RegisterUser() throws ResponseException {
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");
        UserData userBob = new UserData("Bob","bob","bob@bob.com");

        service.register(userAlice);
        service.register(userBob);
        HashMap<String, UserData> users = service.listUsers();
        assertEquals(2, users.size());
    }

    @Test
    void RegisterTakenUser() throws ResponseException {
        UserData userBob = new UserData("Bob","bob","bob@bob.com");
        UserData userBobTaken = new UserData("Bob","banana","billy@joe.com");

        service.register(userBob);
        ResponseException exception = assertThrows(ResponseException.class, () -> service.register(userBobTaken));
        assertEquals(ResponseException.Code.AlreadyTaken, exception.code());
    }
}