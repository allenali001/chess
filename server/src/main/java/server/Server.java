package server;


import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.handler.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import server.helper.jsonHelper.*;

import static spark.Spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        UserService userService=new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO,authDAO,gameDAO);

        // Register your endpoints and handle exceptions here.
        post("/user", new RegisterHandler(userService));
        post("/session", new LoginHandler(userService));
        delete("/session", new LogOutHandler(userService));
        //get("/game", new ListGameHandler());
        //post("/game", new CreateGameHandler());
        //put("/game", new JoinGameHandler());
        delete("/db", new ClearHandler(clearService));


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
