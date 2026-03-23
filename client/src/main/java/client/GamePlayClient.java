package client;

import server.ServerFacade;

import java.util.Objects;

public class GamePlayClient implements ChessClient {
    private final ServerFacade server;

    public GamePlayClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public String eval(String in) {
        if (Objects.equals(in, "quit")) {
            return "quit";
        } else if (Objects.equals(in, "exit")) {
            return "exit";
        }

        return "nothin' yet";
    }

    public String help() {
        return """
                Options:
                - Quit "quit"
                - Help "help"
                - Exit "exit"
                """;
    }
}
