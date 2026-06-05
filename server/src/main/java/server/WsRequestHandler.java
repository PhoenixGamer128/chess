package server;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import model.GameData;
import model.ResponseException;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        if (!userService.validAuthToken(command.getAuthToken())) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        switch (command.getCommandType()) {
            case CONNECT -> enterGame(command, ctx.session);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    private void enterGame(UserGameCommand command, Session session) {
        connections.addSession(command.getGameID(), session);

        var loadMessage = createLoadMessage(command.getAuthToken(), command.getGameID());

        var notificationMessage =
                String.format("%s %s", getUsername(command.getAuthToken()), enterMessage(getPlayerType(command)));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setServerMessage(notificationMessage);

        connections.sendRoot(session, command.getGameID(), loadMessage);
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
        return gameService.listGames(authToken).games().get(gameID-1);
    }

    private ServerMessage createLoadMessage(String authToken, Integer gameID) {
        var loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadMessage.setCurrentGameState(getCurrentGameState(authToken, gameID));
        return loadMessage;
    }

    private ChessGame getCurrentGameState(String authToken, Integer gameID) {
        return gameService.listGames(authToken).games().get(gameID-1).game();
    }

    private String getUsername(String authToken) {
        return userService.getUser(authToken).username();
    }
}
