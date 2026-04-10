package client;

import chess.*;
import client.websocket.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import ui.ChessDisplay;
import websocket.messages.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlayClient implements ChessClient, NotificationHandler {
    private final ServerFacade server;
    private WebSocketFacade ws;
    private String joinedCol;
    private int gameID;
    private String authToken;
    private ChessGame game;
    private final String url;

    public GamePlayClient(String serverURL) {
        server = new ServerFacade(serverURL);
        this.url = serverURL;
    }

    public void setColor(String color) { joinedCol = color; }

    public void connect(String auth, int gameID) throws ResponseException {
        this.authToken = auth;
        this.gameID = gameID;
        ws = new WebSocketFacade(url, this);
        ArrayList<GameData> games = server.listGames();
        for (GameData g : games) {
            if (g.gameID() == gameID) {
                this.game = g.getChess();
                System.out.println("Found game, connecting.");
            }
        }
        if (game == null) {
            System.out.println("Game not found");
            return;
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
        this.gameID = 0;
        this.joinedCol = null;
        this.authToken = null;
        return "You left the game.";
    }

    private String makeMove(String in) throws ResponseException {
        var inputs = in.split(" ");
        if (inputs.length < 3) {
            return "Enter a start position and end position (e.g. A2 A3).";
        }
        if ((!joinedCol.equals("WHITE")) && (!joinedCol.equals("BLACK"))) {
            return "An observer cannot make a move.";
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
                int startCol = starts.charAt(0) - 'A' + 1;
                int startRow = Character.getNumericValue(starts.charAt(1));
                ChessPosition startPos = new ChessPosition(startRow, startCol);
                ChessPosition endPos = new ChessPosition((end[1]-'0'), (end[0]-'A'+1));
                ChessMove move = new ChessMove(startPos, endPos, null);
                if (game.getBoard().getPiece(startPos).getPieceType() == ChessPiece.PieceType.PAWN) {
                    move = promotePawn(startPos, endPos);
                }
                ws.makeMove(authToken, gameID, move);
                return String.format("You moved your piece at <%s> to <%s>.", starts, ends);
            }
        }
        return "Enter a valid start position and end position (e.g. A2 A3).";
    }

    private ChessMove promotePawn(ChessPosition start, ChessPosition end) {
        if ((end.getRow() == 8) || (end.getRow() == 1)) {
            System.out.println("You can promote this pawn. Choose QUEEN | BISHOP | ROOK | KNIGHT");
            System.out.println("GAMEPLAY >>> ");
            Scanner scanner = new Scanner(System.in);
            String promote = scanner.nextLine().trim();
            switch (promote) {
                case "QUEEN" -> {
                    return new ChessMove(start, end, ChessPiece.PieceType.QUEEN);
                }
                case "BISHOP" -> {
                    return new ChessMove(start, end, ChessPiece.PieceType.BISHOP);
                }
                case "ROOK" -> {
                    return new ChessMove(start, end, ChessPiece.PieceType.ROOK);
                }
                case "KNIGHT" -> {
                    return new ChessMove(start, end, ChessPiece.PieceType.KNIGHT);
                }
            }
        }
        return new ChessMove(start, end, null);
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
            ChessPosition startPos = new ChessPosition((start[1]-'0'), (start[0]-'A'+1));
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
        System.out.println("You are attempting to resign. Are you sure?");
        System.out.println("GAMEPLAY >>> ");
        Scanner scan = new Scanner(System.in);
        String confirm = scan.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            ws.resign(authToken, gameID);
            return "";
        } else {
            return "Continuing game!";
        }

    }

    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case ERROR -> {
                ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println("\n" + SET_TEXT_COLOR_RED + error.getErrorMessage());
            }
            case NOTIFICATION -> {
                NotificationMessage notice = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println("\n" + SET_TEXT_COLOR_MAGENTA + notice.getNotification());
            }
            case LOAD_GAME -> {
                LoadGameMessage load = new Gson().fromJson(message, LoadGameMessage.class);
                loadGame(load);
            }
        }
        System.out.println("\n" + RESET_TEXT_COLOR + "GAMEPLAY >>> ");
    }

    private void loadGame(LoadGameMessage load) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
        Gson gson = gsonBuilder.create();
        game = gson.fromJson(load.getGame(), ChessGame.class);
        ChessDisplay dis = new ChessDisplay(game.getBoard());
        ChessGame.TeamColor disCol;
        if (joinedCol.equals("WHITE")) {
            disCol = ChessGame.TeamColor.WHITE;
        } else {
            disCol = ChessGame.TeamColor.BLACK;
        }
        dis.displayBoard(disCol);
    }
}
