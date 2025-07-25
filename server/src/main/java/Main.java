import dataaccess.*;
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
                DataAccess dataAccess = new MemoryDataAccess();
                if (args.length >= 2 && args[1].equalsIgnoreCase("sql")) {
                    dataAccess = new MySqlDataAccess();
                }

                UserDAO userDAO = dataAccess.getUserDAO();
                GameDAO gameDAO = dataAccess.getGameDAO();
                AuthDAO authDAO = dataAccess.getAuthDAO();

                var authService = new AuthService(authDAO);
                var clearService = new ClearService(userDAO, authDAO, gameDAO);
                var gameService = new GameService(gameDAO, authService);
                var userService = new UserService(userDAO, authDAO);

                port = new Server(userService, gameService, clearService).run(port);
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