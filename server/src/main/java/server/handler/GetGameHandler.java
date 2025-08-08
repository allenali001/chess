package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import result.GameResult;
import service.GameService;
import service.exceptions.IncorrectAuthTokenException;
import service.exceptions.NoGameException;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.helper.JsonHelper.toJson;

public class GetGameHandler implements Route {
    private final GameService gameService;

    public GetGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            int gameID = Integer.parseInt(req.params(":id")); // from /game/:id

            var game = gameService.getGame(authToken, gameID);
            return toJson(res, 200, new GameResult(game));
        } catch (IncorrectAuthTokenException e) {
            return toJson(res, 401, new GameResult(e.getMessage()));
        } catch (NoGameException e) {
            return toJson(res, 404, new GameResult(e.getMessage()));
        } catch (DataAccessException e) {
            return toJson(res, 500, new GameResult("Server error: " + e.getMessage()));
        } catch (Exception e) {
            return toJson(res, 400, new GameResult("Bad request: " + e.getMessage()));
        }
    }
}
