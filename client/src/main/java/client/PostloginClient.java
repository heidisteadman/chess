package client;

import exception.ResponseException;
import model.GameList;
import server.ServerFacade;

import java.util.Objects;

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
        }

        return "Invalid. Type help to see possible commands.";
    }

    public String help() {
        return """
                Options:
                - Logout "logout"
                - Create game "create" <game name>
                - List games "list"
                - Join a game "join" <game ID> <WHITE | BLACK>
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
            int gameID = server.create(gameName);
            if (gameID == 1) {
                return "Unable to create a gameID. Try again.";
            } else if (gameID == 2) {
                return "Did not create a gameID. Try again.";
            } else {
                return ("Success! Game created. Game ID: " + gameID);
            }
        } catch (ResponseException x) {
            return ("Failed to create game. " + x.getMessage());
        }
    }

    private String listGames() {
        try {
            GameList games = server.listGames();
            String listGame = games.toString();
            return ("Success! Here are the games: " + listGame);
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

        try {
            server.joinGame(color, gameID);
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
        try {
            server.joinGame("WHITE", gameID);
            return "Success! You have joined the game.";
        } catch (ResponseException x) {
            return ("Failed to join the game. " + x.getMessage());
        }
    }


}
