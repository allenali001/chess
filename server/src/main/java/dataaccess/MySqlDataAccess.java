package dataaccess;

public class MySqlDataAccess implements DataAccess {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public MySqlDataAccess() throws DataAccessException {
        userDAO = new UserDaoSql();
        gameDAO = new GameDaoSql();
        authDAO = new AuthDaoSql();
    }

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
