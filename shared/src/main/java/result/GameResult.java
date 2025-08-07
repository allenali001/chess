package result;

import chess.ChessGame;

public class GameResult {
    private ChessGame game;
    public GameResult(ChessGame game){
        this.game=game;
    }
    public ChessGame getGame(){
        return game;
    }
    public void setGame(ChessGame  game){
        this.game = game;
    }
}
