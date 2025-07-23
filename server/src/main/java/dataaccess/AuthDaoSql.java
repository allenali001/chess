package dataaccess;

import com.google.gson.Gson;
import dataaccess.DAOs.AuthDAO;
import models.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class AuthDaoSql implements AuthDAO {

    public AuthDaoSql() throws DataAccessException {
        configureDatabase();
    }

    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (token, username,json) VALUES(?, ?,?)";
        var token = UUID.randomUUID().toString();
        var auth = new AuthData(username, token);
        var json = new Gson().toJson(auth);
        executeUpdate(statement, token, username, json);
        return auth;

    }

    public AuthData getAuth(String token)throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT token, json FROM auth WHERE token = ?";
            try(var ps =conn.prepareStatement(statement)){
                ps.setString(1,token);
                try(var rs = ps.executeQuery()){
                    if(rs.next()){
                        return readAuth(rs);
                    }
                }
            }
        }catch (SQLException ex){
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
        var username = rs.getString("username");
        var authTok = rs.getString("token");
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }

    public void clear() throws DataAccessException {
        String statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private void executeUpdate(String statement, Object... params) throws
            DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param == null) ps.setNull(i + 1, NULL);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format(
                    "unable to update database: %s, %s", statement, ex.getMessage()));
        }
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

    private void configureDatabase() throws DataAccessException {
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