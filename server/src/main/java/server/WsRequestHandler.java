package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.ChessPlayerInfo;
import org.eclipse.jetty.websocket.api.Session;
import model.GameData;
import model.ResponseException;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;

public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private enum PlayerType {
        WHITE,
        BLACK,
        OBSERVER
    }

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;
    private final GameService gameService;

    public WsRequestHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("WebSocket connected");

        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            if (!userService.validAuthToken(command.getAuthToken())) {
                throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
            }
            switch (command.getCommandType()) {
                case CONNECT -> enterGame(command, ctx.session);
                case LEAVE -> leaveGame(command, ctx.session);
                case MAKE_MOVE -> makeMove(command, ctx.session);
                case RESIGN -> resignGame(command, ctx.session);
            }
        } catch (Exception ex) {
            var serverErrorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            serverErrorMessage.setErrorMessage(ex.getMessage());
            connections.sendRoot(ctx.session, serverErrorMessage);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket closed");
    }

    private void enterGame(UserGameCommand command, Session session) throws IOException {
        connections.addSession(command.getGameID(), session);

        var loadMessage = createLoadMessage(command.getAuthToken(), command.getGameID());

        var notificationMessage =
                String.format("%s %s", getUsername(command.getAuthToken()), enterMessage(getPlayerType(command)));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(notificationMessage);

        connections.sendRoot(session, loadMessage);
        connections.broadcast(session, command.getGameID(), notification);
    }

    private String enterMessage(PlayerType type) {
        return switch(type) {
            case WHITE -> "has joined as a white player";
            case BLACK -> "has joined as a black player";
            case OBSERVER -> "has joined as an observer";
        };
    }

    private PlayerType getPlayerType(UserGameCommand command) {
        GameData gameInfo = getGameData(command.getAuthToken(), command.getGameID());
        String username = getUsername(command.getAuthToken());
        if (gameInfo.whiteUsername() != null && gameInfo.whiteUsername().equals(username)) {
            return PlayerType.WHITE;
        }
        else if (gameInfo.blackUsername() != null && gameInfo.blackUsername().equals(username)) {
            return PlayerType.BLACK;
        }
        else {
            return PlayerType.OBSERVER;
        }
    }

    private GameData getGameData(String authToken, Integer gameID) {
        return gameService.getGame(authToken, gameID);
    }

    private ServerMessage createLoadMessage(String authToken, Integer gameID) {
        var loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadMessage.setCurrentGameState(getCurrentGameState(authToken, gameID));
        return loadMessage;
    }

    private ChessGame getCurrentGameState(String authToken, Integer gameID) {
        return gameService.getGame(authToken, gameID).game();
    }

    private String getUsername(String authToken) {
        return userService.getUser(authToken).username();
    }

    private void leaveGame(UserGameCommand command, Session session) throws IOException {
        connections.deleteSession(command.getGameID(), session);
        if (!getPlayerType(command).equals(PlayerType.OBSERVER)) {
            gameService.leaveGame(command.getAuthToken(), command.getGameID());
        }

        String notificationMessage = String.format("%s has left", getUsername(command.getAuthToken()));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(notificationMessage);

        connections.broadcast(session, command.getGameID(), notification);
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException {
        try {
            ChessMove move = command.getMove();
            ChessGame game = gameService.getGame(command.getAuthToken(), command.getGameID()).game();
            GameData gameData = getGameData(command.getAuthToken(), command.getGameID());

            ChessGame.TeamColor playerColor =
                    getPlayerType(command) == PlayerType.WHITE ?
                            ChessGame.TeamColor.WHITE :
                            ChessGame.TeamColor.BLACK;
            ChessGame.TeamColor enemyColor =
                    playerColor.equals(ChessGame.TeamColor.WHITE) ?
                            ChessGame.TeamColor.BLACK :
                            ChessGame.TeamColor.WHITE;

            String playerUsername = playerColor == ChessGame.TeamColor.WHITE ?
                    gameData.whiteUsername() :
                    gameData.blackUsername();
            String enemyUsername = playerColor == ChessGame.TeamColor.WHITE ?
                    gameData.blackUsername() :
                    gameData.whiteUsername();

            ChessPlayerInfo playerInfo = new ChessPlayerInfo(playerUsername, enemyUsername, playerColor, enemyColor);

            // bar user from making moves in checkmate
            if (game.isInCheckmate(playerColor) || game.isInCheckmate(enemyColor)) {
                sendErrorMessage(session, "Error: Game has ended");
                return;
            }

            // make move
            if (playerColor.equals(game.getTeamTurn())
                    && game.validMoves(move.getStartPosition()).contains(move)
                    && !game.getGameResigned()) {
                game.makeMove(move);
                gameService.updateBoard(command.getGameID(), game);
                var loadMessage = createLoadMessage(command.getAuthToken(), command.getGameID());
                connections.sendAll(session, command.getGameID(), loadMessage);

                var moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                moveMessage.setMessage(String.format(
                        "%s made the move %s",
                        playerInfo.playerUsername(),
                        convertMoveToString(move)));
                connections.broadcast(session, command.getGameID(), moveMessage);

                if (game.isInCheckmate(enemyColor)) {
                    var checkmateMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                    checkmateMessage.setMessage(String.format("%s is in checkmate", enemyUsername));
                    connections.sendAll(session, command.getGameID(), checkmateMessage);
                }
            } else {
                throw new InvalidMoveException();
            }
        }
        catch (InvalidMoveException ex) {
            sendErrorMessage(session, "Error: Invalid move");
        }
    }

    private String convertMoveToString(ChessMove move) {
        StringBuilder builder = new StringBuilder();
        String[] letterCoords = {"a","b","c","d","e","f","g","h"};
        Arrays.asList(letterCoords).get(0);
        builder.append(Arrays.asList(letterCoords).get(move.getStartPosition().getColumn()-1));
        builder.append(move.getStartPosition().getRow());
        builder.append(" ");
        builder.append(Arrays.asList(letterCoords).get(move.getEndPosition().getColumn()-1));
        builder.append(move.getEndPosition().getRow());
        return builder.toString();
    }

    private void resignGame(UserGameCommand command, Session session) throws IOException {
        ChessGame game = gameService.getGame(command.getAuthToken(), command.getGameID()).game();
        if (!getPlayerType(command).equals(PlayerType.OBSERVER)) {
            if (game.getGameResigned()) {
                sendErrorMessage(session, "Error: game has already been resigned");
                return;
            }
            game.setGameResigned(true);
        } else {
           sendErrorMessage(session, "Error: Cannot resign as an observer");
            return;
        }
        gameService.updateBoard(command.getGameID(), game);

        String notificationMessage = String.format("%s has resigned", getUsername(command.getAuthToken()));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(notificationMessage);

        connections.sendAll(session, command.getGameID(), notification);
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        var moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        moveError.setErrorMessage(errorMessage);
        connections.sendRoot(session, moveError);
    }
}
//TODO: Combine unnecessary duplicate code
