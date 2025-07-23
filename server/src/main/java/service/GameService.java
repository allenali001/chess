package service;

import dataaccess.DataAccessException;
import models.AuthData;
import models.GameData;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGameRequest;
import server.result.CreateGameResult;
import server.result.JoinGameResult;
import server.result.ListGameResult;
import service.exceptions.*;
import dataaccess.MemoryClasses.GameDaoMemory;

import java.util.List;

public class GameService {
    private final GameDaoMemory gameDaoMemory;
    private final AuthService authService;

    public GameService(GameDaoMemory gameDaoMemory, AuthService authService) {
        this.gameDaoMemory = gameDaoMemory;
        this.authService = authService;
    }
    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws IncorrectAuthTokenException, DataAccessException,
            MissingParameterException {
        authService.valAuthToken(authToken);
        if (request == null || request.gameName() == null || request.gameName().isBlank()) {
            throw new MissingParameterException("Error: Missing a parameter");
        }
        GameData game = gameDaoMemory.createGame(request.gameName());
        return new CreateGameResult(game.getGameID(), null);
    }
    public void joinGame(JoinGameRequest joinGameRequest)
            throws DataAccessException, IncorrectAuthTokenException,
            AlreadyTakenException, NoGameException, Forbidden {
        AuthData authData = authService.valAuthToken(joinGameRequest.authToken());
        String username = authData.getUsername();
        GameData game = gameDaoMemory.getGame(joinGameRequest.gameID());
        if (game == null) {
            throw new NoGameException("Error: No game with this GameID exists");
        }
        String color;
        color = joinGameRequest.playerColor();
        if (!"WHITE".equals(color) && !"BLACK".equals(color)) {
            throw new Forbidden("Error: Invalid Color Entered");

        }
        if (color.equals("WHITE")) {
            if (game.getWhiteUsername() == null) {
                game.setWhiteUsername(username);
            } else {
                throw new AlreadyTakenException("Error: This color is already taken by another player");
            }
        } else {
            if (game.getBlackUsername() == null) {
                game.setBlackUsername(username);
            } else {
                throw new AlreadyTakenException
                        ("Error: This color is already taken by another player");
            }
        }
        new JoinGameResult(null);
    }
   public ListGameResult listGame(ListGameRequest listGamerRequest) throws IncorrectAuthTokenException, DataAccessException {
        authService.valAuthToken(listGamerRequest.authToken());
        List<GameData> games;
        games = gameDaoMemory.listGames();
        return new ListGameResult(games,null);
    }
}
