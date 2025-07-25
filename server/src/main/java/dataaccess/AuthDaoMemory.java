package dataaccess;
import models.AuthData;
import service.exceptions.IncorrectAuthTokenException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AuthDaoMemory implements AuthDAO {
    private final List<AuthData> authdatlist = new ArrayList<>();
    public AuthData createAuth(String username) throws DataAccessException {
        try {
            String authTok = UUID.randomUUID().toString();
            AuthData tok = new AuthData(username, authTok);
            authdatlist.add(tok);
            return tok;
        } catch (Exception Ex) {
            throw new DataAccessException("Create Auth data could not be accessed", Ex);
        }
    }
    public AuthData getAuth(String tok) throws DataAccessException {
        try {
            for (AuthData atok : authdatlist) {
                if (tok.equals(atok.authToken())) {
                    return atok;
                }
            }
            return null;
        } catch (Exception Ex) {
            throw new DataAccessException("Get auth data could not be accessed", Ex);
        }
    }
    public void clear() throws DataAccessException {
        try{
            authdatlist.clear();
        }catch (Exception ex){
            throw new DataAccessException("Could not clear", ex);
        }

    }

    public void deleteAuth(String tok) throws IncorrectAuthTokenException, DataAccessException {
            AuthData authData = getAuth(tok);
            if (authData == null) {
                throw new IncorrectAuthTokenException("Error: Incorrect Auth Token");
            }
            authdatlist.remove(authData);
        }
    }
