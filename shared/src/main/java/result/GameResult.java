package result;

import chess.ChessGame;

public class GameResult {
    private ChessGame game;
    private String message;
    public GameResult(ChessGame game){
        this.game=game;
    }
    public GameResult(String message){
        this.message=message;
    }
    public ChessGame getGame(){
        return game;
    }
    public void setGame(ChessGame  game){
        this.game = game;
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
}
