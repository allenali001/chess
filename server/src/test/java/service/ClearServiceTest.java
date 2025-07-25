package service;

import dataaccess.daos.AuthDAO;
import dataaccess.memoryClasses.AuthDaoMemory;
import dataaccess.memoryClasses.GameDaoMemory;
import dataaccess.memoryClasses.UserDaoMemory;
import models.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
private AuthDAO authDAO;
private UserDaoMemory userDaoMemory;
private GameDaoMemory gameDaoMemory;
private ClearService clearService;
    @BeforeEach
    void setUp() {
        authDAO= new AuthDaoMemory();
        userDaoMemory =new UserDaoMemory();
        gameDaoMemory =new GameDaoMemory();
        clearService=new ClearService(userDaoMemory,authDAO, gameDaoMemory);
    }

    @Test
    void clearPositive() throws Exception{
        authDAO.createAuth("username");
        userDaoMemory.createUser(new UserData("username", "password", "email"));
        gameDaoMemory.createGame("gameName");
        System.out.println(gameDaoMemory);
        clearService.clear();
        assertNull(authDAO.getAuth("1234"));
        assertNull(userDaoMemory.getUser("username"));
        System.out.println(authDAO.getAuth("1234"));
    }
}