package com.darkfactory.education.identityaccess.auth;

public class InvalidMfaCodeException extends RuntimeException {
    public InvalidMfaCodeException() {
        super("Invalid MFA code.");
    }
}

