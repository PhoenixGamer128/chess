package dataaccess;

public class ResponseException extends RuntimeException {

    public enum Code {
        AlreadyTaken
    }

    Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code code() {
        return code;
    }
}
