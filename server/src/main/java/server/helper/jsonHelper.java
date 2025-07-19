package server.helper;

import com.google.gson.Gson;
import spark.Response;
import spark.Request;

public class jsonHelper {
    private static final Gson gson = new Gson();
    public static <S> S fromJson(Request req, Class<S> classNew){
        return gson.fromJson(req.body(),classNew);
    }
    public static String toJson(Response res, int status, Object body) {
        res.status(status);
        res.type("application/json");
        return gson.toJson(body);
    }
}
