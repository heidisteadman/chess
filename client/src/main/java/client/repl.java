package client;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class repl {
    PreloginClient prelog;
    PostloginClient postlog;
    GamePlayClient gameplay;
    State state = State.SIGNEDOUT;

    public repl(String serverURL) {
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
                if (result.contains("You logged in")) {
                    state = State.SIGNEDIN;
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
