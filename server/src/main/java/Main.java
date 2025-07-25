import dataaccess.AuthDaoSql;
import dataaccess.daos.AuthDAO;
import dataaccess.daos.GameDAO;
import dataaccess.daos.UserDAO;
import dataaccess.GameDaoSql;
import dataaccess.memoryClasses.AuthDaoMemory;
import dataaccess.memoryClasses.GameDaoMemory;
import dataaccess.memoryClasses.UserDaoMemory;
import dataaccess.UserDaoSql;
import server.Server;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args){
            try {
                var port = 8080;
                if (args.length >= 1) {
                    port = Integer.parseInt(args[0]);
                }

                UserDAO userDAO = new UserDaoMemory();
                GameDAO gameDAO = new GameDaoMemory();
                AuthDAO authDAO = new AuthDaoMemory();
                if (args.length >= 2 && args[1].equals("sql")) {
                    userDAO = new UserDaoSql();
                    gameDAO = new GameDaoSql();
                    authDAO= new AuthDaoSql();
                }

                var authService = new AuthService(authDAO);
                var gameService =new GameService(gameDAO, authService);
                var userService = new UserService(userDAO, authDAO);
                var clearService = new ClearService(userDAO,authDAO,gameDAO);
                var server = new Server(userService,gameService, clearService);
                port=server.run(port);
                System.out.printf("Server started on port %d with UserDAO: %s, GameDAO: %s, AuthDAO: %s%n", port,
                        userDAO.getClass().getSimpleName(),
                        gameDAO.getClass().getSimpleName(),
                authDAO.getClass().getSimpleName());
                return;
            } catch (Throwable ex) {
                System.out.printf("Unable to start server: %s%n", ex.getMessage());
            }
            System.out.println("""
                Chess Server:
                java ServerMain <port> [sql]
                """);
    }
}