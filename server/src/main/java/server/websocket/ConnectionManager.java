package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<Integer, Map<String,Connection>> trackGameConnections = new ConcurrentHashMap<>();

    public void add(String playerUsername, int gameID, Session session) {
        trackGameConnections.putIfAbsent(gameID,new ConcurrentHashMap<>());
        trackGameConnections.get(gameID).put(playerUsername, new Connection(playerUsername,session));
    }

    public void remove(String playerUsername, int gameID) {
        var connections = trackGameConnections.get(gameID);
        connections.remove(playerUsername);
    }

    public void broadcast(String excludeUsername, int gameID, ServerMessage notification) throws IOException {
        var connections = trackGameConnections.get(gameID);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.playerUsername.equals(excludeUsername)) {
                    c.send(notification.toString());
                }
            }
        }
    }
    public void sendError(String username, int gameID, String errorMessage){
        var game = trackGameConnections.get(gameID);
        if (game == null) return;
        var connection = game.get(username);
        if(connection!=null&&connection.session.isOpen()){
            try{
                ServerMessage error = ServerMessage.error(errorMessage);
                connection.send(error.toString());
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}