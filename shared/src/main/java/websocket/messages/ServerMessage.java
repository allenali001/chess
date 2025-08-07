package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String message;
    ChessGame game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.message = message;
    }
    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
    }
    public static ServerMessage error(String message){
        return new ServerMessage(ServerMessageType.ERROR, message);
    }
    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }
    public String getMessage() { return this.message; }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }
    public String toString() {
        return new Gson().toJson(this);
    }

@Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
