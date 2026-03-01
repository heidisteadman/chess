package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class GameDAO {
    public static ArrayList<GameData> games = new ArrayList<>();
    private static int gameID = 1000;

    public static int createGame(String gameName) throws ResponseException {
        GameData newGame = new GameData(gameID++, null, null, gameName, new ChessGame());
        if (games.contains(newGame)) {
            throw new ResponseException(400, "Already Taken");
        }
        games.add(newGame);
        return newGame.getID();
    }

    public static GameData getGame(int gid) {
        for (GameData game : games) {
            int id = game.getID();
            if (id == gid) {
                return game;
            }
        }
        return null;
    }

    public static void updateGame(int gid, String white, String black) throws ResponseException {
        GameData oldGame = getGame(gid);
        if (oldGame == null) {
            throw new ResponseException(401, "Error: Game not found");
        }
        String gname = oldGame.getName();
        ChessGame chess = oldGame.getChess();

        GameData newGame = new GameData(gid, white, black, gname, chess);
        deleteGame(oldGame);
        games.add(newGame);
    }
    public static void deleteGame(GameData g) throws ResponseException {
        if (games.contains(g)) {
            games.remove(g);
        } else {
            throw new ResponseException(401, "Error: game not found");
        }
    }
    public static void clearGameDB(){
        games = new ArrayList<>();
        gameID = 1000;
    }

}
