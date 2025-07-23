package dataaccess.MemoryClasses;
import dataaccess.DAOs.GameDAO;
import dataaccess.DataAccessException;
import models.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameDaoMemory implements GameDAO {
    private final List<GameData> gameDataList = new ArrayList<>();
    private final Random rand = new Random();

    public GameData createGame(String gameName)throws DataAccessException {
        try {
            int gameID;
            while (true) {
                gameID = rand.nextInt(9000) + 1000;
                if (getGame(gameID) == null) {
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
        }catch (Exception Ex){
            throw new DataAccessException("Create game data could not be accessed", Ex);
        }
    }
    public GameData getGame(int gameID) throws DataAccessException{
        try {
            return gameDataList.stream().filter(g -> gameID == g.getGameID()).findFirst().orElse(null);
        }catch(Exception Ex){
            throw new DataAccessException("Get game data could not be accessed");
        }
    }
    public void clear() throws DataAccessException {
        try {
            gameDataList.clear();
        }catch(Exception Ex){
            throw new DataAccessException("Clear could not be accessed", Ex);
        }
    }
    public List<GameData> listGames() throws DataAccessException {
        try {
            return new ArrayList<>(gameDataList);
        }catch(Exception Ex){
            throw new DataAccessException("List game data could not be accessed", Ex);
        }
    }

}
