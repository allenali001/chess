package server.request;

public record CreateGameRequest(String gameName, String authToken) { }
