package server.handler;

import dataaccess.DataAccessException;
import result.ClearResult;
import service.ClearService;
import service.exceptions.FailureToClearException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.JsonHelper.toJson;

public class ClearHandler implements Route {
    private final ClearService clearService;
    public ClearHandler(ClearService clearService){
        this.clearService=clearService;
    }
    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            clearService.clear();
            return toJson(res, 200, new Object());
        } catch (FailureToClearException  | DataAccessException ex) {
            res.status(500);
            result = toJson(res, 500, new ClearResult("Error" + ex.getMessage()));
        }
        return result;
    }
}
