package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.GameService;
import service.UserService;
import service.ClearService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        this.userService = new UserService(new SQLUserDAO(), new SQLAuthDAO());
        this.gameService = new GameService(new SQLGameDAO(), userService);
        this.clearService = new ClearService(userService, gameService);

        javalin.post("/user", this::registerUser)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .get("/game", this::listGames)
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
        returnEmptyJson(ctx);
    }

    private void createGame(Context ctx) {
        String authToken = ctx.header("authorization");
        GameData requestedName = new Gson().fromJson(ctx.body(), GameData.class);
        CreateGameRequest gameRequest = new CreateGameRequest(authToken, requestedName.gameName());

        ctx.result(new Gson().toJson(gameService.createGame(gameRequest)));
    }

    private void joinGame(Context ctx) {
        String authToken = (ctx.header("authorization"));
        JoinGameData joinGameData = new Gson().fromJson(ctx.body(), JoinGameData.class);
        JoinGameRequest gameRequest = new JoinGameRequest(authToken, joinGameData);

        gameService.joinGame(gameRequest);
        returnEmptyJson(ctx);
    }

    private void listGames(Context ctx) throws IOException {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        GameList gameList = gameService.listGames(authToken);
        String result = new Gson().toJson(gameList);
        ctx.result(result);
    }

    private void deleteDataBase(Context ctx) {
        clearService.clear();
        returnEmptyJson(ctx);
    }

    private void returnEmptyJson(Context ctx) {
        JsonObject emptyJson = new Gson().fromJson("{}", JsonObject.class);
        ctx.result(new Gson().toJson(emptyJson));
    }
}
