package server.handler;

import server.result.LogoutResult;
import service.UserService;
import service.exceptions.IncorrectAuthTokenException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.jsonHelper.*;


public class LogOutHandler implements Route {
    private final UserService userService;
    public LogOutHandler(UserService userService){
        this.userService=userService;
    }
    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            String authToken = req.headers("Authorization");
            userService.logout(authToken);
            result = toJson(res, 200, new LogoutResult(null));
        } catch (IncorrectAuthTokenException Ex) {
            result = toJson(res, 401, new LogoutResult(Ex.getMessage()));
        }
        return result;
    }
}