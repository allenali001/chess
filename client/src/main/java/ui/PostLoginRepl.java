package ui;

import exception.ResponseException;
import models.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLoginRepl implements NotificationHandler {
    private final ChessClient client;
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final String serverUrl;

    private final Map<Integer, GameData> gameMap = new HashMap<>();

    public PostLoginRepl(ChessClient client, ServerFacade server, String authToken, String username) {
        this.client = client;
        this.server = server;
        this.authToken = authToken;
        this.username=username;
        this.serverUrl = client.getServerUrl();
    }

    public void run() {
        System.out.println("Type 'help' for options.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printPrompt();
            String input = scanner.nextLine().trim();
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try {
                switch (cmd) {
                    case "logout" -> {
                        server.logout(authToken);
                        System.out.println("You have been logged out");
                        client.transitionToPrelogin();
                        return;
                    }
                    case "create" -> doCreateGame(params);
                    case "list" -> doListGames();
                    case "join" -> doPlayGame(params);
                    case "observe"-> doObserveGame(params);
                    case "help" -> System.out.println(help());
                    case "quit"-> {
                        System.out.println("Quit game");
                        return;
                    }
                    default -> System.out.print("Command not recognized. " +
                            "Check spelling and try again, or type 'help' for options.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void doCreateGame(String[] params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            var result = server.createGame(new CreateGameRequest(gameName));
            System.out.println("Game created. Game name: " + gameName + ", GameID: " + result.gameID());
        } else {
            System.out.println("GameName required");
        }
    }

    private void doPlayGame(String[] params) throws ResponseException {
        if (gameMap.isEmpty()) {
            System.out.println("Run 'list' first");
            return;
        }
        if (params.length >= 2) {
            int gameNumber;
            gameNumber = Integer.parseInt(params[0]);
            String color = params[1].toLowerCase();
            if (!color.equals("white") && !color.equals("black")) {
                System.out.println("Must enter a valid color: White or Black");
                return;
            }
            var game = gameMap.get(gameNumber);
            if (game == null) {
                System.out.println("Invalid Game number entered");
                return;
            }
            String playerColor = color.equals("observe") ? null : color;
            server.joinGame(new JoinGameRequest(authToken, game.getGameID(), playerColor));
            System.out.println("Playing game as " + color + ":" + game.getGameName() + "\n");
            var ws = new WebSocketFacade(serverUrl,this);
            client.transitionToGameplay(game.getGameID(), color,authToken,username,ws);
        }
    }
    public void doObserveGame(String[] params) throws ResponseException{
        if (gameMap.isEmpty()){
            System.out.println("Run 'list' first");
            return;
        }
        if (params.length >=1){
            int gameNumber = Integer.parseInt(params[0]);
            var game = gameMap.get(gameNumber);
            System.out.print("Observing game: "+ game.getGameName() + "\n");
            var ws = new WebSocketFacade(serverUrl,this);
            client.transitionToGameplay(game.getGameID(),"OBSERVER",authToken,username,ws);
        }else{
            System.out.println("GameNumber required");
        }
    }
    public void doListGames() throws ResponseException{
        gameMap.clear();
        var result = server.listGame(authToken);
        var games = result.games();
        int index = 1;
        for (var game:games){
            gameMap.put(index,game);
            System.out.printf("%d. %s | White: %s | Black: %s%n",
                    index++, game.getGameName(),
                    game.getWhiteUsername()!=null?game.getWhiteUsername() : "-",
                    game.getBlackUsername()!=null ? game.getBlackUsername(): "-");
        }
    }
    public String help() {

        return """
                - logout - goes back to login page
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE][BLACK] - a game
                - observe <ID> - a game
                - quit - playing chess
                - help - with possible commands
                """;
    }

    @Override
    public void notify (ServerMessage notification){
        System.out.println(notification.getMessage());
        printPrompt();
    }
    public void printPrompt(){
        System.out.print("\n" + "[LOGGED_IN] >>> ");
    }
}

