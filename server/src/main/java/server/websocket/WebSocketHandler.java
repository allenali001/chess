package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.AuthData;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.exceptions.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;


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
            String username = getUsername(command.getAuthToken());
            switch (command.getCommandType()) {
                case CONNECT -> connect(username, session, command.getGameID());
                //case MAKE_MOVE -> makeMove(command, username, session);
                case LEAVE -> leaveGame(username);
                case RESIGN -> resign(username);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, message));
        }catch (Exception ex){
            ex.printStackTrace();
            sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, message));
        }
    }


    private void connect(String username, Session session, int gameID) throws IOException, DataAccessException {
        connections.add(username, session);
        GameData gameData = gameDAO.getGame(gameID);
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.getGame());
        sendMessage(session, loadGame);
        var message = String.format("%s has joined the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }

    private void leaveGame(String username) throws IOException {
        connections.remove(username);
        var message = String.format("%s has left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }

    private void resign(String username) throws IOException {
        var message = String.format("%s has resigned", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }

    private ChessGame getGame(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        return gameData.getGame();
    }

 /*   private void makeMove(UserGameCommand command, String username, Session session) throws IOException {
        try {
            var move = command.getMove();
            ChessGame game = getGame(command.getGameID());
            game.makeMove(move);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null );
            connections.broadcast(username, notification);
        } catch (InvalidMoveException ex) {
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
            session.getRemote().sendString(errorMessage.toString());
        } catch (DataAccessException ex) {
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Could not access server");
            session.getRemote().sendString(errorMessage.toString());
        }
    }
  */

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