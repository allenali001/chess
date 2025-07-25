package dataaccess;

import com.google.gson.Gson;
import dataaccess.daos.GameDAO;
import models.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static dataaccess.DaoHelper.executeUpdate;


public class GameDaoSql implements GameDAO {
    private final Random random = new Random();
    public GameDaoSql() throws DataAccessException {
        String[] createStatements = {
                """
    CREATE TABLE IF NOT EXISTS game (
    `id` INT NOT NULL,
    `gameName` VARCHAR(256) NOT NULL,
    `json` TEXT DEFAULT NULL,
     PRIMARY KEY (`id`),
    INDEX(`gameName`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
        };
        DaoHelper.configureDatabase(createStatements);
    }
    public GameData createGame(String gameName) throws DataAccessException {
        int gameID;
        while (true) {
            gameID = random.nextInt(9000) + 1000;
            if (getGame(gameID) == null) {
                break;
            }
        }
        GameData newGame = new GameData(gameID, null, null, gameName, null);
        var statement = "INSERT INTO game(id, gameName, json) VALUES (? , ? , ?)";
        var json = new Gson().toJson(newGame);
        executeUpdate(statement, gameID, gameName, json);
        return newGame;
    }
        public GameData getGame(int gameID) throws DataAccessException{
            try (var conn = DatabaseManager.getConnection()){
                var statement = "SELECT id, json FROM game WHERE id = ?";
                try(var ps = conn.prepareStatement(statement)){
                    ps.setInt(1,gameID);
                    try(var rs = ps.executeQuery()){
                        if (rs.next()){
                            return readGame(rs);
                        }
                    }
                }
            }catch (SQLException ex){
                throw new DataAccessException(String.format("Unable to read data: %s", ex.getMessage()));
            }
            return null;
        }
    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, GameData.class);
        game.setID(rs.getInt("id"));
        return game;
    }

    public List<GameData> listGames() throws DataAccessException{
        var result = new ArrayList<GameData>();
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT id, json FROM game";
            try(var ps = conn.prepareStatement(statement)){
                try(var rs = ps.executeQuery()){
                    while(rs.next()){
                        result.add(readGame(rs));
                    }
                }
            }
        }catch(SQLException ex){
            throw new DataAccessException(String.format("Unable to read data: %s", ex.getMessage()));
        }
        return result;
    }


    public void clear() throws DataAccessException{
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }
    public void updateGame(GameData game) throws DataAccessException{
        String statement = "UPDATE game SET whiteusername = ?, blackusername=?," +
                " gameName=?, json = ?, gameID = ?";
        String json = new Gson().toJson(game);
        try{
            executeUpdate(statement, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), json,game.getGameID());
            }
        catch(Exception ex){
            throw new DataAccessException("Failed to update game data" , ex);
        }
    }


}
