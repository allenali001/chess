package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import models.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
private AuthDAO authDAO;
private UserDAO userDAO;
private GameDAO gameDAO;
private ClearService clearService;
    @BeforeEach
    void setUp() {
        authDAO= new AuthDAO();
        userDAO=new UserDAO();
        gameDAO=new GameDAO();
        clearService=new ClearService(userDAO,authDAO,gameDAO);
    }

    @Test
    void clearPositive() throws Exception{
        authDAO.createAuth("username");
        userDAO.createUser(new UserData("username", "password", "email"));
        gameDAO.createGame("gameName");
        System.out.println(gameDAO);
        clearService.clear();
        assertNull(authDAO.getAuth("1234"));
        assertNull(userDAO.getUser("username"));
        System.out.println(authDAO.getAuth("1234"));
    }
}