package dataaccess;
import models.AuthData;
import models.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AuthDAO {
    private final List<AuthData> authdatlist = new ArrayList<>();
    public AuthData createAuth(String username) {
        String tok = UUID.randomUUID().toString();
        AuthData authdat = new AuthData(username, tok);
        authdatlist.add(authdat);
        return authdat;
    }
    public AuthData getAuth(String tok) {
        for (AuthData atok : authdatlist) {
            if (tok.equals(atok.getAuthToken())) {
                return atok;
            }
        }
        return null;
    }
    public void clear(){
        authdatlist.clear();
    }

    public void deleteAuth(String authtok){
        int max = authdatlist.size();
        for (int i =0; i<max; i++){
            AuthData atok = authdatlist.get(i);
                if (atok.getAuthToken().equals(authtok)) {
                    authdatlist.remove(i);
                    break;
            }
        }
    }
}

/*clear: A method for clearing all data from the database. This is used during testing.

       createAuth: Create a new authorization.
        getAuth: Retrieve an authorization given an authToken.
deleteAuth: Delete an authorization so that it is no longer valid.
 */