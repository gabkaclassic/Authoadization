package org.example.handling.exceptions;

import java.security.InvalidKeyException;

public class InvalidSecretKeyException extends InvalidKeyException {

    public InvalidSecretKeyException() {

        super("Interaction key is not valid");
    }
}
