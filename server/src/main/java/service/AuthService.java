package service;

import dataaccess.AuthDAO;
import service.exceptions.IncorrectAuthTokenException;
import models.AuthData;

public class AuthService {
    private final AuthDAO authDAO = new AuthDAO();

    public AuthData valAuthToken(String authToken) throws IncorrectAuthTokenException {
        if (authToken != null) {
            if (!authToken.isBlank()) {
                AuthData auth = authDAO.getAuth(authToken);
                if (auth == null) {
                    throw new IncorrectAuthTokenException("Error: No Auth Token");
                } else {
                    return auth;
                }
            } else {
                throw new IncorrectAuthTokenException("Error: Incorrect Auth Token");
            }
        } else {
            throw new IncorrectAuthTokenException("Error: Incorrect Auth Token");
        }
    }
}

