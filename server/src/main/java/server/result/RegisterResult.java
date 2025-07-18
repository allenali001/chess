package server.result;

public record RegisterResult(String username, String authToken, String message){}

/*
Success response	[200] { "username":"", "authToken":"" }
Failure response	[400] { "message": "Error: bad request" }
Failure response	[403] { "message": "Error: already taken" }
Failure response	[500] { "message": "Error: (description of error)" }

 */