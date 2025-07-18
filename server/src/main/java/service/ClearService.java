package service;

import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class ClearService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();
    public void clearData(){
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
