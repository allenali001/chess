package models;

import chess.ChessGame;

public class GameData {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private final String gameName;
    private final ChessGame game;
    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game=game;
    }
    public int getGameID(){
        return gameID;
    }
    public String getWhiteUsername(){return whiteUsername;}
    public String getBlackUsername(){
        return blackUsername;
    }
    public String getGameName(){
        return gameName;
    }
    public ChessGame getGame(){
        return game;
    }
    public void setWhiteUsername(String username){
        this.whiteUsername = username;
    }
    public void setBlackUsername(String username){
        this.blackUsername = username;
    }
    public void setID(int gameID) {this.gameID = gameID;}
}
