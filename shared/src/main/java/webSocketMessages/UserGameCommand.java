package webSocketMessages;

import chess.ChessMove;
import com.google.gson.Gson;


public record UserGameCommand(Type type, String authToken, Integer gameID, String playerColor, ChessMove move) {
    public enum Type {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
