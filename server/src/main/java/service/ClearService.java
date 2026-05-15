package service;

import dataaccess.ResponseException;

public class ClearService {
    UserService userService;

    public ClearService(UserService userService) {
        this.userService = userService;
    }

    public void clear() throws ResponseException {
        userService.clearUsers();
    }
}
