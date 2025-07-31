package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String facadeUrl;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facadeUrl = "http://localhost:" + port;
        facade = new ServerFacade(facadeUrl);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        String username = "userPass" +  System.currentTimeMillis();
        var request = new RegisterRequest(username, "password", "email");
        var result = facade.register(request);
        assertEquals(username, result.username());
    }

    @Test
    public void registerFailure() {
        String username = "userfail"+  System.currentTimeMillis();
        var request = new RegisterRequest(username, null, "email");
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(request);
        });
    }

    @Test
    public void loginSuccess() throws ResponseException {
        var username = "LoginSuccess" + System.currentTimeMillis();
        facade.register(new RegisterRequest(username, "password", "email"));
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest(username, "password"));
        });
    }

    @Test
    public void loginFailure() throws ResponseException {
        var username = "LoginFail" + System.currentTimeMillis();
        facade.register(new RegisterRequest(username, "password", "email"));
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest(username, null));
        });
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        var username = "LogoutSuccess" + System.currentTimeMillis();
        var reg = facade.register(new RegisterRequest(username, "password", "email"));
        var result = facade.logout(reg.authToken());
        assertNotNull(result);
    }

    @Test
    public void logoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout(null);
        });
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        var username = "CreateSuccess" + System.currentTimeMillis();
        facade.register(new RegisterRequest(username, "password", "email"));
        var result = facade.createGame(new request.CreateGameRequest("1234"));
        assertTrue(result.gameID() >=1);
    }

    @Test
    public void createGameFailure() {
        var facadefail = new ServerFacade(facadeUrl);
        Assertions.assertThrows(ResponseException.class, () -> {
            facadefail.createGame(new request.CreateGameRequest("null"));
        });
    }

    @Test
    public void listGameSuccess() throws ResponseException {
        var username = "ListSuccess" + System.currentTimeMillis();
        var regResult = facade.register(new RegisterRequest(username, "password", "email"));
        String token = regResult.authToken();
        facade.createGame(new request.CreateGameRequest("1234"));
        var result = facade.listGame(token);
        assertFalse(result.games().isEmpty());
    }

    @Test
    public void listGameFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGame(null);
        });
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        var username = "JoinSuccess" + System.currentTimeMillis();
        var regResult = facade.register(new RegisterRequest(username, "password", "email"));
        String token = regResult.authToken();
        var created = facade.createGame(new request.CreateGameRequest("1234"));
        var request = new request.JoinGameRequest(token, created.gameID(), "WHITE");
        var result = facade.joinGame(request);
        assertNotNull(result);
    }

    @Test
    public void joinGameFailure() throws ResponseException {
        var username = "JoingFailure" + System.currentTimeMillis();
        var regResult = facade.register(new RegisterRequest(username, "password", "email"));
        var request = new request.JoinGameRequest(null, 1234, "WHITE");
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame(request);
        });
    }
}
