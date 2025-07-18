package service;

import models.AuthData;
import models.GameData;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGameRequest;
import server.result.CreateGameResult;
import server.result.ListGameResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.IncorrectAuthTokenException;
import dataaccess.GameDAO;
import service.exceptions.NoGameException;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO=new GameDAO();
    public CreateGameResult create(CreateGameRequest createGameRequest) throws IncorrectAuthTokenException {
        AuthService authService;
        authService = new AuthService();
        authService.valAuthToken(createGameRequest.authToken());
        GameData game;
        game = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResult(game.getGameID(),null);
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws IncorrectAuthTokenException, AlreadyTakenException, NoGameException {
        AuthService authService;
        authService = new AuthService();
        AuthData authData;
        authData = authService.valAuthToken(joinGameRequest.authToken());
        String username = authData.getUsername();
        GameData game;
        game = gameDAO.getGame(joinGameRequest.gameID());
        if (game != null) {
            String color;
            color = joinGameRequest.playerColor();
            if (color.equals("WHITE")) {
                if (game.getWhiteUsername() == null) {
                   game.setWhiteUsername(username);
                } else {
                    throw new AlreadyTakenException("Error: This color is already taken by another player");
                }
            } else {
                if (color.equals("BLACK")) {
                    if (game.getBlackUsername() == null) {
                        game.setBlackUsername(username);
                    }else{
                        throw new AlreadyTakenException("Error: This color is already taken by another player");
                    }
                }
            }
        } else {
            throw new NoGameException("Error: Game ID does not exist");
        }
    }
    public ListGameResult listGame(ListGameRequest listGamerRequest) throws IncorrectAuthTokenException{
        AuthService authService;
        authService = new AuthService();
        authService.valAuthToken(listGamerRequest.authToken());
        List<GameData> games;
        games = gameDAO.listGames();
        return new ListGameResult(games,null);
    }
}
