package service;

import dataaccess.AuthDAO;
import dataaccess.AuthDaoMemory;
import dataaccess.UserDaoMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;
class UserServiceTest {
    private AuthDAO authDAO;
    private UserService userService;
    @BeforeEach
    void setUp() {
        UserDaoMemory userDaoMemory = new UserDaoMemory();
        authDAO = new AuthDaoMemory();
        userService = new UserService(userDaoMemory, authDAO);
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
        Exception ex= assertThrows(Exception.class,()-> userService.register(new RegisterRequest(null,null,"email")));
        System.out.println(ex.getMessage());
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
        Exception ex= assertThrows(Exception.class,()-> userService.login(new LoginRequest("username",null)));
        System.out.println(ex.getMessage());
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
         Exception ex= assertThrows(Exception.class,()-> userService.logout("invalid"));
         System.out.println(ex.getMessage());
     }
 }