package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MySQLGameDAOTests {
    MySQLGameDAO gameDAO;

    @BeforeEach
    public void makeDAO() throws ResponseException {
        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGameTest() throws ResponseException {
        String testGameName = "test game";
        int testGameID = gameDAO.createGame(testGameName);
        GameData testGame = gameDAO.getGame(testGameID);

        Assertions.assertNotNull(testGame, "gameID should exist");
        Assertions.assertEquals(testGameName, testGame.getName());
        Assertions.assertNull(testGame.getWhite());
        Assertions.assertNull(testGame.getBlack());
    }

    @Test
    public void createGameNullTest() {
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void joinGameTest() throws ResponseException {
        String testGameName = "test game";
        int testGameID = gameDAO.createGame(testGameName);
        gameDAO.joinGame(testGameID, "myName", null);
        GameData testGame = gameDAO.getGame(testGameID);

        Assertions.assertNotNull(testGame, "game should exist");
        Assertions.assertEquals("myName", testGame.getWhite());
        Assertions.assertNull(testGame.getBlack());
    }

    @Test
    public void joinGameNoUserTest() throws ResponseException{
        String testGameName = "test game";
        int testGameID = gameDAO.createGame(testGameName);

        Assertions.assertThrows(ResponseException.class, () -> gameDAO.joinGame(testGameID, null, null));
    }

    @Test
    public void listGamesTest() throws ResponseException {
        String testGameName1 = "test game 1";
        String testGameName2 = "test game 2";
        int game1ID = gameDAO.createGame(testGameName1);
        int game2ID = gameDAO.createGame(testGameName2);
        GameData game1 = gameDAO.getGame(game1ID);
        GameData game2 = gameDAO.getGame(game2ID);
        ArrayList<GameData> testGamesList = new ArrayList<>();
        testGamesList.add(game1);
        testGamesList.add(game2);

        ArrayList<GameData> returnedList = gameDAO.listGames();

        Assertions.assertEquals(testGamesList, returnedList);
    }

    @Test
    public void getGameTest() throws ResponseException {
        String testGameName = "test game";
        int testGameID = gameDAO.createGame(testGameName);
        GameData testGame = gameDAO.getGame(testGameID);
        ChessGame newGame = new ChessGame();

        Assertions.assertEquals(testGameName, testGame.gameName());
        Assertions.assertNull(testGame.whiteUsername());
        Assertions.assertNull(testGame.blackUsername());
        Assertions.assertEquals(testGameID, testGame.gameID());
        Assertions.assertEquals(newGame, testGame.game());
    }

    @Test
    public void getGameNoGameTest() throws ResponseException {
        GameData noGame = gameDAO.getGame(1);

        Assertions.assertNull(noGame);
    }

    @Test
    public void clearTest() throws ResponseException {
        String testGameName = "test game";
        int testGameID = gameDAO.createGame(testGameName);
        gameDAO.clear();

        GameData gotGame = gameDAO.getGame(testGameID);
        Assertions.assertNull(gotGame);
    }
}
