package dataaccess;
import models.UserData;

import java.util.ArrayList;
import java.util.List;


public class UserDaoMemory implements UserDAO {
    private final List<UserData> userslist = new ArrayList<>();

    public UserData getUser(String username) throws DataAccessException {
        try {
            if (username != null) {
                for (UserData u : userslist) {
                    if (u.getUsername().equals(username)) {
                        return u;
                    }
                }
            }
            return null;
        }catch(Exception Ex){
            throw new DataAccessException("Get user data could not be accessed", Ex);
    }
}
    public void createUser(UserData u) throws DataAccessException{
        try {
                userslist.add(u);
        }catch(Exception Ex){
            throw new DataAccessException("Create user data could not be accessed", Ex);
        }
    }
    public void clear()throws DataAccessException {
        try {
            userslist.clear();
        }catch(Exception Ex){
            throw new DataAccessException("Clear data could not be accessed");
        }
    }
}

