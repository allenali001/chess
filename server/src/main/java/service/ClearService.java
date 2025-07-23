package service;

import dataaccess.DAOs.AuthDAO;
import dataaccess.MemoryClasses.UserDaoMemory;
import dataaccess.MemoryClasses.GameDaoMemory;
import service.exceptions.FailureToClearException;

public class ClearService {
    private final UserDaoMemory userDaoMemory;
    private final AuthDAO authDAO;
    private final GameDaoMemory gameDaoMemory;

    public ClearService(UserDaoMemory userDaoMemory, AuthDAO authDAO, GameDaoMemory gameDaoMemory) {
        this.userDaoMemory = userDaoMemory;
        this.authDAO = authDAO;
        this.gameDaoMemory = gameDaoMemory;
    }

    public void clear() throws FailureToClearException {
        try {
            userDaoMemory.clear();
            authDAO.clear();
            gameDaoMemory.clear();
        } catch (Exception Ex) {
            throw new FailureToClearException("Failed to clear");
        }
    }
}