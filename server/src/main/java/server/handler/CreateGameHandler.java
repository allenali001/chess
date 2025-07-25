package server.handler;

import dataaccess.DataAccessException;
import server.request.CreateGameRequest;
import server.result.CreateGameResult;
import service.GameService;
import service.exceptions.IncorrectAuthTokenException;
import service.exceptions.MissingParameterException;
import spark.Request;
import spark.Response;
import spark.Route;
import static server.helper.JsonHelper.fromJson;
import static server.helper.JsonHelper.toJson;


public class CreateGameHandler implements Route {
    private final GameService gameService;
    public CreateGameHandler(GameService gameService){
        this.gameService=gameService;
    }
    @Override
    public Object handle(Request req, Response res){
        Object result;
        try{
            String authToken = req.headers("Authorization");
            CreateGameRequest createGameRequest = fromJson(req,CreateGameRequest.class );
            CreateGameResult createGameResult = gameService.createGame(authToken,createGameRequest);
            result = toJson(res, 200, createGameResult);
        }catch (IncorrectAuthTokenException ex){
            result = toJson(res,401, new CreateGameResult(null,ex.getMessage()));
        }catch (MissingParameterException ex){
            result = toJson(res,400, new CreateGameResult(null, ex.getMessage()));
        }catch (DataAccessException ex){
            res.status(500);
            result=toJson(res, 500, new CreateGameResult(null,"Error" + ex.getMessage()));
        }
        return result;
    }
}
