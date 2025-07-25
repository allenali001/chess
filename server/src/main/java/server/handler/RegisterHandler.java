package server.handler;

import dataaccess.DataAccessException;
import server.request.RegisterRequest;
import server.result.RegisterResult;
import service.UserService;
import service.exceptions.AlreadyTakenException;
import service.exceptions.MissingParameterException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.JsonHelper.fromJson;
import static server.helper.JsonHelper.toJson;

public class RegisterHandler implements Route {
    private final UserService userService;
    public RegisterHandler(UserService userService){
        this.userService=userService;
    }
    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            RegisterRequest registerRequest = fromJson(req, RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            result = toJson(res, 200, registerResult);
        } catch (AlreadyTakenException Ex) {
            result = toJson(res, 403, new RegisterResult(null, null, Ex.getMessage()));
        }catch (MissingParameterException Ex){
            result = toJson(res,400, new RegisterResult(null,null,Ex.getMessage()));
        }catch (DataAccessException ex){
            res.status(500);
            result = toJson(res, 500, new RegisterResult(null,null,"Error" + ex.getMessage()));
        }
        return result;
    }
}
