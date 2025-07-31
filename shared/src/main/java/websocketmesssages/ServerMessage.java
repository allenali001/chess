package websocketmesssages;

import chess.ChessGame;
import com.google.gson.Gson;

public record ServerMessage(Type type, String message, ChessGame game) {
    public enum Type {
        LOAD_GAME,
        NOTIFICATION,
        ERROR
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
