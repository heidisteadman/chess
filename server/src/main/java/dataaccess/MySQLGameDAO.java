package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import com.google.gson.Gson;

public class MySQLGameDAO implements SQLGameDAO, SQLDAO{
    public MySQLGameDAO() throws ResponseException{
        SQLDAO.configureDatabase(CREATE_GAMES);
    }

    public int createGame(String gameName) throws ResponseException {
        ChessGame newGame = new ChessGame();
        String state = "INSERT INTO games (whiteUser, blackUser, gameName, game) VALUES (?, ?, ?, ?)";
        var jsonGame = new Gson().toJson(newGame);
        return SQLDAO.executeUpdate(state, "", "", gameName, jsonGame);
    }

    public ArrayList<GameData> listGames() throws ResponseException {
        String state = "SELECT gameID, whiteUser, blackUser, gameName, game FROM games";
        ArrayList<GameData> gameList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(state)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData addGame = readGame(rs);
                        gameList.add(addGame);
                    }
                }
            }
        } catch (SQLException | DataAccessException sqlex) {
            throw new ResponseException(500, "failed to get games from database");
        }
        return gameList;
    }

    public GameData getGame(int gameID) throws ResponseException {
        String state = "SELECT gameID, whiteUser, blackUser, gameName, game FROM games WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(state)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException sqlex ) {
            throw new ResponseException(500, "failed to get game from database");
        }
    }

    public void joinGame(int gameID, String white, String black) throws ResponseException{
        if (black == null) {
            String state = "UPDATE games SET whiteUser=? WHERE gameID=?";
            SQLDAO.executeUpdate(state, white, gameID);
        }

        if (white == null) {
            String state = "UPDATE games SET blackUser=? WHERE gameID=?";
            SQLDAO.executeUpdate(state, black, gameID);
        }

        if ((white != null) && (black != null)) {
            String state = "UPDATE games SET whiteUser=? WHERE gameID=?";
            String state2 = "UPDATE games SET blackUser=? WHERE gameID=?";
            SQLDAO.executeUpdate(state, white, gameID);
            SQLDAO.executeUpdate(state2, black, gameID);
        }
    }

    public void updateGame(int gameID, String game) throws ResponseException {
        String state = "UPDATE games SET game=? WHERE gameID=?";
        SQLDAO.executeUpdate(state, game, gameID);
    }

    public void deleteGame(int gameID) throws ResponseException {
        String state = "DELETE FROM games WHERE gameID=?";
        SQLDAO.executeUpdate(state, gameID);
    }

    public void clear() throws ResponseException {
        String state = "TRUNCATE games";
        SQLDAO.executeUpdate(state);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        var white = rs.getString("whiteUser");
        if (white.isEmpty()) {
            white = null;
        }
        var black = rs.getString("blackUser");
        if (black.isEmpty()) {
            black = null;
        }
        String gname = rs.getString("gameName");
        var gameJSON = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);

        return new GameData(gameID, white, black, gname, game);
    }
}
