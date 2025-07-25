package dataaccess;

import com.google.gson.Gson;
import dataaccess.daos.UserDAO;
import models.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DaoHelper.executeUpdate;


public class UserDaoSql implements UserDAO {

    public UserDaoSql() throws DataAccessException {
        String[] createStatements = {
                """
    CREATE TABLE IF NOT EXISTS user (
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
        DaoHelper.configureDatabase(createStatements);
    }

    public void createUser(UserData u) throws DataAccessException {
            String statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
            String json = new Gson().toJson(u);
            try{
            executeUpdate(statement, u.getUsername(), u.getPassword(), u.getEmail(), json);
        }catch (Exception ex) {
                throw new DataAccessException("Unable to create user data: %s" , ex);

            }
    }
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json, password FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to read data: %s", ex.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        UserData user =new Gson().fromJson(json, UserData.class);
        String password = rs.getString("password");
        if(user!=null){
            if (password!=null){
                user = new UserData(user.getUsername(), password, user.getEmail());
            }
        }
        return user;
    }

    public void clear() throws DataAccessException{
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

}
