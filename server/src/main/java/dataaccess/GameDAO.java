package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class GameDAO {
    static ArrayList<GameData> games = new ArrayList<>();

    public void createGame(GameData g) throws DataAccessException {
        games.add(g);
    }

    public GameData getGame(int gid) throws DataAccessException {
        for (GameData game : games) {
            int id = game.getID();
            if (id == gid) {
                return game;
            }
        }
        throw new DataAccessException("401: Game not found");
    }

    public void updateGame(int gid, String gname) throws DataAccessException {
        GameData oldGame = getGame(gid);
        if (oldGame == null) {
            throw new DataAccessException("400: bad request");
        }
        String white = oldGame.getWhite();
        String black = oldGame.getBlack();
        ChessGame chess = oldGame.getChess();

        GameData newGame = new GameData(gid, white, black, gname, chess);
        deleteGame(oldGame);
        createGame(newGame);
    }
    public void deleteGame(GameData g) throws DataAccessException {
        if (games.contains(g)) {
            games.remove(g);
        } else {
            throw new DataAccessException("401: game not found");
        }
    }
    public void clearGameDB(){
        games = new ArrayList<>();
    }

}
