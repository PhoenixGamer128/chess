package client;

import dataaccess.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.lang.module.ResolutionException;
import java.util.Arrays;
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
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.println(WHITE_KING + "Welcome to chess CS 240! Type \"Help\" for available commands." + BLACK_KING);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.println(printHelp(State.SIGNEDOUT));
    }

    private String printHelp(State currentState) {
        return switch (currentState) {
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
                        Create Game - Create a new chess game
                        List Games - List all games
                        Play Game - Join a chess game as a player
                        Observe Game - Join a chess game as an observer
                        """;
            case State.INGAME ->
                        """
                        WIP: Chess game commands coming soon!
                        """;
            default ->
                "A bug was encountered, please type \"Quit\" and restart the program.";
        };
    }

    private void repl() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();
            result = eval(input);
            System.out.println(result);
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_DARK_GREY + ">>>");
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "help" -> printHelp(state);
            case "quit" -> "quit";
            case "login" -> login();
            default -> "Invalid command, type \"Help\" for available commands";
        };
    }

    private String login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();

        UserData requestedUser = new UserData(username, password, null);
        try {
            AuthData authData = server.login(requestedUser);
            authToken = authData.authToken();
            return "Logged in as " + authData.username();
        }
        catch (ResponseException ex) {
            if (ex.code().equals(ResponseException.Code.Unauthorized)) {
                return "Username or password incorrect";
            }
            return "Error " + ex.code() + ": " + ex.getMessage();
        }
    }
}
// TODO: Make errors prettier (server error)