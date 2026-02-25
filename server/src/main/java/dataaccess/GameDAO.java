package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class GameDAO {
    static ArrayList<GameData> games = new ArrayList<>();
    private static int gameID = 1000;

    public static int createGame(String gameName) throws DataAccessException {
        GameData newGame = new GameData(gameID++, null, null, gameName, new ChessGame());
        if (games.contains(newGame)) {
            throw new DataAccessException("Already Taken");
        }
        games.add(newGame);
        return newGame.getID();
    }

    public static GameData getGame(int gid) throws DataAccessException {
        for (GameData game : games) {
            int id = game.getID();
            if (id == gid) {
                return game;
            }
        }
        throw new DataAccessException("401: Game not found");
    }

    public static void updateGame(int gid, String white, String black) throws DataAccessException {
        GameData oldGame = getGame(gid);
        if (oldGame == null) {
            throw new DataAccessException("401: Game not found");
        }
        String gname = oldGame.getName();
        ChessGame chess = oldGame.getChess();

        GameData newGame = new GameData(gid, white, black, gname, chess);
        deleteGame(oldGame);
        games.add(newGame);
    }
    public static void deleteGame(GameData g) throws DataAccessException {
        if (games.contains(g)) {
            games.remove(g);
        } else {
            throw new DataAccessException("401: game not found");
        }
    }
    public static void clearGameDB(){
        games = new ArrayList<>();
    }

}
