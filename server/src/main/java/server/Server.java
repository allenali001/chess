package server;

import dataaccess.memoryClasses.AuthDaoMemory;
import dataaccess.memoryClasses.GameDaoMemory;
import dataaccess.memoryClasses.UserDaoMemory;
import server.handler.*;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;


public class Server {
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    public Server() {
        this.userService = new UserService(new UserDaoMemory(), new AuthDaoMemory());
        this.gameService = new GameService(new GameDaoMemory(), new AuthService(new AuthDaoMemory()));
        this.clearService = new ClearService(new UserDaoMemory(), new AuthDaoMemory(), new GameDaoMemory());
    }

    public Server(UserService userService, GameService gameService, ClearService clearService){
        this.userService = userService;
        this.gameService = gameService;
        this.clearService= clearService;
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
