package service;

import dataaccess.ResponseException;
import model.DeleteDBResult;

public class ClearService {
    UserService userService;

    public ClearService(UserService userService) {
        this.userService = userService;
    }

    public DeleteDBResult clear() throws ResponseException {
        return userService.clearUsers();
    }
}
