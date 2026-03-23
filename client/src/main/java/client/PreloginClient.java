package client;

import exception.ResponseException;
import model.AuthData;
import server.ServerFacade;
import model.UserData;
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

        var inputs = in.split(" ");
        if (Objects.equals(inputs[0], "register")) {
            return register(in);
        } else if (Objects.equals(inputs[0], "login")) {
            return login(in);
        } else {
            return "Invalid input. Type `help` for your options.";
        }

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

    private String register(String in) {
        var inputs = in.split(" ");
        if (inputs.length != 4) {
            return "Enter a username, password, and email.";
        }
        String user = inputs[1];
        String pass = inputs[2];
        String email = inputs[3];
        UserData u = new UserData(user, pass, email);
        try {
            AuthData logged = server.register(u);
            return ("Success! You are logged in as " + logged.username());
        } catch (ResponseException x) {
            if (x.getCode() == 403) {
                return "That username is already taken.";
            } else {
                return ("Unable to register. " + x.getMessage());
            }
        }
    }

    private String login(String in) {
        var inputs = in.split(" ");
        if (inputs.length != 3) {
            return "Enter a username and a password.";
        }
        String user = inputs[1];
        String pass = inputs[2];
        UserData u = new UserData(user, pass, "");
        try {
            AuthData logged = server.login(u);
            return ("Success! You logged in as " + logged.username());
        } catch (ResponseException x) {
            if (x.getCode() == 401) {
                return "Incorrect username or password.";
            } else {
                return ("Unable to log in. " + x.getMessage());
            }
        }
    }
}
