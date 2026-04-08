package server;

import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import io.javalin.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import exception.ResponseException;
import service.MySQLGameService;
import service.MySQLUserService;
import service.MySQLAuthService;
import websocket.WebsocketHandler;

public class Server {

    private final Javalin javalin;
    private final MySQLUserDAO userDAO;
    {
        try {
            userDAO = new MySQLUserDAO();
        } catch (ResponseException re) {
            throw new RuntimeException(re);
        }
    }

    private final MySQLGameDAO gameDAO;
    {
        try {
            gameDAO = new MySQLGameDAO();
        } catch (ResponseException re) {
            throw new RuntimeException(re);
        }
    }

    private final MySQLAuthDAO authDAO;
    {
        try {
            authDAO = new MySQLAuthDAO();
        } catch (ResponseException re) {
            throw new RuntimeException(re);
        }
    }

    private final MySQLUserService userService = new MySQLUserService(userDAO, authDAO);
    private final MySQLGameService gameService = new MySQLGameService(gameDAO, authDAO);
    private final MySQLAuthService authService = new MySQLAuthService(authDAO);

    public Server() {
        WebsocketHandler wsh = new WebsocketHandler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clear)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler)
                .ws("/ws", ws -> {
                    ws.onConnect(wsh);
                    ws.onMessage(wsh);
                    ws.onClose(wsh);
                });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) throws ResponseException {
        authService.clear();
        userService.clear();
        gameService.clear();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseException {
        MySQLUserService.RegisterRequest reg = new Gson().fromJson(ctx.body(), MySQLUserService.RegisterRequest.class);
        var res = userService.register(reg);
        ctx.result(new Gson().toJson(res));
    }

    private void login(Context ctx) throws ResponseException {
        MySQLUserService.LoginRequest req = new Gson().fromJson(ctx.body(), MySQLUserService.LoginRequest.class);
        var res = userService.login(req);
        ctx.result(new Gson().toJson(res));
    }

    private void logout(Context ctx) throws ResponseException {
        String token = ctx.header("authorization");
        MySQLUserService.LogoutRequest req = new MySQLUserService.LogoutRequest(token);
        var res = userService.logout(req);
        ctx.result(new Gson().toJson(res));
    }

    private void listGames(Context ctx) throws ResponseException {
        String token = ctx.header("authorization");
        MySQLGameService.ListGamesRequest req = new MySQLGameService.ListGamesRequest(token);
        var res = gameService.listGames(req);
        ctx.result(new Gson().toJson(res));
    }

    private void createGame(Context ctx) throws ResponseException {
        MySQLGameService.CreateGameRequest req = new Gson().fromJson(ctx.body(), MySQLGameService.CreateGameRequest.class);
        String token = ctx.header("authorization");
        var res = gameService.createGame(req, token);
        ctx.result(new Gson().toJson(res));
    }

    private void joinGame(Context ctx) throws ResponseException {
        MySQLGameService.JoinGameRequest req = new Gson().fromJson(ctx.body(), MySQLGameService.JoinGameRequest.class);
        String token = ctx.header("authorization");
        var res = gameService.joinGame(req, token);
        ctx.result(new Gson().toJson(res));
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.getCode());
        ctx.result(ex.toJson());
    }
}
