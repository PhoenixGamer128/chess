package dataaccess;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends DataAccessException {

    public enum Code {
        AlreadyTaken,
        DataAccess,
        BadRequest,
        Unauthorized,
        NotFound,
    }

    Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code code() {
        return code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int toHttpStatusCode(ResponseException ex) {
        return switch (ex.code()) {
            case Code.AlreadyTaken -> 403;
            case Code.DataAccess -> 500;
            case Code.BadRequest -> 400;
            case Code.Unauthorized -> 401;
            case Code.NotFound -> 404;
        };
    }

    public Code fromHttpStatusCode(int code) {
        return switch (code) {
            case 500 -> Code.DataAccess;
            case 400 -> Code.BadRequest;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + code);
        };
    }
}
