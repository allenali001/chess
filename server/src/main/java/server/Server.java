package server;

import dataaccess.*;
import server.handler.*;
import server.websocket.WebSocketHandler;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;


public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private WebSocketHandler webSocketHandler;



    public Server(UserService userService, GameService gameService, ClearService clearService, WebSocketHandler webSocketHandler){
        this.userService = userService;
        this.gameService = gameService;
        this.clearService= clearService;
        this.webSocketHandler = webSocketHandler;
    }
    public Server() {
        try {
            var userDAO = new UserDaoSql();
            var authDAO = new AuthDaoSql();
            var gameDAO = new GameDaoSql();

            this.userService = new UserService(userDAO, authDAO);
            this.gameService = new GameService(gameDAO, new AuthService(authDAO));
            this.clearService = new ClearService(userDAO, authDAO, gameDAO);
            this.webSocketHandler = new WebSocketHandler(gameDAO,authDAO);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error: Could not access server", ex);
        }
    }

    public int run(int desiredPort){
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);
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
