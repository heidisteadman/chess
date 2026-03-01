package service;

import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameServiceTests {

    @Test
    public void createGameTest() throws ResponseException {
        UserService.RegisterRequest reg = new UserService.RegisterRequest("me", "pass", "me@email.org");
        UserService.RegisterResponse regRes = UserService.register(reg);
        String token = regRes.authToken();

        GameService.CreateGameRequest newGame = new GameService.CreateGameRequest("game");
        GameService.CreateGameResponse gameResp = GameService.createGame(newGame, token);
        int gid = gameResp.gameID();
        GameData game = GameDAO.getGame(gid);
        ArrayList<GameData> expGames = new ArrayList<>();
        expGames.add(game);

        Assertions.assertEquals(expGames, GameDAO.games);

        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void noTokTest() {
        GameService.CreateGameRequest newGame = new GameService.CreateGameRequest("name");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            GameService.createGame(newGame, "token");
        });

        Assertions.assertEquals("Error: Unauthorized", ex.message());

        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void listGameTest() throws ResponseException {
        UserService.RegisterRequest reg = new UserService.RegisterRequest("me", "pass", "me@email.org");
        UserService.RegisterResponse regRes = UserService.register(reg);
        String token = regRes.authToken();

        GameService.CreateGameRequest newGame = new GameService.CreateGameRequest("game");
        GameService.CreateGameResponse gameResp = GameService.createGame(newGame, token);
        int gid = gameResp.gameID();
        GameData game = GameDAO.getGame(gid);
        ArrayList<GameData> expGames = new ArrayList<>();
        expGames.add(game);

        GameService.ListGamesRequest listReq = new GameService.ListGamesRequest(token);
        GameService.ListGamesResponse listRes = GameService.listGames(listReq);

        Assertions.assertEquals(expGames, listRes.games());

        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void listNoTokTest() {
        GameService.ListGamesRequest listReq = new GameService.ListGamesRequest("auth");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            GameService.listGames(listReq);
        });

        Assertions.assertEquals("Error: Unauthorized", ex.message());

        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void validJoinTest() throws ResponseException {
        UserService.RegisterRequest reg = new UserService.RegisterRequest("me", "pass", "me@email.org");
        UserService.RegisterResponse regRes = UserService.register(reg);
        String token = regRes.authToken();

        GameService.CreateGameRequest newGame = new GameService.CreateGameRequest("game");
        GameService.CreateGameResponse gameResp = GameService.createGame(newGame, token);
        int gid = gameResp.gameID();
        String id = String.valueOf(gid);

        GameService.JoinGameRequest jgame = new GameService.JoinGameRequest("WHITE", id);
        GameService.JoinGameResponse jresp = GameService.joinGame(jgame, token);
        Assertions.assertInstanceOf(GameService.JoinGameResponse.class, jresp);

        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void userTakenTest() throws ResponseException {
        UserService.RegisterRequest reg = new UserService.RegisterRequest("me", "pass", "me@email.org");
        UserService.RegisterResponse regRes = UserService.register(reg);
        String token = regRes.authToken();

        GameService.CreateGameRequest newGame = new GameService.CreateGameRequest("game");
        GameService.CreateGameResponse gameResp = GameService.createGame(newGame, token);
        int gid = gameResp.gameID();
        String id = String.valueOf(gid);

        GameService.JoinGameRequest jgame = new GameService.JoinGameRequest("WHITE", id);
        GameService.joinGame(jgame, token);

        UserService.RegisterRequest reg2 = new UserService.RegisterRequest("me2", "passwerd", "me2@email.org");
        UserService.RegisterResponse regRes2 = UserService.register(reg2);
        String token2 = regRes2.authToken();

        GameService.JoinGameRequest jgame2 = new GameService.JoinGameRequest("WHITE", id);
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            GameService.joinGame(jgame2, token2);
        });

        Assertions.assertEquals("Error: Already Taken", ex.message());

        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void clearGameTest() throws ResponseException {
        UserService.RegisterRequest reg = new UserService.RegisterRequest("me", "pass", "me@email.org");
        UserService.RegisterResponse regRes = UserService.register(reg);
        String token = regRes.authToken();

        GameService.CreateGameRequest newGame = new GameService.CreateGameRequest("game");
        GameService.createGame(newGame, token);
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();

        ArrayList<GameData> expGames = new ArrayList<>();

        Assertions.assertEquals(expGames, GameDAO.games);

    }
}
