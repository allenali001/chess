package dataaccess.DAOs;

import dataaccess.DataAccessException;
import models.AuthData;
import service.exceptions.IncorrectAuthTokenException;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String tok) throws DataAccessException;

    void clear() throws DataAccessException;

    void deleteAuth(String tok) throws IncorrectAuthTokenException, DataAccessException;
}