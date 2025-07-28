package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String playerUsername, Session session) {
        var connection = new Connection(playerUsername, session);
        connections.put(playerUsername, connection);
    }

    public void remove(String playerUsername) {
        connections.remove(playerUsername);
    }

    public void broadcast(String excludeUsername, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.playerUsername.equals(excludeUsername)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.playerUsername);
        }
    }
}