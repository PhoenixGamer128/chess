package server;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("WebSocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        ctx.send("Websocket Response: " + ctx.message());
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }
}
