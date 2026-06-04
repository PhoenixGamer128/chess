package websocket;

import websocket.commands.UserGameCommand;

public record WebSocketRequest(UserGameCommand.CommandType commandType, String authToken, Integer gameID) {
}
