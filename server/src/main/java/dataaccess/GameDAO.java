package dataaccess;
import models.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameDAO {
    private final List<GameData> gameDataList = new ArrayList<>();
    private final Random rand = new Random();

    public GameData createGame(String gameName){
        int gameID;
        while (true){
            gameID = rand.nextInt(9000)+1000;
            if (getGame(gameID) == null){
                break;
            }
        }
        GameData newGame = new GameData(gameID,
                null,
                null,
                gameName,
                null);
        gameDataList.add(newGame);
        return newGame;
    }
    public GameData getGame(int gameID){
        return gameDataList.stream().filter(g -> gameID == g.getGameID()).findFirst().orElse(null);
    }
    public void clear(){
        gameDataList.clear();
    }
    public List<GameData> listGames(){
        return new ArrayList<>(gameDataList);
    }
    public void updateGame(GameData updated){
        int listSize;
        listSize = gameDataList.size();
        int i = 0;
        while (true) {
            if (i >= listSize) {
                break;
            }
            if (updated.getGameID() == gameDataList.get(i).getGameID()){
                gameDataList.set(i,updated);
                return;
            }
            i += 1;
        }
    }


}
/*
 createGame: Create a new game.
        getGame: Retrieve a specified game with the given game ID.
        listGames: Retrieve all games.
        updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
clear
 */