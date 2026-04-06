package client;

import exception.ResponseException;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Repl {
    PreloginClient prelog;
    PostloginClient postlog;
    GamePlayClient gameplay;
    State state = State.SIGNEDOUT;

    public Repl(String serverURL) throws ResponseException {
        prelog = new PreloginClient(serverURL);
        postlog = new PostloginClient(serverURL);
        gameplay = new GamePlayClient(serverURL);
    }

    public void run() {
        ChessClient client = prelog;
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to Chess. Choose an option to start.");
        System.out.println(SET_TEXT_COLOR_BLUE + client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!Objects.equals(result, "quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result.contains("logged in") || result.contains("Game created") || result.contains("Here are") || result.contains("left")){
                    state = State.SIGNEDIN;
                    client = postlog;
                    System.out.println(client.help());
                } else if (result.contains("logged out") || result.contains("Database cleared")) {
                    state = State.SIGNEDOUT;
                    client = prelog;
                    System.out.println(client.help());
                } else if (result.contains("Switching to Game")) {
                    gameplay.setColor(postlog.getColor());
                    state = State.GAMEPLAY;
                    client = gameplay;
                    System.out.println(client.help());
                }
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String stateString;
        switch (state) {
            case SIGNEDIN -> stateString = "SIGNED IN";
            case GAMEPLAY -> stateString = "GAMEPLAY";
            default -> stateString = "SIGNED OUT";
        }
        System.out.println("\n" + RESET_TEXT_COLOR + stateString + " >>> ");
    }

}
