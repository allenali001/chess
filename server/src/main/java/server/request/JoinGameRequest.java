package server.request;

public record JoinGameRequest(String authToken, int gameID, String playerColor) { }
