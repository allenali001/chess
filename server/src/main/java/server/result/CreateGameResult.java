package server.result;

public record CreateGameResult (int gameID, String message){
}

/*
Success response	[200] { "gameID": 1234 }
Failure response	[400] { "message": "Error: bad request" }
Failure response	[401] { "message": "Error: unauthorized" }
Failure response	[500] { "message": "Error: (description of error)" }
 */