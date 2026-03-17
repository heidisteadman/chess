package client;

public class PreloginClient implements ChessClient{
    public PreloginClient(String serverURL) {}
    public String eval(String in) {
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
