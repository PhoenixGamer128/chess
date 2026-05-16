package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.CreateGameRequest;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import service.ClearService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        this.userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        this.gameService = new GameService(new MemoryGameDAO(), userService);
        this.clearService = new ClearService(userService, gameService);

        javalin.post("/user", this::registerUser)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .post("/game", this::createGame)
                .delete("/db", this::deleteDataBase)
                .exception(ResponseException.class, this::exceptionHandler);
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode(ex));
        ctx.result(ex.toJson());
    }

    private void registerUser(Context ctx) {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        ctx.result(new Gson().toJson(userService.register(user)));
    }

    private void loginUser(Context ctx) {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        ctx.result(new Gson().toJson(userService.login(user)));
    }

    private void logoutUser(Context ctx) {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        userService.logout(authToken);
        JsonObject emptyJson = new Gson().fromJson("{}", JsonObject.class);
        ctx.result(new Gson().toJson(emptyJson));
    }

    private void createGame(Context ctx) {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        GameData requestedName = new Gson().fromJson(ctx.body(), GameData.class);
        CreateGameRequest gameRequest = new CreateGameRequest(authToken, requestedName.gameName());
        ctx.result(new Gson().toJson(gameService.createGame(gameRequest)));
    }

    private void deleteDataBase(Context ctx) {
        clearService.clear();
        JsonObject emptyJson = new Gson().fromJson("{}", JsonObject.class);
        ctx.result(new Gson().toJson(emptyJson));
    }
}
