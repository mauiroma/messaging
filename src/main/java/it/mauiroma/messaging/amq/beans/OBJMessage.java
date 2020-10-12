package it.mauiroma.messaging.amq.beans;

import java.io.Serializable;

public class OBJMessage implements Serializable {
    public OBJMessage(String messageText) {
        message=messageText;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    private String message;
}
