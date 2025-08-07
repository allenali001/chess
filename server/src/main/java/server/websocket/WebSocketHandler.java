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
import service.exceptions.GameOverException;
import service.exceptions.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
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
        }catch (UnauthorizedException ex){
            var msg = new ErrorMessage("Error: Unauthorized");
            session.getRemote().sendString(msg.toString());
        }
    }


    private void connect(String username, Session session, int gameID) throws IOException, DataAccessException {
        connections.add(username, session);
        GameData gameData;
        try {
            gameData = gameDAO.getGame(gameID);
        }catch(DataAccessException ex){
            connections.sendError(username,"Could not connect to database");
            return;
        }
        if (gameData == null){
            connections.sendError(username,"No game data");
            return;
        }
        ChessGame game = gameData.getGame();
        var loadGame = new LoadGameMessage(game);
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
        var notification = new NotificationMessage(message);
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
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }

    private void resign(String username, int gameID) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.getGame();
        game.closeGame(true);
        gameDAO.updateGame(gameData);
        var message = String.format("%s has resigned", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }

    private void makeMove(UserGameCommand command, String username, Session session) throws IOException {
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame game = gameData.getGame();
            if (game.gameIsOver()) {
                var message = new ErrorMessage("This game is over and is no longer playable");
                session.getRemote().sendString(message.toString());
                return;
            }
            String white = gameData.getWhiteUsername();
            String black = gameData.getBlackUsername();
            if (!username.equals(white) && !username.equals(black)) {
                var message = new ErrorMessage("Observers cannot make moves in the game");
                session.getRemote().sendString(message.toString());
                return;
            }
            ChessMove move = command.getMove();
            try {
                game.makeMove(move);
                gameDAO.updateGame(gameData);
                var loadMessage = new LoadGameMessage(game);
                connections.broadcast("", loadMessage);
                String message = String.format("%s moved from %s to %s", username, move.getStartPosition(), move.getEndPosition());
                var notification1 = new NotificationMessage(message);
                connections.broadcast(username, notification1);
                ChessGame.TeamColor opponentColor = game.getTeamTurn();
                String opponent = (opponentColor == ChessGame.TeamColor.WHITE) ? gameData.getWhiteUsername() : gameData.getBlackUsername();
                if (game.isInCheck(opponentColor)) {
                    game.closeGame(true);
                    gameDAO.updateGame(gameData);
                    var checkMessage = String.format("%s is in check", opponent);
                    var checkNotif = new NotificationMessage(checkMessage);
                    connections.broadcast("", checkNotif);
                    return;
                }
                if (game.isInCheckmate(opponentColor)) {
                    var checkmateMessage = String.format("%s is in checkmate", opponent);
                    var checkmateNotif = new NotificationMessage(checkmateMessage);
                    connections.broadcast("", checkmateNotif);
                    return;
                }
                if (game.isInStalemate(opponentColor)) {
                    game.closeGame(true);
                    gameDAO.updateGame(gameData);
                    var stalemateMessage = String.format("%s is in stalemate", opponent);
                    var stalemateNotif = new NotificationMessage(stalemateMessage);
                    connections.broadcast("", stalemateNotif);
                }
            } catch (InvalidMoveException ex) {
                var errorMessage = new ErrorMessage("Invalid move");
                session.getRemote().sendString(errorMessage.toString());
            } catch (DataAccessException ex) {
                var errorMessage = new ErrorMessage("Could not access server");
                session.getRemote().sendString(errorMessage.toString());
            }
        }catch (DataAccessException ex){
            var errorMsg= new ErrorMessage("Could not access server");
            session.getRemote().sendString(errorMsg.toString());
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