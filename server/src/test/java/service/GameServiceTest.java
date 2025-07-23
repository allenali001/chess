package service;

import dataaccess.*;
import models.AuthData;
import models.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGameRequest;
import server.result.CreateGameResult;
import server.result.ListGameResult;

import static org.junit.jupiter.api.Assertions.*;
class GameServiceTest {
    private GameDaoMemory gameDaoMemory;
    private GameService gameService;
    private String authTok;

    @BeforeEach
    void setUp() throws DataAccessException {
        UserDaoMemory userDaoMemory = new UserDaoMemory();
        gameDaoMemory = new GameDaoMemory();
        AuthDAO authDAO = new AuthDaoMemory();
        AuthService authService = new AuthService(authDAO);
        gameService = new GameService(gameDaoMemory, authService);
        userDaoMemory.createUser(new UserData("username","password","email"));
        AuthData authData = authDAO.createAuth("username");
        authTok=authData.getAuthToken();
    }

    @Test
    void createGamePositive() throws Exception {
        CreateGameRequest req = new CreateGameRequest("gameName");
        CreateGameResult result = gameService.createGame(authTok, req);
        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.gameID() <= 0);
    }

    @Test
    void createGameException() {
        Exception ex= assertThrows(Exception.class, () -> {
            gameService.createGame(authTok, new CreateGameRequest(null));
        });
        System.out.println(ex.getMessage());
    }
    @Test
    void joinGamePositive() throws Exception {
        int gameID = gameService.createGame(authTok, new CreateGameRequest("gameName")).gameID();
        JoinGameRequest joinReq = new JoinGameRequest(authTok, gameID, "BLACK");
        gameService.joinGame(joinReq);
        assertEquals("username", gameDaoMemory.getGame(gameID).getBlackUsername());
    }

    @Test
    void joinGameException() {
        Exception ex= assertThrows(Exception.class, () -> {
            gameService.joinGame(new JoinGameRequest(authTok, 1234,"BLACK")); // Invalid game ID
        });
        System.out.println(ex.getMessage());
    }

    @Test
    void listGamePositive() throws Exception {
        gameService.createGame(authTok, new CreateGameRequest("gameName1"));
        gameService.createGame(authTok, new CreateGameRequest("gameName2"));
        gameService.createGame(authTok, new CreateGameRequest("gameName3"));
        ListGameRequest listReq = new ListGameRequest(authTok);
        ListGameResult result = gameService.listGame(listReq);
        System.out.println(result);
        assertNotNull(result);
        assertEquals(3, result.games().size());
    }

    @Test
    void listGameException() {
        Exception ex= assertThrows(Exception.class, () -> {
            gameService.listGame(new ListGameRequest("invalid"));
        });
        System.out.println(ex.getMessage());
    }
}