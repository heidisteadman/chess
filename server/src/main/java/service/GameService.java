package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class GameService {
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResponse(ArrayList<GameData> gameList) {}
    public record CreateGameRequest(String authToken, String gameName) {}
    public record CreateGameResponse(int gameID) {}
    public record JoinGameRequest(String color, String gameID) {}
    public record JoinGameResponse() {}
    public record GetAuth(String authToken){
        public String getToken() {return authToken;}
    }

    public static ListGamesResponse listGames(ListGamesRequest l) throws ResponseException {
        AuthData auth = AuthDAO.findAuth(l.authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        }

        String username = auth.getUser();
        UserData user = UserDAO.getUser(username);
        ArrayList<GameData> game = UserDAO.listGames(user);

        return new ListGamesResponse(game);
    }

    public static CreateGameResponse createGame(CreateGameRequest c, String token) throws ResponseException {
        AuthData auth = AuthDAO.findAuth(token);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        }
        int gameID = GameDAO.createGame(c.gameName);
        return new CreateGameResponse(gameID);
    }

    public static JoinGameResponse joinGame(JoinGameRequest j, String token) throws ResponseException {
        AuthData auth = AuthDAO.findAuth(token);
        if (auth == null) {
            throw new ResponseException(401, "Error: Unauthorized");
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
            throw new ResponseException(403, "Error: Already Taken");
        }
    }

    public static void clearGames() {
        GameDAO.clearGameDB();
    }
}
