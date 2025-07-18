package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import models.AuthData;
import models.UserData;
import server.request.LoginRequest;
import server.request.LogoutRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;
import server.result.RegisterResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.IncorrectAuthTokenException;
import service.exceptions.NoSuchUserException;
import service.exceptions.WrongPassWordException;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();
    public RegisterResult register(RegisterRequest registerRequest)throws AlreadyTakenException {
        UserData userdata;
        userdata = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Error: Username is already taken");
        }
        userDAO.createUser(userdata);
        AuthData auth;
        auth = authDAO.createAuth(userdata.getUsername());
        return new RegisterResult(userdata.getUsername(), auth.getAuthToken(), null);
    }

    public LoginResult login(LoginRequest loginRequest) throws NoSuchUserException, WrongPassWordException {
        UserData username1;
        username1 = userDAO.getUser(loginRequest.username());
        if (username1== null){
            throw new NoSuchUserException("Error: Username does not exist");
        }
        if (!username1.getPassword().equals(loginRequest.password())){
            throw new WrongPassWordException("Error: Incorrect Password");
        }
        AuthData authData;
        authData = authDAO.createAuth(username1.getUsername());
        return new LoginResult(username1.getUsername(), authData.getAuthToken(), null);
    }
    public void logout(LogoutRequest logoutRequest) throws IncorrectAuthTokenException {
        AuthService authService;
        authService = new AuthService();
        authService.logout(logoutRequest.authToken());
    }
}
