package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
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
                case MAKE_MOVE -> makeMove(command, username, session);
                case LEAVE -> leaveGame(username, command.getGameID());
                case RESIGN -> resign(username, command.getGameID());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private void connect(String username, Session session, int gameID) throws IOException, DataAccessException {
        connections.add(username, session);
        GameData gameData = gameDAO.getGame(gameID);
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.getGame());
        sendMessage(session, loadGame);
        String role;
        if (username.equals(gameData.getBlackUsername())){
            role = "as black team";
        }else if (username.equals(gameData.getWhiteUsername())){
            role = "as white team";
        }else{
            role = "as an observer";
        }
        var message = String.format("%s has joined the game %s", username, role);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }

    private void leaveGame(String username, int gameID) throws IOException, DataAccessException {
        connections.remove(username);
        GameData gameData = gameDAO.getGame(gameID);
        boolean spaceInGame = false;
        if (username.equals(gameData.getWhiteUsername())){
            gameData.setWhiteUsername(null);
            spaceInGame= true;
        }else if (username.equals(gameData.getBlackUsername())){
            gameData.setBlackUsername(null);
            spaceInGame = true;
        }
        if (spaceInGame){
            gameDAO.updateGame(gameData);
        }
        var message = String.format("%s has left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }

    private void resign(String username, int gameID) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.getGame();
        game.closeGame(true);
        gameDAO.updateGame(gameData);
        var message = String.format("%s has resigned", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }

    private void makeMove(UserGameCommand command, String username, Session session) throws IOException {
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame game = gameData.getGame();
            if(game.gameIsOver()){
                var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "This game is over and is no longer playable.");
                session.getRemote().sendString(message.toString());
            return;
            }
            ChessMove move = command.getMove();
            game.makeMove(move);
            gameDAO.updateGame(gameData);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,game);
            connections.broadcast("", notification);
            String message = String.format("%s moved from %s to %s", username, move.getStartPosition(), move.getEndPosition());
            var notification1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(username,notification1);
            ChessGame.TeamColor opponentColor=game.getTeamTurn();
            String opponent = (opponentColor == ChessGame.TeamColor.WHITE) ? gameData.getWhiteUsername() : gameData.getBlackUsername();
            if (game.isInCheck(opponentColor)){
                var checkMessage = String.format("%s is in check", opponent);
                var checkNotif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
                connections.broadcast("", checkNotif);
            }
            if (game.isInCheckmate(opponentColor)) {
                var checkmateMessage = String.format("%s is in checkmate", opponent);
                var checkmateNotif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmateMessage);
                connections.broadcast("", checkmateNotif);
            }
            if (game.isInStalemate(opponentColor)) {
                var stalemateMessage = String.format("%s is in stalemate", opponent);
                var stalemateNotif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, stalemateMessage);
                connections.broadcast("", stalemateNotif);
            }
        } catch (InvalidMoveException ex) {
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
            session.getRemote().sendString(errorMessage.toString());
        } catch (DataAccessException ex) {
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Could not access server");
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