package server;

import dataaccess.*;
import server.handler.*;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;


public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;


    public Server(UserService userService, GameService gameService, ClearService clearService){
        this.userService = userService;
        this.gameService = gameService;
        this.clearService= clearService;
    }
    public Server() {
        try {
            var userDAO = new UserDaoSql();
            var authDAO = new AuthDaoSql();
            var gameDAO = new GameDaoSql();

            this.userService = new UserService(userDAO, authDAO);
            this.gameService = new GameService(gameDAO, new AuthService(authDAO));
            this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        } catch (DataAccessException ex) {
            try {
                throw new DataAccessException("Error: could not access server");
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int run(int desiredPort){
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user",new RegisterHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogOutHandler(userService));
        Spark.get("/game", new ListGameHandler(gameService));
        Spark.post("/game", new CreateGameHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService));
        Spark.delete("/db", new ClearHandler(clearService));


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
