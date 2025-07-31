package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocketmesssages.UserGameCommand;
import websocketmesssages.ServerMessage;
import java.io.IOException;
import models.AuthData;
import service.exceptions.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = getUsername(command.authToken());
            switch (command.type()) {
                case CONNECT -> connect(username, session);
                case MAKE_MOVE -> makeMove(command, username, session);
                case LEAVE -> leaveGame(username);
                case RESIGN -> resign(username);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, new ServerMessage(ServerMessage.Type.ERROR, "Unauthorized", null));
        }catch (Exception ex){
            ex.printStackTrace();
            sendMessage(session, new ServerMessage(ServerMessage.Type.ERROR, "Error"+ex.getMessage(),null ));
        }
    }


    private void connect(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s has joined the game", username);
        var notification = new ServerMessage(ServerMessage.Type.NOTIFICATION, message, null);
        connections.broadcast(username, notification);
    }

    private void leaveGame(String username) throws IOException {
        connections.remove(username);
        var message = String.format("%s has left the game", username);
        var notification = new ServerMessage(ServerMessage.Type.NOTIFICATION, message, null);
        connections.broadcast(username, notification);
    }

    private void resign(String username) throws IOException {
        var message = String.format("%s has resigned", username);
        var notification = new ServerMessage(ServerMessage.Type.NOTIFICATION, message, null);
        connections.broadcast(username, notification);
    }

    private ChessGame getGame(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        return gameData.getGame();
    }

    private void makeMove(UserGameCommand command, String username, Session session) throws IOException {
        try {
            var move = command.move();
            ChessGame game = getGame(command.gameID());
            game.makeMove(move);
            var message = String.format("%s has moved from %s to %s", username, move.getStartPosition(), move.getEndPosition());
            var notification = new ServerMessage(ServerMessage.Type.LOAD_GAME, message, game);
            connections.broadcast(username, notification);
        } catch (InvalidMoveException ex) {
            var errorMessage = new ServerMessage(ServerMessage.Type.ERROR, "Invalid move", null);
            session.getRemote().sendString(errorMessage.toString());
        } catch (DataAccessException ex) {
            var errorMessage = new ServerMessage(ServerMessage.Type.ERROR, "Could not access server", null);
            session.getRemote().sendString(errorMessage.toString());
        }
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(message.toString());
    }

    private String getUsername(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        return authData.username();
    }
}