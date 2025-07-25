package dataaccess;

import models.UserData;


public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData u) throws DataAccessException;
    void clear()throws DataAccessException;
}
