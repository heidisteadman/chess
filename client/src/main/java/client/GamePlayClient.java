package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import ui.ChessDisplay;

import javax.management.Notification;
import java.util.ArrayList;
import java.util.Collection;
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
        if (Objects.equals(in, "help")) {
            return help();
        }
        var inputs = in.split(" ");
        try {
            return switch (inputs[0]) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> makeMove(in);
                case "highlight" -> highlight(in);
                case "resign" -> resign();
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

    private String leave() throws ResponseException{
        ws.leave(authToken, gameID);
        gameID = 0;
        authToken = null;
        return "You left the game.";
    }

    private String makeMove(String in) throws ResponseException {
        var inputs = in.split(" ");
        if (inputs.length < 3) {
            return "Enter a start position and end position (e.g. A2 A3).";
        }
        String starts = inputs[1];
        char[] start = starts.toCharArray();
        String ends = inputs[2];
        char[] end  = ends.toCharArray();

        if ((start[0] >= 'A') && (start[0] <= 'H') && (start[1] >= '1') && (start[1] <= '8')) {
            if ((end[0] >= 'A') && (end[0] <= 'H') && (end[1] >= '1') && (end[1] <= '8')) {
                if (game.isEnded()) {
                    return "The game has ended, you can leave.";
                }
                ChessPosition startPos = new ChessPosition((start[1]-'0'), (start[0]-'a'+1));
                ChessPosition endPos = new ChessPosition((end[1]-'0'), (end[0]-'a'+1));
                ChessMove move = new ChessMove(startPos, endPos, null);
                ws.makeMove(authToken, gameID, move);
                return String.format("You moved your piece at <%s> to <%s>.", starts, ends);
            }
        }
        return "Enter a valid start position and end position (e.g. A2 A3).";
    }

    private String highlight(String in) {
        var inputs = in.split(" ");
        if (inputs.length < 2) {
            return "Enter a start position (e.g. A1).";
        }
        String starts = inputs[1];
        char[] start = starts.toCharArray();

        if ((start[0] >= 'A') && (start[0] <= 'H') && (start[1] >= '1') && (start[1] <= '8')) {
            if (game.isEnded()) {
                return "The game has ended, you can leave.";
            }
            ChessPosition startPos = new ChessPosition((start[1]-'0'), (start[0]-'a'+1));
            if (game.getBoard().getPiece(startPos) == null) {
                return "No piece at selected position.";
            }
            Collection<ChessMove> moves = game.validMoves(startPos);
            ChessDisplay show  = new ChessDisplay(game.getBoard());
            show.highlightMoves(moves, startPos);
            if (Objects.equals(joinedCol, "WHITE")) {
                show.displayBoard(ChessGame.TeamColor.WHITE);
            } else {
                show.displayBoard(ChessGame.TeamColor.BLACK);
            }
        }
        return "";
    }

    private String resign() throws ResponseException {
        ws.resign(authToken, gameID);
        return "You resigned.";
    }

    public void notify(Notification notification) {}
}
