package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class GameService {
    public record ListGamesRequest(String authToken, String whiteUsername, String blackUsername) {}
    public record ListGamesResponse(ArrayList<GameData> gameList) {}
    public record CreateGameRequest(String authToken, String gameName) {}
    public record CreateGameResponse(int gameID) {}
    public record JoinGameRequest(String authToken, String color, String gameID) {}
    public record JoinGameResponse() {}
    public record ClearGamesRequest() {}
    public record ClearGamesResponse() {}

    public ListGamesResponse listGames(ListGamesRequest l) throws DataAccessException {
        AuthData auth = AuthDAO.findAuth(l.authToken);
        if (auth == null) {
            throw new DataAccessException("401: Error: Unauthorized");
        }

        if (l.whiteUsername != null) {
            UserData white = UserDAO.getUser(l.whiteUsername);
            ArrayList<GameData> games = UserDAO.listGames(white);
            return new ListGamesResponse(games);
        } else if (l.blackUsername != null) {
            UserData black = UserDAO.getUser(l.blackUsername);
            ArrayList<GameData> games = UserDAO.listGames(black);
            return new ListGamesResponse(games);
        } else {
            throw new DataAccessException("500: Error: No username provided");
        }
    }

    public CreateGameResponse createGame(CreateGameRequest c) throws DataAccessException {
        AuthData auth = AuthDAO.findAuth(c.authToken);
        if (auth == null) {
            throw new DataAccessException("401: Error: Unauthorized");
        }
        int gameID = GameDAO.createGame(c.gameName);
        return new CreateGameResponse(gameID);
    }

    public JoinGameResponse joinGame(JoinGameRequest j) throws DataAccessException {
        AuthData auth = AuthDAO.findAuth(j.authToken);
        if (auth == null) {
            throw new DataAccessException("401: Error: Unauthorized");
        }
        int gid = Integer.parseInt(j.gameID);
        GameData game = GameDAO.getGame(gid);
        String whiteUser = game.getWhite();
        String blackUser = game.getBlack();
        String user = auth.getUser();

        if (Objects.equals(j.color, "BLACK") && (blackUser == null)) {
            GameDAO.updateGame(gid, whiteUser, user);
            return new JoinGameResponse();
        } else if (Objects.equals(j.color, "WHITE") && (whiteUser == null)) {
            GameDAO.updateGame(gid, user, blackUser);
            return new JoinGameResponse();
        } else {
            throw new DataAccessException("403: Error: Already Taken");
        }
    }

    public ClearGamesResponse clearGames(ClearGamesRequest c) {
        GameDAO.clearGameDB();
        return new ClearGamesResponse();
    }
}
