package server.result;

import models.GameData;

import java.util.List;

public record ListGameResult (List<GameData> games, String message){
}

/*
Success response	[200] { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
Failure response	[401] { "message": "Error: unauthorized" }
Failure response	[500] { "message": "Error: (description of error)" }
 */