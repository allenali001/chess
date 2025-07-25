package server.handler;

import dataaccess.DataAccessException;
import server.request.ListGameRequest;
import server.result.ListGameResult;
import service.GameService;
import service.exceptions.IncorrectAuthTokenException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.JsonHelper.toJson;

public class ListGameHandler implements Route {
    private final GameService gameService;
    public ListGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("Authorization");
        Object result;
        try {
            ListGameRequest listGameRequest = new ListGameRequest(authToken);
            ListGameResult listGameResult = gameService.listGame(listGameRequest);
            result = toJson(res, 200, listGameResult);
        } catch (IncorrectAuthTokenException Ex) {
            result = toJson(res, 401, new ListGameResult(null, Ex.getMessage()));
        } catch (DataAccessException ex) {
            res.status(500);
            result = toJson(res, 500, new ListGameResult(null, "Error" + ex.getMessage()));
        }
        return result;
    }
}
