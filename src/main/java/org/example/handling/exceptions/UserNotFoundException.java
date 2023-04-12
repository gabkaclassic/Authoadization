package org.example.handling.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(Exception e) {
        super("User with this key is not found");
        setStackTrace(e.getStackTrace());
    }

    public String getMessage() {

        return "User with this key is not found";
    }
}
