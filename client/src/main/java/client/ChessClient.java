package client;

import dataaccess.ResponseException;
import model.AuthData;
import model.CreateGameRequest;
import model.UserData;
import server.ServerFacade;
import ui.ChessStyles;

import java.lang.module.ResolutionException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private enum State {
        SIGNEDOUT,
        SIGNEDIN,
        INGAME
    }
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        printIntro();
        repl();
    }

    private void printIntro() {
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.println(WHITE_KING + "Welcome to chess CS 240! Type \"Help\" for available commands." + BLACK_KING);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.println(printHelp());
    }

    private String printHelp() {
        return switch (state) {
            case State.SIGNEDOUT ->
                        """
                        Help - Show available commands
                        Quit - Exit the program
                        Login - Login as existing user
                        Register - Create a new user
                        """;
            case State.SIGNEDIN ->
                        """
                        Help - Show available commands
                        Logout - Logout and return to login screen
                        Create Game <Game name> - Create a new chess game
                        List Games - List all games
                        Play Game <Game number> - Join a chess game as a player
                        Observe Game <Game number> - Join a chess game as an observer
                        """;
            case State.INGAME ->
                        """
                        WIP: Chess game commands coming soon!
                        """;
        };
    }

    private void repl() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            result = eval(input);

            ChessStyles.resetText();
            System.out.println(result);
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + ">>> ");
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            if (state.equals(State.SIGNEDOUT)) {
                return switch (cmd) {
                    case "help" -> printHelp();
                    case "quit" -> "quit";
                    case "login" -> login();
                    case "register" -> register();
                    default -> cmdErrorMessage();
                };
            }
            if (state.equals(State.SIGNEDIN)) {
                return switch (cmd) {
                    case "help" -> printHelp();
                    case "logout" -> logout();
                    case "create" -> createGame(params);
                    case "list" -> listGames(params);
                    case "play" -> playGame(params);
                    case "observe" -> observeGame(params);
                    default -> cmdErrorMessage();
                };
            }
            return "";
        }
        catch (ResponseException ex) {
            return "Error " + ex.code() + ": " + ex.getMessage();
        }
    }

    private String logout() throws ResponseException {
        server.logout(authToken);
        state = State.SIGNEDOUT;
        return "Logged out";
    }

    private String createGame(String[] params) throws ResponseException {
        if (Objects.equals(params[0], "game")) {
            CreateGameRequest gameRequest = new CreateGameRequest(authToken, params[1]);
            server.createGame(gameRequest);
            return "Created game " + params[1];
        } else {
            return cmdErrorMessage();
        }
    }

    private String listGames(String[] params) {
        return "Not implemented";
    }

    private String playGame(String[] params) {
        return "Not implemented";
    }

    private String observeGame(String[] params) {
        return "Not implemented";
    }

    private String login() throws ResponseException {
        try {
            String result = requestSession(true);
            state = State.SIGNEDIN;
            return result;
        }
        catch (ResponseException ex) {
            if (ex.code().equals(ResponseException.Code.Unauthorized)) {
                return "Incorrect username or password";
            }
            throw ex;
        }
    }

    private String register() throws ResponseException {
        String result = requestSession(false);
        state = State.SIGNEDIN;
        return result;
    }

    private String requestSession(boolean onlyLogin) throws ResponseException {
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
        authToken = authData.authToken();
        return "Logged in as " + authData.username();
    }

    private String cmdErrorMessage() {
        return "Invalid command, type \"Help\" for available commands";
    }
}
// TODO: Make errors prettier (server error)