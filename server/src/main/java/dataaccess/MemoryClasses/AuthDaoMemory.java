package dataaccess.MemoryClasses;
import dataaccess.DAOs.AuthDAO;
import dataaccess.DataAccessException;
import models.AuthData;
import service.exceptions.IncorrectAuthTokenException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AuthDaoMemory implements AuthDAO {
    private final List<AuthData> authdatlist = new ArrayList<>();
    public AuthData createAuth(String username) throws DataAccessException {
        try {
            String tok = UUID.randomUUID().toString();
            AuthData authdat = new AuthData(username, tok);
            authdatlist.add(authdat);
            return authdat;
        } catch (Exception Ex) {
            throw new DataAccessException("Create Auth data could not be accessed", Ex);
        }
    }
    public AuthData getAuth(String tok) throws DataAccessException {
        try {
            for (AuthData atok : authdatlist) {
                if (tok.equals(atok.getAuthToken())) {
                    return atok;
                }
            }
            return null;
        } catch (Exception Ex) {
            throw new DataAccessException("Get auth data could not be accessed", Ex);
        }
    }
    public void clear() throws DataAccessException {
        authdatlist.clear();
    }

    public void deleteAuth(String authtok) throws IncorrectAuthTokenException, DataAccessException {
            AuthData authData = getAuth(authtok);
            if (authData == null) {
                throw new IncorrectAuthTokenException("Error: Incorrect Auth Token");
            }
            authdatlist.remove(authData);
        }
    }
