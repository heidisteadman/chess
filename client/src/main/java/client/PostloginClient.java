package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import ui.ChessDisplay;

import java.util.ArrayList;
import java.util.Objects;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostloginClient implements ChessClient{
    private final ServerFacade server;

    public PostloginClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
    }

    public String eval(String in) {
        if (Objects.equals(in, "quit")) {
            return "quit";
        } else if (Objects.equals(in, "help")) {
            return help();
        }

        var inputs = in.split(" ");
        if (Objects.equals(inputs[0], "logout")) {
            return logout();
        } else if (Objects.equals(inputs[0], "create")) {
            return create(in);
        } else if (Objects.equals(inputs[0], "list")) {
            return listGames();
        } else if (Objects.equals(inputs[0], "join")) {
            return joinGame(in);
        } else if (Objects.equals(inputs[0], "observe")) {
            return observe(in);
        } else if (Objects.equals(inputs[0], "clear")) {
            return clear();
        }

        return "Invalid. Type help to see possible commands.";
    }

    public String help() {
        return """
                Options:
                - Logout "logout"
                - Create game "create" <game name>
                - List games "list"
                - Join a game "join" <WHITE | BLACK> <game ID>
                - Observe a game "observe" <game ID>
                - Help "help"
                - Quit "quit"
                """;
    }

    private String logout() {
        try {
            server.logout();
            return "You have been logged out.";
        } catch (ResponseException x) {
            return ("Failed to log out. " + x.getMessage());
        }
    }

    private String create(String in) {
        var inputs = in.split(" ");
        if (inputs.length != 2) {
            return "Enter a game name.";
        }

        String gameName = inputs[1];
        try {
            int gameID = server.createGame(gameName);
            return ("Success! Game created. Game ID: " + gameID);
        } catch (ResponseException x) {
            return ("Failed to create game. " + x.getMessage());
        }
    }

    private String listGames() {
        try {
            ArrayList<GameData> games = server.listGames();
            for (GameData game : games) {
                System.out.println("Game name: " + game.gameName());
                System.out.println("Game ID: " + game.gameID());
                ChessDisplay show = new ChessDisplay(game.getChess().gameBoard);
                show.displayBoard(ChessGame.TeamColor.WHITE);
                System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            }

            return ("Success! Here are the games.");
        } catch (ResponseException x) {
            return ("Failed to get games. " + x.getMessage());
        }
    }

    private String joinGame(String in) {
        var inputs = in.split(" ");
        if (inputs.length != 3) {
            return "Enter a color <WHITE or BLACK> and a game ID.";
        }

        String color = inputs[1];
        String gameID = inputs[2];

        int id;
        try {
            id = Integer.parseInt(gameID);
        } catch (Throwable x) {
            return "Enter a valid game ID.";
        }

        if (!Objects.equals(color, "BLACK") && !Objects.equals(color, "WHITE")) {
            return "Enter a valid team color <WHITE | BLACK>";
        }

        ChessGame.TeamColor teamCol;
        if (Objects.equals(color, "BLACK")) {
            teamCol = ChessGame.TeamColor.BLACK;
        } else {
            teamCol = ChessGame.TeamColor.WHITE;
        }

        try {
            ArrayList<GameData> games = server.listGames();
            GameData game = null;
            for (GameData g : games) {
                if (g.gameID() == id) {
                    game = g;
                }
            }
            if (game != null) {
                server.joinGame(color, gameID);
                ChessDisplay show = new ChessDisplay(game.getChess().gameBoard);
                show.displayBoard(teamCol);
                System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            } else {
                throw new ResponseException(500, "Invalid game ID.");
            }
            return "Success! You have joined. Switching to Game Play mode.";
        } catch (ResponseException x) {
            return ("Failed to join the game. " + x.getMessage());
        }
    }

    private String observe(String in) {
        var inputs = in.split(" ");
        if (inputs.length != 2) {
            return "Enter a gameID to observe.";
        }

        String gameID = inputs[1];
        int id;
        try {
            id = Integer.parseInt(gameID);
        } catch (Throwable x) {
            return "Enter a valid game ID.";
        }
        try {
            server.joinGame("WHITE", gameID);
            ArrayList<GameData> games = server.listGames();
            GameData game = null;
            for (GameData g : games) {
                if (g.gameID() == id) {
                    game = g;
                }
            }

            if (game != null) {
                ChessDisplay show = new ChessDisplay(game.getChess().gameBoard);
                show.displayBoard(ChessGame.TeamColor.WHITE);
                System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            } else {
                return "Enter a valid game ID.";
            }
            return "Success! You have joined the game.";
        } catch (ResponseException x) {
            return ("Failed to join the game. " + x.getMessage());
        }
    }

    private String clear() {
        try {
            server.clear();
            return ("Database cleared. Logging out.");
        } catch (ResponseException x) {
            return ("Failed to clear database. " + x.getMessage());
        }
    }


}
