package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import service.UserService;
import model.UserData;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    static final UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
//    @BeforeEach
//    void clear() throws DataAccessException {
//        service.delete();
//    }

    @Test
    void RegisterUser() throws DataAccessException {
        UserData userAlice = new UserData("Alice", "alice", "alice@alice.com");
        UserData userBob = new UserData("Bob","bob","bob@bob.com");

        service.register(userAlice);
        service.register(userBob);
        HashMap<String, UserData> users = service.listUsers();
        assertEquals(2, users.size());
    }

    @Test
    void RegisterTakenUser() throws DataAccessException, AlreadyTakenException {
        UserData userBob = new UserData("Bob","bob","bob@bob.com");
        UserData userBobTaken = new UserData("Bob","banana","billy@joe.com");

        service.register(userBob);
        assertThrows(AlreadyTakenException.class, () -> service.register(userBobTaken));
    }
}