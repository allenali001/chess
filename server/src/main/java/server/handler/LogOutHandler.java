package server.handler;

import dataaccess.DataAccessException;
import result.LogoutResult;
import service.UserService;
import service.exceptions.IncorrectAuthTokenException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.JsonHelper.*;


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
        }catch(DataAccessException ex){
            res.status(500);
            result = toJson(res, 500, new LogoutResult("Error" + ex.getMessage()));
        }
        return result;
    }
}