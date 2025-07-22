package server.handler;

import dataaccess.DataAccessException;
import server.request.JoinGameRequest;
import server.result.JoinGameResult;
import service.GameService;
import service.exceptions.AlreadyTakenException;
import service.exceptions.Forbidden;
import service.exceptions.IncorrectAuthTokenException;
import service.exceptions.NoGameException;
import spark.Request;
import spark.Response;
import spark.Route;
import static server.helper.JsonHelper.fromJson;
import static server.helper.JsonHelper.toJson;


public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public Object handle(Request req, Response res) {
        Object result;
        try {
            String authToken = req.headers("Authorization");
            JoinGameRequest joinGameRequest = fromJson(req, JoinGameRequest.class);
            joinGameRequest = new JoinGameRequest(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
            gameService.joinGame(joinGameRequest);
            result = toJson(res, 200, new JoinGameResult(null));
        } catch (IncorrectAuthTokenException Ex) {
            result = toJson(res, 401, new JoinGameResult(Ex.getMessage()));
        } catch (AlreadyTakenException Ex) {
            result = toJson(res, 403, new JoinGameResult(Ex.getMessage()));
        }catch (Forbidden | NoGameException Ex){
            result = toJson(res, 400, new JoinGameResult(Ex.getMessage()));
         }catch (DataAccessException Ex){
            result= toJson(res, 500, new JoinGameResult( Ex.getMessage()));
         }
        return result;
    }
}

