package server;

import com.google.gson.Gson;import exception.ResponseException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import model.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.*;

public class ServerFacade {
    private final String serverURL;
    private HttpClient client = HttpClient.newHttpClient();

    public ServerFacade(String url) {
        this.serverURL = url;
    }

    public UserData register(UserData u) throws ResponseException {
        var request = buildRequest("POST", "/user", u);
        var response = sendRequest(request);
        return handleResponse(response, UserData.class);
    }

    public UserData login(UserData u) throws ResponseException {
        var request = buildRequest("POST", "/session", u);
        var response = sendRequest(request);
        return handleResponse(response, UserData.class);
    }

    public void logout() throws ResponseException {
        var request = buildRequest("DELETE", "/session", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL+path))
                .method(method, makeRequestBody(body));
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
