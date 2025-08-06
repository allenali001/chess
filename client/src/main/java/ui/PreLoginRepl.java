package ui;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;


public class PreLoginRepl implements NotificationHandler {
    private final ChessClient client;
    private final ServerFacade server;

    public PreLoginRepl(ChessClient client, ServerFacade server) {
        this.client = client;
        this.server = server;
    }

    public void run() {
        System.out.println("Welcome to Chess. Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printPrompt();
            String input = scanner.nextLine().trim();
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try {
                switch (cmd) {
                    case "quit" -> {
                        System.out.println("Quit game");
                        return;
                    }
                    case "login" -> doLogin(params);
                    case "register" -> doRegister(params);
                    case "help" -> System.out.println(help());
                    default -> System.out.print("Command not recognized. " +
                            "Check spelling and try again, or type 'help' for options.");
                }
            } catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public void printPrompt(){
        System.out.print("\n" + "[LOGGED_OUT] >>> ");
    }

    private void doLogin(String[] params) throws ResponseException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            var result = server.login(new LoginRequest(username, password));
            System.out.println("Logged in as: " + username);
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
                - login <USERNAME> <PASSWORD> - to play chess
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - quit - playing chess
                - help - with possible commands
                """;
    }

    @Override
    public void notify (ServerMessage notification){
        System.out.println(notification.getMessage());
        printPrompt();
    }
}

