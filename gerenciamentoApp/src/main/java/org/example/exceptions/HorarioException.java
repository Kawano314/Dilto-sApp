package org.example.exceptions;

public class HorarioException extends Exception {
    public HorarioException(String message) {
        super(message);
    }

    public HorarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
