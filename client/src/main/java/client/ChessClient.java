package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.ResponseException;
import server.ServerFacade;
import ui.ChessStyles;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.*;

import static ui.EscapeSequences.*;

public class ChessClient implements NotificationHandler {
    public enum State {
        SIGNEDOUT,
        SIGNEDIN,
        INGAME
    }
    public enum UserType {
        WHITE,
        BLACK,
        OBSERVER
    }
    private final ServerFacade server;
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final InGameClient inGameClient;
    private final WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private HashMap<Integer, Integer> gameIDList = new HashMap<>();
    private int currentGameID;
    private UserType currentUserType;
    private ChessGame currentGameState;

    static Logger logger = Logger.getLogger("myLogger");

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);

        preLoginClient = new PreLoginClient(this, server);
        postLoginClient = new PostLoginClient(this, server);
        inGameClient = new InGameClient(this, server);
    }

    public void run() {
        printIntro();
        repl();
    }

    public void notify(ServerMessage notification) {
        if (!notification.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
            if (notification.getCurrentGameState() != null) {
                currentGameState = notification.getCurrentGameState();
                System.out.print(printBoard(currentGameState) + "\n" + SET_TEXT_COLOR_LIGHT_GREY + ">>> ");
            }
        }
        if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            System.out.println(SET_TEXT_COLOR_BLUE + notification.getMessage());
            printPrompt();
        }
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
                    Play Game <Game number> <White/Black> - Join a chess game as a player
                    Observe Game <Game number> - Join a chess game as an observer
                    """;
            case State.INGAME ->
                    """
                    exit: ---
                    help: ---
                    redraw chess board: ---
                    make move <position> <position>
                    """;
        };
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
                    case "help", "" -> printHelp();
                    case "quit" -> "quit";
                    case "login" -> preLoginClient.login();
                    case "register" -> preLoginClient.register();
                    default -> cmdErrorMessage();
                };
            }
            else if (state.equals(State.SIGNEDIN)) {
                return switch (cmd) {
                    case "help", "" -> printHelp();
                    case "logout" -> preLoginClient.logout();
                    case "create" -> postLoginClient.createGame(params);
                    case "list" -> postLoginClient.listGames(params);
                    case "play" -> postLoginClient.playGame(params);
                    case "observe" -> postLoginClient.observeGame(params);
                    default -> cmdErrorMessage();
                };
            }

            else {
                return switch (cmd) {
                  case "exit", "leave" -> exitGame();
                  case "help", "" -> printHelp();
                  case "redraw" -> printBoard(currentGameState);
                    default -> cmdErrorMessage();
                };
            }
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String enterGame() {
        inGameClient.enterGame(authToken, currentGameID);
        return "";
    }

    public String printBoard(ChessGame boardState) {

        return inGameClient.showBoard(boardState);
    }

    private String exitGame() {
        inGameClient.exitGame(authToken, currentGameID);
        state = State.SIGNEDIN;
        return "Exited game";
    }

    public static String cmdErrorMessage() {
        return "Invalid command, type \"Help\" for available commands";
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setGameIDList(HashMap<Integer, Integer> gameIDList) {
        this.gameIDList = gameIDList;
    }

    public HashMap<Integer, Integer> getGameIDList() {
        return this.gameIDList;
    }

    public void setCurrentGameID(int gameID) {
        currentGameID = gameID;
    }

    public void setCurrentUserType(UserType userType) {
        currentUserType = userType;
    }

    public UserType getCurrentUserType() {
        return currentUserType;
    }

    public WebSocketFacade getWs() {return ws;}
}