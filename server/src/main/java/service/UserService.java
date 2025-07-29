package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import models.AuthData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.exceptions.*;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
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
                registerRequest.email().isBlank()) {
            throw new MissingParameterException("Error: Missing a parameter");
        }
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData userdata = new UserData(registerRequest.username(), registerRequest.email(),hashedPassword);
        userDAO.createUser(userdata);
        AuthData auth;
        auth = authDAO.createAuth(userdata.username());
        return new RegisterResult(userdata.username(), auth.authToken(), null);
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
        if (!BCrypt.checkpw(loginRequest.password(), username1.password())) {
            throw new WrongPassWordException("Error: Incorrect Password");
        }
            AuthData authData;
        authData = authDAO.createAuth(username1.username());
        return new LoginResult(username1.username(), authData.authToken(), null);
    }
    public void logout(String authToken) throws
            IncorrectAuthTokenException, DataAccessException {
        var auth = authDAO.getAuth(authToken);
        if(auth==null){
            throw new IncorrectAuthTokenException("Error: invalid auth token");
        }
        authDAO.deleteAuth(authToken);

    }
}
