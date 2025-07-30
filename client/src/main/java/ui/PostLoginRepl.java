package ui;

import exception.ResponseException;
import models.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import webSocketMessages.ServerMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLoginRepl implements NotificationHandler {
    private final ChessClient client;
    private final ServerFacade server;
    private final String authToken;
    private final ReplHelper helper = new ReplHelper();

    private final Map<Integer, GameData> gameMap = new HashMap<>();

    public PostLoginRepl(ChessClient client, ServerFacade server, String authToken, String username) {
        this.client = client;
        this.server = server;
        this.authToken = authToken;
    }

    public void run() {
        System.out.println("You are now logged in. Type 'help'.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            helper.printPrompt();
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
                    case "creategame" -> doCreateGame(params);
                    case "listgames" -> doListGames();
                    case "playgame" -> doPlayGame(params);
                    case "observegame" -> doObserveGame(params);
                    case "help" -> System.out.println(help());
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
            System.out.println("Run 'listgames' first");
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
            System.out.println("Playing game as " + color + ":" + game.getGameName());
            client.transitionToGameplay(game.getGameID(), color);
        }
    }
    public void doObserveGame(String[] params) throws ResponseException{
        if (gameMap.isEmpty()){
            System.out.println("Run 'listgames' first");
            return;
        }
        if (params.length >=1){
            int gameNumber = Integer.parseInt(params[0]);
            var game = gameMap.get(gameNumber);
            System.out.print("Observing game: "+ game.getGameName());
            client.transitionToGameplay(game.getGameID(),"OBSERVER");
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
            System.out.printf("%d. %s | White: %s | Black: %s%n", index++, game.getGameName(), game.getWhiteUsername()!=null?game.getWhiteUsername() : "-",
                    game.getBlackUsername()!=null ? game.getBlackUsername(): "-");
        }
    }
    public String help() {

        return """
                - logout,
                - creategame <gameName>,
                - listgames, 
                - playgame <gameID> <playercolor>,
                - observegame <gameID>
                - quit
                """;
    }

    @Override
    public void notify (ServerMessage notification){
        System.out.println(notification.message());
        helper.printPrompt();
    }

}

