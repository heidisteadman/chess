package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import com.google.gson.Gson;

public class MySQLGameDAO implements SQLGameDAO, SQLDAO{
    public MySQLGameDAO() throws ResponseException{
        SQLDAO.configureDatabase(CREATE_GAMES);
    }

    public int createGame(String gameName) throws ResponseException {
        if (gameName == null) {
            throw new ResponseException(400, "Error: bad request");
        }

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
            throw new ResponseException(500, "Error: failed to get games from database");
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
            throw new ResponseException(500, "Error: failed to get game from database");
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

        if ((white == null) && (black == null)) {
            throw new ResponseException(400, "Error: bad request");
        }
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

    public void updateGame(int gameID, String game) throws ResponseException {
        GameData oldGame = getGame(gameID);
        if (oldGame == null) {
            throw new ResponseException(400, "Error: old game does not exist");
        }
        var statement = "UPDATE games SET game=? WHERE gameID=?";
        SQLDAO.executeUpdate(statement, game, gameID);
    }

    public void leaveGame(int gameID, String color) throws ResponseException {
        GameData game = getGame(gameID);
        if (game == null) {
            throw new ResponseException(400, "Error: game does not exist");
        }
        if (Objects.equals(color, "WHITE")) {
            var statement = "UPDATE games SET whiteUser=? WHERE gameID=?";
            SQLDAO.executeUpdate(statement, "", gameID);
        } else if (Objects.equals(color, "BLACK")) {
            var statement = "UPDATE games SET blackUser=? WHERE gameID=?";
            SQLDAO.executeUpdate(statement, "", gameID);
        }
    }

    public void endGame(int gameID) throws ResponseException {
        GameData oldGame = getGame(gameID);
        if (oldGame == null) {
            throw new ResponseException(400, "Error: game does not exist");
        }
        oldGame.getChess().setEnded();
        String jsonGame = new Gson().toJson(oldGame.getChess());
        var statement = "UPDATE games SET game=? WHERE gameID=?";
        SQLDAO.executeUpdate(statement, jsonGame, gameID);
    }
}
