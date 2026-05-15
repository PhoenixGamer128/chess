package dataaccess;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends RuntimeException {

    public enum Code {
        AlreadyTaken,
        DataAccess,
        BadRequest
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
        return new Gson().toJson(Map.of("message", getMessage()));
    }


    public int toHttpStatusCode(ResponseException ex) {
        return switch (ex.code()) {
            case Code.AlreadyTaken -> 401;
            case Code.DataAccess -> 500;
            case Code.BadRequest -> 400;
        };
    }
}
