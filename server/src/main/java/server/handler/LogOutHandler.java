package server.handler;

import server.result.LogoutResult;
import service.UserService;
import server.request.LogoutRequest;
import service.exceptions.IncorrectAuthTokenException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.jsonHelper.*;


public class LogOutHandler implements Route {
    private final UserService userService = new UserService();

    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            LogoutRequest logoutRequest = fromJson(req, LogoutRequest.class);
            userService.logout(logoutRequest);
            result = toJson(res, 200, new LogoutResult(null));
        } catch (IncorrectAuthTokenException Ex) {
            result = toJson(res, 400, new LogoutResult(Ex.getMessage()));
        }
        return result;
    }
}