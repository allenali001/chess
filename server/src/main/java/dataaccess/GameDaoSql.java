package dataaccess;

import com.google.gson.Gson;
import models.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class GameDaoSql implements GameDAO {
    public GameDaoSql() throws DataAccessException {
        configureDatabase(CreateStatements);
    }
    private static final String[] CreateStatements = {
            """
    CREATE TABLE IF NOT EXISTS game (
    `id` INT AUTO_INCREMENT NOT NULL,
    `gameName` VARCHAR(256) NOT NULL,
    `whiteUsername` VARCHAR(256),
    `blackUsername` VARCHAR(256),
    `json` TEXT DEFAULT NULL,
     PRIMARY KEY (`id`),
    INDEX(`gameName`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    public GameData createGame(String gameName) throws DataAccessException {
        String insert = "INSERT INTO game(gameName) VALUES (?)";
        int gameID;
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(insert, RETURN_GENERATED_KEYS)){
             ps.setString(1,gameName);
             ps.executeUpdate();
             try(var rs = ps.getGeneratedKeys()) {
                 if (rs.next()) {
                     gameID = rs.getInt(1);
                 } else {
                     throw new DataAccessException("Error: no game id");
                 }
             }
        }catch (SQLException ex){
                 throw new DataAccessException("Error: can't create game");
    }
        GameData newGame = new GameData(gameID, null, null, gameName, null);
             String json = new Gson().toJson(newGame);
             String n = "UPDATE game SET json = ? WHERE id = ?";
             executeUpdate(n,json,gameID);
             return newGame;
    }
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM game WHERE id = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", ex.getMessage()));
        }
        return null;
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            var game = new Gson().fromJson(json, GameData.class);
            game.setID(rs.getInt("id"));
            return game;
        } catch (Exception ex) {
            throw new SQLException("Error: Failure" + json, ex);
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(true);
            var statement = "SELECT id, json FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", ex.getMessage()));
        }
        return result;
    }


    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }
    public void updateGame(GameData game) throws DataAccessException {
        String statement = "UPDATE game SET whiteUsername = ?, blackUsername=?," +
                " gameName=?, json = ? WHERE id = ?";
        String json = new Gson().toJson(game);
        try {
            executeUpdate(statement, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), json, game.getGameID());
        } catch (Exception ex) {
            throw new DataAccessException("Error: Failed to update game data", ex);
        }
    }
    public int executeUpdate(String statement, Object... params) throws
            DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case null -> ps.setNull(i + 1, NULL);
                    default -> {
                    }
                }
            }
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format(
                    "unable to update database: %s, %s", statement, ex.getMessage()));
        }
    }

    public static void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format(
                    "Unable to configure database: %s", ex.getMessage()));
        }
    }
}
