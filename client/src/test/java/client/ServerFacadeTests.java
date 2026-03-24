package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static PreloginClient prelog;
    private static PostloginClient postlog;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String serverURL = "http://localhost:" + port;
        prelog = new PreloginClient(serverURL);
        postlog = new PostloginClient(serverURL);
        prelog.eval("register aUser password email.com");
        postlog.eval("create newGame");
        postlog.eval("logout");
    }

    @AfterAll
    static void stopServer() {
        postlog.eval("clear");
        server.stop();
    }


    @Test
    public void registerTest() {
        String result = prelog.eval("register user name email");
        String expected = "Success! You are logged in as user";
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void registerInvalidTest() {
        prelog.eval("register myName password my@email.com");
        String result = prelog.eval("register myName pass email");
        String expected = "That username is already taken.";
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void loginTest() {
        prelog.eval("register myUser myPass myEmail");
        String result = prelog.eval("login myUser myPass");
        String expected = "Success! You logged in as myUser";
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void loginWrongPasswordTest() {
        String result = prelog.eval("login myUser pass");
        String expected = "Incorrect username or password.";
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void logoutTest() {
        prelog.eval("login myUser myPass");
        String result = postlog.eval("logout");
        String expected = "You have been logged out.";
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void createTest() {
        prelog.eval("login aUser password");
        String result = postlog.eval("create testGame");
        String expected = "Success! Game created. Game ID: ";
        boolean contain= result.contains(expected);
        postlog.eval("logout");
        Assertions.assertTrue(contain);
    }

    @Test
    public void listTest() {
        prelog.eval("login aUser password");
        String result = postlog.eval("list");
        String expected = "Success! Here are the games.";
        postlog.eval("logout");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void joinTest() {
        prelog.eval("login aUser password");
        String result = postlog.eval("join WHITE 1");
        String expected = "Success! You have joined. Switching to Game Play mode.";
        postlog.eval("logout");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void joinInvalidTest() {
        prelog.eval("login aUser password");
        String result = postlog.eval("join WHITE 5");
        String expected = "Failed to join the game. Invalid game ID.";
        postlog.eval("logout");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void observeTest() {
        prelog.eval("login aUser password");
        String result = postlog.eval("observe 1");
        String expected = "Success! You are observing the game.";
        postlog.eval("logout");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void observeInvalidTest() {
        prelog.eval("login aUser password");
        String result = postlog.eval("observe 5");
        String expected = "Enter a valid game ID.";
        postlog.eval("logout");
        Assertions.assertEquals(expected, result);
    }

}
