package dataaccess;
import models.UserData;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    private final List<UserData> userslist = new ArrayList<>();

    void insertUser(UserData u) throws DataAccessException{
        if (getUser(u.getUsername())!=null){
            throw new DataAccessException("Error: Username already taken");
        }
        createUser(u);
    }

    public UserData getUser(String username) {
        if (username!=null){
            for (UserData u:userslist){
                if (u.getUsername().equals(username)){
                    return u;
                }
            }
        }
        return null;
    }
    public void createUser(UserData u){
        userslist.add(u);
    }
    public void clear(){
        userslist.clear();
    }
}


/*createUser: Create a new user.
        getUser: Retrieve a user with the given username.
 clear
 */