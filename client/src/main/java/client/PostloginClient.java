package client;

import server.ServerFacade;

public class PostloginClient {
    private final ServerFacade server;

    public PostloginClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
    }

    public String eval(String in) {}
}
