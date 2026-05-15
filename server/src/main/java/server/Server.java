package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.RegisterResponse;
import model.UserData;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        this.userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        javalin.post("/user", this::registerUser)
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
        // TODO: figure out why we're turning a request into a response
        ctx.result(new Gson().toJson(userService.register(user)));
    }
}
