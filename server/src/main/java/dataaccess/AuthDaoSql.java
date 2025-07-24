package dataaccess;

import com.google.gson.Gson;
import dataaccess.DAOs.AuthDAO;
import models.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import static dataaccess.DaoHelper.executeUpdate;

public class AuthDaoSql implements AuthDAO {

    public AuthDaoSql() throws DataAccessException {
        DaoHelper.configureDatabase(createStatements);

    }

    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (token, username,json) VALUES(?, ?,?)";
        var token = UUID.randomUUID().toString();
        var auth = new AuthData(username, token);
        var json = new Gson().toJson(auth);
        executeUpdate(statement, token, username, json);
        return auth;
    }

    public AuthData getAuth(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT token, json FROM auth WHERE token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to read data: %s",
                    ex.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String token) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE token=?";
        executeUpdate(statement, token);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }

    public void clear() throws DataAccessException {
        String statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `token` VARCHAR(256) NOT NULL,
              `username` VARCHAR(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`token`),
              INDEX(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}