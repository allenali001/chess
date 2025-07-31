package ui.websocket;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocketmesssages.UserGameCommand;
import websocketmesssages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(String authToken, int gameID, String playerColor) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.Type.CONNECT, authToken,gameID,playerColor,null);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void leaveGame(String authToken, int gameID, String playerColor) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.Type.LEAVE, authToken,gameID,playerColor,null);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void resignFromGame(String authToken, int gameID, String playerColor) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.Type.RESIGN, authToken,gameID,playerColor,null);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void makeMove(String authToken,int gameID, String playerColor, ChessMove move) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.Type.MAKE_MOVE, authToken, gameID, playerColor,move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}

