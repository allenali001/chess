package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import service.exceptions.IncorrectAuthTokenException;
import models.AuthData;

public class AuthService {
    private final AuthDAO authDAO;
    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData valAuthToken(String authToken) throws DataAccessException, IncorrectAuthTokenException {
        if (authToken == null || authToken.isBlank()) {
            throw new IncorrectAuthTokenException("Error: No Auth Token Entered");
        }
        AuthData authTok = authDAO.getAuth(authToken);
        if (authTok == null) {
            throw new IncorrectAuthTokenException("Error: Invalid Auth Token");
        }
        return authTok;
    }
}
