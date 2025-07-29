package result;

import models.GameData;

import java.util.List;

public record ListGameResult (List<GameData> games, String message){
}
