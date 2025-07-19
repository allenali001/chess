package models;

public class AuthData {
    private final String authToken;
    private final String username;
    public AuthData(String username, String authToken){
        this.authToken=authToken;
        this.username=username;
    }
    public String getAuthToken() {
        return authToken;
    }
    public String getUsername() {
        return username;
    }
}



