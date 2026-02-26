package server;

import io.javalin.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import exception.ResponseException;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clear)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/games", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
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

    private void clear(Context ctx) {
        service.AuthService.clearAuth();
        service.UserService.clearUser();
        service.GameService.clearGames();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseException {
        UserService.RegisterRequest reg = new Gson().fromJson(ctx.body(), UserService.RegisterRequest.class);
        var res = UserService.register(reg);
        ctx.result(new Gson().toJson(res));
    }

    private void login(Context ctx) throws ResponseException {
        UserService.LoginRequest req = new Gson().fromJson(ctx.body(), UserService.LoginRequest.class);
        var res = UserService.login(req);
        ctx.result(new Gson().toJson(res));
    }

    private void logout(Context ctx) throws ResponseException {
        UserService.LogoutRequest req = new Gson().fromJson(ctx.header("Authorization"), UserService.LogoutRequest.class);
        var res = UserService.logout(req);
        ctx.result(new Gson().toJson(res));
    }

    private void listGames(Context ctx) throws ResponseException {
        GameService.ListGamesRequest req = new Gson().fromJson(ctx.header("Authorization"), GameService.ListGamesRequest.class);
        var res = GameService.listGames(req);
        ctx.result(new Gson().toJson(res));
    }

    private void createGame(Context ctx) throws ResponseException {
        GameService.CreateGameRequest req = new Gson().fromJson(ctx.body(), GameService.CreateGameRequest.class);
        GameService.GetAuth auth = new Gson().fromJson(ctx.header("Authorization"), GameService.GetAuth.class);
        String token = auth.getToken();
        var res = GameService.createGame(req, token);
        ctx.result(new Gson().toJson(res));
    }

    private void joinGame(Context ctx) throws ResponseException {
        GameService.JoinGameRequest req = new Gson().fromJson(ctx.body(), GameService.JoinGameRequest.class);
        GameService.GetAuth auth = new Gson().fromJson(ctx.header("Authorization"), GameService.GetAuth.class);
        String token = auth.getToken();
        var res = GameService.joinGame(req, token);
        ctx.result(new Gson().toJson(res));
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.getCode());
        ctx.result(ex.toJson());
    }
}
