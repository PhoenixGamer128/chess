package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;
import service.ClearService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final ClearService clearService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        this.userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        this.clearService = new ClearService(userService);

        javalin.post("/user", this::registerUser)
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

    private void deleteDataBase(Context ctx) {
        clearService.clear();
        ctx.result(new Gson().toJson(""));
    }
}
