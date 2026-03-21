package client;

import server.ServerFacade;

public class GamePlayClient implements ChessClient {
    private final ServerFacade server;

    public GamePlayClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public String eval(String in) {
        return "nothin' yet";
    }

    public String help() {
        return """
                Options:
                - Quit "quit"
                - Help "help"
                """;
    }
}
