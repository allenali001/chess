package ui;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import websocketmesssages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;


public class PreLoginRepl implements NotificationHandler {
    private final ChessClient client;
    private final ServerFacade server;
    private final ReplHelper helper = new ReplHelper();

    public PreLoginRepl(ChessClient client, ServerFacade server) {
        this.client = client;
        this.server = server;
    }

    public void run() {
        System.out.println("Welcome to Chess. Sign in to start or type 'help'.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            helper.printPrompt();
            String input = scanner.nextLine().trim();
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try {
                switch (cmd) {
                    case "quit" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    case "login" -> doLogin(params);
                    case "register" -> doRegister(params);
                    case "help" -> System.out.println(help());
                    default -> System.out.print("Command not recognized. " +
                            "Check spelling and try again, or type 'help' for options.");
                }
            } catch (Exception ex){
                System.out.println("Error: "+ex.getMessage());
            }
        }
    }



    private void doLogin(String[] params) throws ResponseException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            var result = server.login(new LoginRequest(username, password));
            System.out.println("You logged in as " + username);
            client.transitionToPostLogin(result.authToken(), params[1]);
        }else{
            System.out.println("Username and password required");
        }
    }
    private void doRegister(String[] params) throws ResponseException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var result = server.register(new RegisterRequest(username, password, email));
            System.out.println("Registered and logged in as " + username);
            client.transitionToPostLogin(result.authToken(), username);
        }else{
            System.out.println("Username, password, and email required");
        }
    }
    public String help() {

        return """
                - login <username> <password>
                - register <username> <password> <email>
                - quit
                """;
    }

    @Override
    public void notify (ServerMessage notification){
        System.out.println(notification.message());
        helper.printPrompt();
    }
}

