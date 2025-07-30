package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import models.AuthData;
import models.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGameResult;
import service.exceptions.*;


import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;

    public GameService(GameDAO gameDao, AuthService authService) {
        this.gameDAO = gameDao;
        this.authService = authService;
    }
    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws IncorrectAuthTokenException, DataAccessException,
            MissingParameterException {
        authService.valAuthToken(authToken);
        if (request == null || request.gameName() == null || request.gameName().isBlank()) {
            throw new MissingParameterException("Error: Missing a parameter");
        }
        GameData game = gameDAO.createGame(request.gameName());
        return new CreateGameResult(game.getGameID(), null);
    }
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest)
            throws DataAccessException, IncorrectAuthTokenException,
            AlreadyTakenException, NoGameException, Forbidden {
        AuthData authData = authService.valAuthToken(joinGameRequest.authToken());
        String username = authData.username();
        GameData game = gameDAO.getGame(joinGameRequest.gameID());
        if (game == null) {
            throw new NoGameException("Error: No game with this GameID exists");
        }
        String color;
        color = joinGameRequest.playerColor();
        if (!"WHITE".equalsIgnoreCase(color) && !"BLACK".equalsIgnoreCase(color)) {
            throw new Forbidden("Error: Invalid Color Entered");
        }
        if ("white".equalsIgnoreCase(color)) {
            if (game.getWhiteUsername() == null) {
                game.setWhiteUsername(username);
            } else {
                throw new AlreadyTakenException("Error: This color is already taken by another player");
            }
        } else if ("black".equalsIgnoreCase(color)){
            if (game.getBlackUsername() == null) {
                game.setBlackUsername(username);
            } else {
                throw new AlreadyTakenException
                        ("Error: This color is already taken by another player");
            }
        }
        gameDAO.updateGame(game);
        return new JoinGameResult(null);
    }
   public ListGameResult listGame(ListGameRequest listGamerRequest) throws IncorrectAuthTokenException, DataAccessException {
        authService.valAuthToken(listGamerRequest.authToken());
        List<GameData> games;
        games = gameDAO.listGames();
        return new ListGameResult(games,null);
    }
}
