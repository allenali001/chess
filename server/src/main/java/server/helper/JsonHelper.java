package server.helper;

import com.google.gson.Gson;
import spark.Response;
import spark.Request;

public class JsonHelper {
    private static final Gson GSON = new Gson();
    public static <S> S fromJson(Request req, Class<S> classNew){
        return GSON.fromJson(req.body(),classNew);
    }
    public static String toJson(Response res, int status, Object body) {
        res.status(status);
        res.type("application/json");
        return GSON.toJson(body);
    }
}
