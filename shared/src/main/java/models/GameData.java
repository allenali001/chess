package models;

import chess.ChessGame;

public class GameData {
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private  ChessGame game;
    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game=game;
    }
    public void setGame(ChessGame game) { this.game = game;}
    public Integer getGameID(){
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
    public void setID(Integer gameID) {this.gameID = gameID;}
    public GameData(){}
}
