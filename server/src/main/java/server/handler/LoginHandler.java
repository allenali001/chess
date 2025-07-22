package server.handler;
import dataaccess.DataAccessException;
import server.request.LoginRequest;
import service.exceptions.MissingParameterException;
import spark.Request;
import spark.Response;
import service.UserService;
import spark.Route;
import server.result.LoginResult;
import service.exceptions.WrongPassWordException;
import service.exceptions.NoSuchUserException;
import static server.helper.JsonHelper.fromJson;
import static server.helper.JsonHelper.toJson;


public class LoginHandler implements Route {
    private final UserService userService;
    public LoginHandler(UserService userService){
        this.userService=userService;
    }
    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            LoginRequest loginRequest = fromJson(req, LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);
            result = toJson(res, 200, loginResult);
        }catch(WrongPassWordException | NoSuchUserException Ex) {
            result = toJson(res, 401, new LoginResult(null, null, Ex.getMessage()));
        }catch (MissingParameterException Ex){
            result = toJson(res, 400, new LoginResult(null,null,Ex.getMessage()));
        }catch (DataAccessException Ex){
            result = toJson(res, 500, new LoginResult(null,null, Ex.getMessage()));
        }
        return result;
    }
}
