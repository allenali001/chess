package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;
import server.result.RegisterResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.IncorrectAuthTokenException;
import service.exceptions.MissingParameterException;

import static org.junit.jupiter.api.Assertions.*;
class UserServiceTest {
    private AuthDAO authDAO;
    private UserService userService;
    @BeforeEach
    void setUp() {
        UserDAO userDAO = new UserDAO();
        authDAO=new AuthDAO();
        userService=new UserService(userDAO,authDAO);
    }

    @Test
    void registerPositive() throws Exception{
        RegisterRequest registerRequest= new RegisterRequest("username","password", "email");
        RegisterResult registerResult = userService.register(registerRequest);
        System.out.println(registerResult);
        assertNotNull(registerResult);
        assertNotNull(registerResult.authToken());
        assertEquals("username", registerResult.username());
    }

    @Test
    void registerException(){
        Exception ex= assertThrows(Exception.class,()->{
            userService.register(new RegisterRequest(null,null,"email"));
        });
        System.out.println(Ex.getMessage());
    }


    @Test
    void loginPositive()throws Exception {
        userService.register(new RegisterRequest("username","password","email"));
        LoginRequest loginRequest = new LoginRequest("username","password");
        LoginResult loginResult = userService.login(loginRequest);
        System.out.println(loginResult);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        assertEquals("username", loginResult.username());
    }

    @Test
    void loginException(){
        Exception ex= assertThrows(Exception.class,()->{
            userService.login(new LoginRequest("username",null));
        });
        System.out.println(Ex.getMessage());
    }


    @Test
    void logoutPositive() throws Exception{
        userService.register(new RegisterRequest("username","password","email"));
        LoginRequest loginRequest = new LoginRequest("username","password");
        LoginResult loginResult = userService.login(loginRequest);
        System.out.println(loginResult);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        assertEquals("username", loginResult.username());
        userService.logout(loginResult.authToken());
        assertNull(authDAO.getAuth(loginResult.authToken()));
    }

     @Test
    void logoutException(){
         Exception ex= assertThrows(Exception.class,()->{
            userService.logout("invalid");
        });
         System.out.println(Ex.getMessage());
     }
 }