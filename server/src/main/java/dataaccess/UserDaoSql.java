package dataaccess;

import com.google.gson.Gson;
import models.UserData;
import java.sql.SQLException;

import static dataaccess.DaoHelper.executeUpdate;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class UserDaoSql implements UserDAO {

    public UserDaoSql() throws DataAccessException {
        DaoHelper.configureDatabase(CREATE_STATEMENTS);
    }

    private static final String[] CREATE_STATEMENTS = {
            """
    CREATE TABLE IF NOT EXISTS `user` (
        username VARCHAR(256) NOT NULL,
        email VARCHAR(256) NOT NULL,
        json TEXT DEFAULT NULL,
        password VARCHAR(256) NOT NULL,
        PRIMARY KEY (username),
        INDEX (email),
        INDEX (password)
    ) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """
    };


    public void createUser(UserData u) throws DataAccessException {
        String statement = "INSERT INTO `user` (username, email, json, password) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(u);
        int rows = DaoHelper.executeUpdate(statement, u.username(), u.email(), json, u.password());
        if (rows != 1) {
            throw new DataAccessException("Error: Failed to insert user, affected rows: " + rows);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("SELECT json FROM user WHERE username = ?");
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                var json = rs.getString("json");
                return new Gson().fromJson(json, UserData.class);
            }
            return null;
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: Unable to read data: %s", ex.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }
}