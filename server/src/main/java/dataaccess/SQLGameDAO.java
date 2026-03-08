package dataaccess;

import exception.ResponseException;
import model.*;

import java.util.ArrayList;

public interface SQLGameDAO {
    ArrayList<GameData> listGames() throws ResponseException;
    int createGame(String gameName) throws ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    void joinGame(int gameID, String white, String black) throws ResponseException;
    void updateGame(int gameID, String game) throws ResponseException;
    void deleteGame(int gameID) throws ResponseException;
    void clear() throws ResponseException;
}
