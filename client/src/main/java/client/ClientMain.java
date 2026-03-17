package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        String serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }

        try {
            new repl(serverURL).run();
        } catch (Throwable x) {
            System.out.printf("Unable to start server: %s%n", x.getMessage());
        }
    }
}
