package org.example.exceptions;

public class ReservaSinucaException extends Exception {

    public ReservaSinucaException(String message) {
        super(message);
    }

    public ReservaSinucaException(String message, Throwable cause) {
        super(message, cause);
    }
}