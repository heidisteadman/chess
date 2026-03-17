package client;

import ui.EscapeSequences;

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
    }


}
