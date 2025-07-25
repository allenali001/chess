package service;

import dataaccess.daos.AuthDAO;
import dataaccess.daos.GameDAO;
import dataaccess.daos.UserDAO;
import service.exceptions.FailureToClearException;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws FailureToClearException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (Exception Ex) {
            throw new FailureToClearException("Failed to clear");
        }
    }
}