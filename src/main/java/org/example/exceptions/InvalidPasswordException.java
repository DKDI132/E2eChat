package org.example.exceptions;

public class InvalidPasswordException  extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
