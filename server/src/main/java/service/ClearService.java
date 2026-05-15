package service;

import dataaccess.ResponseException;
import model.DeleteDBResult;

public class ClearService {
    UserService userService;

    public ClearService(UserService userService) {
        this.userService = userService;
    }

    public void clear() throws ResponseException {
        userService.clearUsers();
    }
}
