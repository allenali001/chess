package server.websocket;

import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.UserGameCommand;
import webSocketMessages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = Serializer.fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthString());
            saveSession(command.getGameID(), session);
            switch (command.type) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }


    private void connect(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s has joined the game", username);
        var notification = new ServerMessage(ServerMessage.Type.NOTIFICATION, message,null);
        connections.broadcast(username,notification);
    }

    private void leave(String username, Session session) throws IOException{
        connections.remove(username);
        var message = String.format("%s has left the game", username);
        var notification = new ServerMessage(ServerMessage.Type.NOTIFICATION,message,null);
        connections.broadcast(username,notification);
        }


}