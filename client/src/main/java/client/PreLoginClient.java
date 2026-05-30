package client;

import dataaccess.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.util.Scanner;

public class PreLoginClient {
    ServerFacade server;
    ChessClient mainClient;

    public PreLoginClient(ChessClient mainClient, ServerFacade server) {
        this.server = server;
        this.mainClient = mainClient;
    }

    public String login() throws ResponseException {
        try {
            String result = requestSession(true);
            mainClient.setState(ChessClient.State.SIGNEDIN);
            return result;
        }
        catch (ResponseException ex) {
            if (ex.code().equals(ResponseException.Code.Unauthorized)) {
                return "Incorrect username or password";
            }
            throw ex;
        }
    }

    public String register() throws ResponseException {
        String result = requestSession(false);
        mainClient.setState(ChessClient.State.SIGNEDIN);
        return result;
    }

    public String requestSession(boolean onlyLogin) throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        String email = null;
        if (!onlyLogin) {
            System.out.print("email: ");
            email = scanner.nextLine();
        }

        UserData requestedUser = new UserData(username, password, email);
        AuthData authData = onlyLogin ? server.login(requestedUser) : server.register(requestedUser);
        mainClient.setAuthToken(authData.authToken());
        return "Logged in as " + authData.username();
    }

    public String logout() throws ResponseException {
        server.logout(mainClient.getAuthToken());
        mainClient.setState(ChessClient.State.SIGNEDOUT);
        return "Logged out";
    }
}
