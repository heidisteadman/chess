package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MySQLGameService {
    public MySQLGameService(MySQLGameDAO gameDAO, MySQLAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public record ListGamesRequest(String authToken) {}
    public record ListGamesResponse(ArrayList<GameData> games) {}
    public record CreateGameRequest(String gameName) {}
    public record CreateGameResponse(int gameID) {}
    public record JoinGameRequest(String playerColor, String gameID) {}
    public record JoinGameResponse() {}

    private final MySQLGameDAO gameDAO;
    private final MySQLAuthDAO authDAO;

    public ListGamesResponse listGames(ListGamesRequest l) throws ResponseException {
        AuthData auth = authDAO.findAuth(l.authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized list games request");
        }

        ArrayList<GameData> game = gameDAO.listGames();
        if (game == null) {
            ArrayList<GameData> games = new ArrayList<>();
            return new MySQLGameService.ListGamesResponse(games);
        }

        return new MySQLGameService.ListGamesResponse(game);
    }

    public CreateGameResponse createGame(CreateGameRequest c, String token) throws ResponseException {
        AuthData auth = authDAO.findAuth(token);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        } else if (c.gameName() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        int gameID = gameDAO.createGame(c.gameName);
        return new MySQLGameService.CreateGameResponse(gameID);
    }

    public JoinGameResponse joinGame(JoinGameRequest j, String token) throws ResponseException {
        AuthData auth = authDAO.findAuth(token);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized join game request");
        }

        if (j.gameID == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        int gid = Integer.parseInt(j.gameID);
        GameData game = gameDAO.getGame(gid);

        if (game == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        String whiteUser = game.getWhite();
        String blackUser = game.getBlack();
        String user = auth.getUser();

        if (Objects.equals(j.playerColor, "BLACK") && (blackUser == null)) {
            gameDAO.joinGame(gid, whiteUser, user);
            return new MySQLGameService.JoinGameResponse();
        } else if (Objects.equals(j.playerColor, "WHITE") && (whiteUser == null)) {
            gameDAO.joinGame(gid, user, blackUser);
            return new MySQLGameService.JoinGameResponse();
        } else if (!(Objects.equals(j.playerColor, "BLACK")) && !(Objects.equals(j.playerColor, "WHITE"))) {
            throw new ResponseException(400, "Error: bad request");
        } else {
            throw new ResponseException(403, "Error: Already Taken");
        }
    }

    public void clear() throws ResponseException {
        gameDAO.clear();
    }
}
