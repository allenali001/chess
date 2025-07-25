package dataaccess;

public interface DataAccess {
    UserDAO getUserDAO();
    GameDAO getGameDAO();
    AuthDAO getAuthDAO();
}
