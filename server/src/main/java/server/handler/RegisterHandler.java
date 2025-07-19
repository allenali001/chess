package server.handler;

import server.request.RegisterRequest;
import server.result.RegisterResult;
import service.UserService;
import service.exceptions.AlreadyTakenException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.jsonHelper.fromJson;
import static server.helper.jsonHelper.toJson;

public class RegisterHandler implements Route {
    private final UserService userService = new UserService();

    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            RegisterRequest registerRequest = fromJson(req, RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            result = toJson(res, 200, registerResult);
        } catch (AlreadyTakenException Ex) {
            result = toJson(res, 401, new RegisterResult(null, null, Ex.getMessage()));
        }
        return result;
    }
}
