package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.AuthData;
import models.UserData;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;
import server.result.RegisterResult;
import service.exceptions.*;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO=userDAO;
        this.authDAO=authDAO;
    }
    public RegisterResult register(RegisterRequest registerRequest)throws
            AlreadyTakenException, MissingParameterException , DataAccessException {
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Error: Username is already taken");
        }if (registerRequest.username()==null ||
                registerRequest.username().isBlank() ||
                registerRequest.password()==null ||
                registerRequest.password().isBlank() ||
                registerRequest.email()==null ||
                registerRequest.email().isBlank()){
            throw new MissingParameterException("Error: Missing a parameter");
        }
        UserData userdata = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(userdata);
        AuthData auth;
        auth = authDAO.createAuth(userdata.getUsername());
        return new RegisterResult(userdata.getUsername(), auth.getAuthToken(), null);
    }

    public LoginResult login(LoginRequest loginRequest) throws
            NoSuchUserException, WrongPassWordException,
            MissingParameterException, DataAccessException{
        if (loginRequest.username() == null ||
                loginRequest.username().isBlank() ||
                loginRequest.password()==null ||
                loginRequest.password().isBlank()) {
            throw new MissingParameterException("Error: Missing a parameter");
        }
        UserData username1;
        username1 = userDAO.getUser(loginRequest.username());
        if (username1 == null) {
            throw new NoSuchUserException("Error: Username does not exist");
        }
        if (!username1.getPassword().equals(loginRequest.password())) {
            throw new WrongPassWordException("Error: Incorrect Password");
        }
            AuthData authData;
        authData = authDAO.createAuth(username1.getUsername());
        return new LoginResult(username1.getUsername(), authData.getAuthToken(), null);
    }
    public void logout(String authToken) throws
            IncorrectAuthTokenException, DataAccessException {
        authDAO.deleteAuth(authToken);
    }
}
