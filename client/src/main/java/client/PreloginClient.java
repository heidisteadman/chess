package client;

import server.ServerFacade;

import java.util.Objects;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreloginClient implements ChessClient{
    private final ServerFacade server;

    public PreloginClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
    }

    public String eval(String in) {
        if (Objects.equals(in, "quit")) {
            return "quit";
        } else if (Objects.equals(in, "help")) {
            System.out.println(SET_TEXT_COLOR_BLUE + help());
            return help();
        }

        return "nothin' yet";
    }

    public String help() {
        return """
                Options:
                - Register "register" <username> <password> <email>
                - Log in "login" <username> <password>
                - Help "help"
                - Quit "quit"
                """;
    }
}
