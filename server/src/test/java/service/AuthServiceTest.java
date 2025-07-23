package service;

import dataaccess.AuthDAO;
import dataaccess.AuthDaoMemory;
import models.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthDAO authDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authDAO = new AuthDaoMemory();
        authService = new AuthService(authDAO);
    }

    @Test
    void valAuthTokenPositive() throws Exception{
        AuthData user = authDAO.createAuth("username");
        AuthData res = authService.valAuthToken(user.getAuthToken());
        System.out.println(res);
        assertNotNull(res);
        assertEquals("username", res.getUsername());
    }

    @Test
    void valAuthTokenException() {
        Exception ex= assertThrows(Exception.class, () -> {
            authService.valAuthToken("authtok");
        });
        System.out.println(ex.getMessage());
    }
}