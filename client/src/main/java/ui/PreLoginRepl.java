package ui;

import server.ServerFacade;
import ui.websocket.NotificationHandler;
import webSocketMessages.Notification;
import webSocketMessages.ServerMessage;

import java.util.Scanner;
import static client.EscapeSequences.*;

public class PreLoginRepl implements NotificationHandler {
    private final ChessClient client;

    public PreLoginRepl(String serverUrl){
        client = new ChessClient(serverUrl);
    }
    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        printHelp();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            try{
                result = client.eval(line);
                System.out.print(BLUE + result);
            }catch (Throwable ex){
                var msg = ex.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

    }
    private void printPrompt(){
        System.out.print("\n" + RESET + ">>>" + GREEN);
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }
}


/*
register (username,passwrod,email)
login (username passwrod)

after login -> postloginrepl
 */