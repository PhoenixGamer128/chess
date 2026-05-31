package server;

import com.google.gson.Gson;
import model.ResponseException;
import model.*;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userRequest) throws ResponseException {
        var request = buildRequest("POST", "/user", userRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData userRequest) throws ResponseException {
        var request = buildRequest("POST", "/session", userRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameList joinGame(JoinGameRequest joinRequest) {
        var request = buildRequest("PUT", "/game", joinRequest.joinGameData(), joinRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public GameID createGame(CreateGameRequest requestedGame) {
        var request = buildRequest("POST", "/game", requestedGame, requestedGame.authToken());
        var response = sendRequest(request);
        return handleResponse(response, GameID.class);
    }

    public GameList listGames(String authToken) {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", "", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest (String method, String path, Object body, String header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (header != null) {
            request.header("authorization", header);
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
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.DataAccess, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                // The method returns a new ResponseException object
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
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
