package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class ConnectionManager {
    HashMap<Integer, HashSet<Session>> sessions;

    public ConnectionManager() {
        sessions = new HashMap<>();
    }

    public void enterGame() {

    }

    public void addSession(Integer gameID, Session session) {
        if (!sessions.containsKey(gameID)) {
            sessions.put(gameID, new HashSet<>());
        }
        sessions.get(gameID).add(session);
    }

    public void deleteSession(Integer gameID, Session session) {
        if (!sessions.containsKey(gameID)) {
            sessions.get(gameID).remove(session);
        }
    }

    public void sendRoot(Session session, Integer gameID, ServerMessage message) {
        String msg = new Gson().toJson(message);
        try {
            session.getRemote().sendString(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(Session session, Integer gameID, ServerMessage message) {
        String msg = new Gson().toJson(message);
        try {
            for (Session otherSession : sessions.get(gameID)) {
                if (!otherSession.equals(session)) {
                    otherSession.getRemote().sendString(msg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
