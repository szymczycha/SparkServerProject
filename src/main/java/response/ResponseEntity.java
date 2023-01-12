package response;

import com.google.gson.JsonElement;

public class ResponseEntity {
    private ResponseStatus status; // SUCCESS, ERROR, INNE
    private String message; // komunikat tekstowy
    private JsonElement data; // dane json pobrane z mapy

    public ResponseEntity(ResponseStatus status, String message, JsonElement data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseEntity(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}
