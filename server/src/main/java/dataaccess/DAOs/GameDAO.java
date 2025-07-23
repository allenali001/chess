package dataaccess.DAOs;

import dataaccess.DataAccessException;
import models.GameData;

import java.util.List;

public interface GameDAO {
    GameData createGame(String gameName)throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
}
