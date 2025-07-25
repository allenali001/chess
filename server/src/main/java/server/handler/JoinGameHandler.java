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
            JoinGameRequest inrequest = fromJson(req, JoinGameRequest.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, inrequest.gameID(), inrequest.playerColor());

            JoinGameResult serviceResult = gameService.joinGame(joinGameRequest);
            result = toJson(res, 200, serviceResult);
        } catch (IncorrectAuthTokenException ex) {
            result = toJson(res, 401, new JoinGameResult(ex.getMessage()));
        } catch (AlreadyTakenException ex) {
            result = toJson(res, 403, new JoinGameResult(ex.getMessage()));
        }catch (Forbidden | NoGameException ex){
            result = toJson(res, 400, new JoinGameResult(ex.getMessage()));
         }catch (DataAccessException ex){
            res.status(500);
            result= toJson(res, 500, new JoinGameResult("Error" + ex.getMessage()));
         }
        return result;
    }
}

