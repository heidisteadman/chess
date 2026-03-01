package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class GameService {
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResponse(ArrayList<GameData> games) {}
    public record CreateGameRequest(String gameName) {}
    public record CreateGameResponse(int gameID) {}
    public record JoinGameRequest(String playerColor, String gameID) {}
    public record JoinGameResponse() {}


    public static ListGamesResponse listGames(ListGamesRequest l) throws ResponseException {
        AuthData auth = AuthDAO.findAuth(l.authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        }

        ArrayList<GameData> game = UserDAO.listGames();
        if (game == null) {
            ArrayList<GameData> games = new ArrayList<>();
            return new ListGamesResponse(games);
        }

        return new ListGamesResponse(game);
    }

    public static CreateGameResponse createGame(CreateGameRequest c, String token) throws ResponseException {
        AuthData auth = AuthDAO.findAuth(token);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        } else if (c.gameName() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        int gameID = GameDAO.createGame(c.gameName);
        return new CreateGameResponse(gameID);
    }

    public static JoinGameResponse joinGame(JoinGameRequest j, String token) throws ResponseException {
        AuthData auth = AuthDAO.findAuth(token);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        }

        if (j.gameID == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        int gid = Integer.parseInt(j.gameID);
        GameData game = GameDAO.getGame(gid);

        if (game == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        String whiteUser = game.getWhite();
        String blackUser = game.getBlack();
        String user = auth.getUser();

        if (Objects.equals(j.playerColor, "BLACK") && (blackUser == null)) {
            GameDAO.updateGame(gid, whiteUser, user);
            return new JoinGameResponse();
        } else if (Objects.equals(j.playerColor, "WHITE") && (whiteUser == null)) {
            GameDAO.updateGame(gid, user, blackUser);
            return new JoinGameResponse();
        } else if (!(Objects.equals(j.playerColor, "BLACK")) && !(Objects.equals(j.playerColor, "WHITE"))) {
            throw new ResponseException(400, "Error: bad request");
        } else {
            throw new ResponseException(403, "Error: Already Taken");
        }
    }

    public static void clearGames() {
        GameDAO.clearGameDB();
    }
}
