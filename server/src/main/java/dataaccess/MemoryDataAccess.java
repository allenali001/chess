package dataaccess;

public class MemoryDataAccess implements DataAccess {
    private final UserDAO userDAO = new UserDaoMemory();
    private final GameDAO gameDAO = new GameDaoMemory();
    private final AuthDAO authDAO = new AuthDaoMemory();

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public GameDAO getGameDAO() {
        return gameDAO;
    }

    @Override
    public AuthDAO getAuthDAO() {
        return authDAO;
    }
}
