package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import server.ServerFacade;

import javax.management.Notification;
import java.util.Objects;

public class GamePlayClient implements ChessClient, NotificationHandler {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private String joinedCol;
    private int gameID;
    private String authToken;

    public GamePlayClient(String serverURL) throws ResponseException {
        server = new ServerFacade(serverURL);
        ws = new WebSocketFacade(serverURL, this);
    }

    public void setColor(String color) { joinedCol = color; }

    public void connect(String auth, int gameID) throws ResponseException {
        this.authToken = auth;
        this.gameID = gameID;
        ws.connect(auth, gameID);
    }

    public String eval(String in) {

        if (Objects.equals(in, "quit")) {
            return "quit";
        } else if (Objects.equals(in, "exit")) {
            return "exit";
        }
        var inputs = in.split(" ");
        return switch (inputs[0]) {
            case "redraw" -> redraw(in);
            case "leave" -> leave(in);
            case "move" -> makeMove(in);
            case "highlight" -> highlight(in);
            case "resign" -> resign(in);
            case null, default -> "Invalid input. Type help for options.";
        };
    }

    public String help() {
        return """
                Options:
                - Quit "quit"
                - Help "help"
                - Exit "exit"
                - Redraw "redraw" to redraw the board
                - Leave "leave" to leave the game
                - Make a move "move <start> <end>"
                - Highlight "highlight <start>" to highlight possible moves
                - Resign "resign" to admit defeat
                """;
    }

    private String redraw(String in) {
        return "not implemented";
    }

    private String leave(String in) {
        return "not implemented";
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
