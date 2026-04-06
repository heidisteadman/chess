package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import ui.ChessDisplay;

import javax.management.Notification;
import java.util.ArrayList;
import java.util.Objects;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GamePlayClient implements ChessClient, NotificationHandler {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private String joinedCol;
    private int gameID;
    private String authToken;
    private ChessGame game;

    public GamePlayClient(String serverURL) throws ResponseException {
        server = new ServerFacade(serverURL);
        ws = new WebSocketFacade(serverURL, this);
    }

    public void setColor(String color) { joinedCol = color; }

    public void connect(String auth, int gameID) throws ResponseException {
        this.authToken = auth;
        this.gameID = gameID;
        ArrayList<GameData> games = server.listGames();
        for (GameData g : games) {
            if (g.gameID() == gameID) {
                this.game = g.getChess();
            }
        }
        ws.connect(auth, gameID);
    }

    public String eval(String in) {
        var inputs = in.split(" ");
        try {
            return switch (inputs[0]) {
                case "redraw" -> redraw();
                case "leave" -> leave(in);
                case "move" -> makeMove(in);
                case "highlight" -> highlight(in);
                case "resign" -> resign(in);
                case null, default -> "Invalid input. Type help for options.";
            };
        } catch (ResponseException e) {
            return "The input failed. Try again. " + e.getMessage();
        }
    }

    public String help() {
        return """
                Options:
                - Help "help"
                - Redraw "redraw" to redraw the board
                - Leave "leave" to leave the game
                - Make a move "move <start> <end>"
                - Highlight "highlight <start>" to highlight possible moves
                - Resign "resign" to admit defeat
                """;
    }

    private String redraw() {
        ChessDisplay show = new ChessDisplay(game.getBoard());
        ChessGame.TeamColor col;
        if (Objects.equals(joinedCol, "WHITE")) {
            col = ChessGame.TeamColor.WHITE;
        } else {
            col = ChessGame.TeamColor.BLACK;
        }
        show.displayBoard(col);
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        return "";
    }

    private String leave(String in) throws ResponseException{
        ws.leave(authToken, gameID);
        gameID = 0;
        authToken = null;
        return "You left the game.";
    }

    private String makeMove(String in) {
        return "not implemented";
    }

    private String highlight(String in) {
        return "not implemented";
    }

    private String resign(String in) {
        return "not implemented";
    }

    public void notify(Notification notification) {}
}
