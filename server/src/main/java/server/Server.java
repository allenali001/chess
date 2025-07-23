package server;


import dataaccess.*;
import server.handler.*;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        UserDaoMemory userDaoMemory = new UserDaoMemory();
        AuthDAO authDAO = new AuthDaoMemory();
        GameDaoMemory gameDaoMemory = new GameDaoMemory();
        UserService userService=new UserService(userDaoMemory, authDAO);
        ClearService clearService = new ClearService(userDaoMemory,authDAO, gameDaoMemory);
        AuthService authService = new AuthService(authDAO);
        GameService gameService = new GameService(gameDaoMemory, authService);

        // Register your endpoints and handle exceptions here.
        post("/user", new RegisterHandler(userService));
        post("/session", new LoginHandler(userService));
        delete("/session", new LogOutHandler(userService));
        get("/game", new ListGameHandler(gameService));
        post("/game", new CreateGameHandler(gameService));
        put("/game", new JoinGameHandler(gameService));
        delete("/db", new ClearHandler(clearService));


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
