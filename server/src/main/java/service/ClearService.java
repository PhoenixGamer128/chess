package service;

import model.ResponseException;

public class ClearService {
    UserService userService;
    GameService gameService;

    public ClearService(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    public void clear() throws ResponseException {
        userService.clearUsers();
        gameService.clearGames();
    }
}
