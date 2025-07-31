package ui;

import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;
    public ChessClient(String serverUrl){
        this.server=new ServerFacade(serverUrl);
    }
    public void run(){
    new PreLoginRepl(this,server).run();
    }
    public void transitionToPostLogin(String authToken, String username){
        new PostLoginRepl(this,server, authToken, username).run();
    }
    public void transitionToGameplay(int gameID, String role){
        new GamePlayRepl(this,gameID,role).run();
    }
    public void transitionToPrelogin(){
        run();
    }
    public ServerFacade getServer(){
        return server;
    }
}