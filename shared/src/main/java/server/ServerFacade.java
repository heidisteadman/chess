package server;

import com.google.gson.Gson;
import exception.ResponseException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import model.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {
    private final String serverURL;
    private final HttpClient client = HttpClient.newHttpClient();
    private record JoinGameRequest(String color, String gameID) {}
    private static String authToken;
    private record CreateGameResponse(int gameID) {}
    private record CreateGameRequest(String gameName) {}
    private record ListGameResponse(ArrayList<GameData> games) {}

    public ServerFacade(String url) {
        this.serverURL = url;
    }

    public AuthData register(UserData u) throws ResponseException {
        var request = buildRequest("POST", "/user", u);
        var response = sendRequest(request);
        AuthData user;
        try {
            user = handleResponse(response, AuthData.class);
        } catch (ResponseException x) {
            throw new ResponseException(403, "Username already taken");
        }

        if (user != null) {
            authToken = user.getToken();
        } else {
            throw new ResponseException(500, "Failed to get token");
        }
        return user;
    }

    public AuthData login(UserData u) throws ResponseException {
        var request = buildRequest("POST", "/session", u);
        var response = sendRequest(request);
        AuthData user;
        try {
            user = handleResponse(response, AuthData.class);
        } catch (ResponseException x) {
            throw new ResponseException(401, "Error: Incorrect username or password.");
        }

        if (user != null) {
            authToken = user.getToken();
        } else {
            throw new ResponseException(500, "Failed to get token");
        }
        return user;
    }

    public void logout() throws ResponseException {
        var request = buildRequest("DELETE", "/session", null);
        sendRequest(request);
    }

    public int createGame(String gameName) throws ResponseException {
        CreateGameRequest req = new CreateGameRequest(gameName);
        var request = buildRequest("POST", "/game", req);
        var response = sendRequest(request);
        CreateGameResponse gameIDres = handleResponse(response, CreateGameResponse.class);
        if (gameIDres != null) {
            return gameIDres.gameID;
        } else {
            throw new ResponseException(500, "did not create game");
        }
    }

    public ArrayList<GameData> listGames() throws ResponseException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        ListGameResponse res = handleResponse(response, ListGameResponse.class);
        if (res != null) {
            return res.games;
        } else {
            return new ArrayList<>();
        }
    }

    public void joinGame(String color, String gameID) throws ResponseException {
        JoinGameRequest join = new JoinGameRequest(color, gameID);
        try {
            var request = buildRequest("PUT", "/game", join);
            sendRequest(request);
        } catch (ResponseException x) {
            throw new ResponseException(500, "Enter a valid gameID and team color.");
        }

    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL+path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception x) {
            throw new ResponseException(500, x.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ResponseException(500, body);
            }

            throw new ResponseException(500, "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
