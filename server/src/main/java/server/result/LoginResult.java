package server.result;

public record LoginResult(String username, String authToken, String message) {
}

/*
Success response	[200] { "username":"", "authToken":"" }
Failure response	[400] { "message": "Error: bad request" }
Failure response	[401] { "message": "Error: unauthorized" }
Failure response	[500] { "message": "Error: (description of error)" }
 */