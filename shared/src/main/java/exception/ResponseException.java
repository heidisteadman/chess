package exception;

import com.google.gson.Gson;
import java.util.Map;

public class ResponseException extends Exception {
    final private int code;
    final private String message;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public int getCode() {
        return code;
    }

    public String message() {
        return message;
    }
}
